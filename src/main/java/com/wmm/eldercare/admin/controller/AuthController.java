package com.wmm.eldercare.admin.controller;

import com.wmm.eldercare.core.common.BusinessException;
import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.mapper.SmsCodeMapper;

import com.wmm.eldercare.core.pojo.RefreshToken;
import com.wmm.eldercare.core.pojo.SmsCode;
import com.wmm.eldercare.core.pojo.User;
import com.wmm.eldercare.core.dto.*;
import com.wmm.eldercare.core.service.RefreshTokenService;
import com.wmm.eldercare.core.service.UserService;
import com.wmm.eldercare.core.util.JwtUtil;
import com.wmm.eldercare.core.util.SmsUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final SmsUtil smsUtil;
    private final SmsCodeMapper smsCodeMapper;

    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody LoginDTO loginDTO) {
        String phone = loginDTO.getPhone();
        String password = loginDTO.getPassword();
        //1. 校验手机号和密码
        User user = userService.findByPhoneWithPassword(phone);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(400, "手机号或密码错误");
        }
        //2. 生成 accessToken
        String accessToken = jwtUtil.generateAccessToken(user.getId(), phone, user.getRole());
        //3. 生成 refreshToken 并保存到数据库
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        RefreshToken rt = new RefreshToken();
        rt.setToken(refreshToken);
        rt.setUserId(user.getId());
        rt.setCreateTime(LocalDateTime.now());
        rt.setExpireTime(LocalDateTime.now().plusDays(jwtUtil.getRefreshTokenExpireDays()));
        refreshTokenService.saveRefreshToken(rt);
        //4. 返回两个 Token
        return Result.success(Map.of("accessToken", accessToken, "refreshToken", refreshToken));
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterDTO registerDTO) {
        String phone = registerDTO.getPhone();
        String password = registerDTO.getPassword();
        String realName = registerDTO.getRealName();
        //1. 手机号是否已注册
        User user = userService.findByPhone(phone);
        if (user != null) {
            throw new BusinessException(400, "手机号已注册");
        }
        //2. 密码加密
        String encodePassword = passwordEncoder.encode(password);
        User newUser = new User();
        newUser.setPhone(phone);
        newUser.setPassword(encodePassword);
        newUser.setRealName(realName);
        //3. 校验验证码
        String smsCode = registerDTO.getSmsCode();
        SmsCode smsCodeEntity = smsCodeMapper.findByPhone(phone);
        log.info("注册验证码校验: phone={}, 前端传的是[{}], 数据库查得[{}]", phone, smsCode, smsCodeEntity != null ? smsCodeEntity.getCode() : "null");
        //3.1 没发过验证码 → 直接拒绝（防止空指针）
        if (smsCodeEntity == null) {
            throw new BusinessException(400, "请先获取验证码");
        }
        //3.2 比对验证码是否正确
        if (smsCode == null || !smsCode.equals(smsCodeEntity.getCode())) {
            throw new BusinessException(400, "验证码错误");
        }
        //4. 校验验证码是否过期
        if (smsCodeEntity.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(400, "验证码已过期");
        }
        //5. 校验验证码是否已使用
        if (smsCodeEntity.getUsed() == 1) {
            throw new BusinessException(400, "验证码已使用");
        }
        //6. 更新验证码为已使用
        smsCodeMapper.updateUsed(smsCodeEntity.getId());
        //7. 保存用户
        userService.addUser(newUser);
        return Result.success("注册成功，请登录");
    }

    @PostMapping("/refresh")
    public Result<Map<String, String>> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        String refreshToken = refreshTokenDTO.getRefreshToken();
        //1. 查数据库，校验 refreshToken
        RefreshToken rt = refreshTokenService.findByToken(refreshToken);
        if (rt == null) {
            throw new BusinessException(400, "刷新令牌不存在");
        }
        if (rt.getExpireTime().isBefore(LocalDateTime.now())) {
            refreshTokenService.deleteByToken(refreshToken);
            throw new BusinessException(400, "刷新令牌已过期，请重新登录");
        }
        //2. 查用户信息
        User user = userService.findUserById(rt.getUserId());
        //3. 删旧 Token，生成新 Token
        refreshTokenService.deleteByToken(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getPhone(), user.getRole());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());
        //4. 保存新 refreshToken
        RefreshToken newRt = new RefreshToken();
        newRt.setToken(newRefreshToken);
        newRt.setUserId(user.getId());
        newRt.setCreateTime(LocalDateTime.now());
        newRt.setExpireTime(LocalDateTime.now().plusDays(jwtUtil.getRefreshTokenExpireDays()));
        refreshTokenService.saveRefreshToken(newRt);
        //5. 返回新 Token
        return Result.success(Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken));
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        refreshTokenService.deleteByUserId(userId);
        return Result.success("登出成功");
    }

    @PostMapping("/send-code")
    public Result<String> sendCode(@RequestBody SmsCodeDTO smsCodeDTO) {
        // 1. 校验手机号格式(简单版:11位、1开头)
        String phone = smsCodeDTO.getPhone();
        if (phone == null || !phone.matches("^1\\d{10}$")) {
            throw new BusinessException(400, "手机号格式不正确");
        }
        //2. 生成验证码
        String smsCode = smsUtil.getSmsCode(phone);
        //3. 保存验证码到数据库
        SmsCode smsCodeEntity = new SmsCode();   // 造一张新卡
        smsCodeEntity.setPhone(phone);
        smsCodeEntity.setCode(smsCode);
        smsCodeEntity.setExpireTime(LocalDateTime.now().plusMinutes(5));
        smsCodeEntity.setUsed(0);
        smsCodeEntity.setCreateTime(LocalDateTime.now());
        smsCodeMapper.insertSmsCode(smsCodeEntity);   // 存进数据库
        //4. 返回验证码
        return Result.success(smsCode);
    }

    @PostMapping("/reset-password")
        @Transactional
        public Result<String> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO){
            //1.验证手机号是否存在
            User user = userService.findByPhone(resetPasswordDTO.getPhone());
            if(user == null){
                throw new BusinessException(400, "手机号不存在");
            }
            //2.校验两次密码是否相同
            if(!resetPasswordDTO.getPassword().equals(resetPasswordDTO.getConfirmPassword())){
                throw new BusinessException(400, "两次密码不相同");
            }
            //3.验证码是否正确
            SmsCode smsCodeEntity = smsCodeMapper.findByPhone(resetPasswordDTO.getPhone());
            if(smsCodeEntity == null){
                throw new BusinessException(400, "请先获取验证码");
            }
            if(!smsCodeEntity.getCode().equals(resetPasswordDTO.getCode())){
                throw new BusinessException(400, "验证码错误");
            }
            if (smsCodeEntity.getExpireTime().isBefore(LocalDateTime.now())) {
                throw new BusinessException(400, "验证码已过期");
            }
            if (smsCodeEntity.getUsed() == 1) {
                throw new BusinessException(400, "验证码已使用");
            }
            //4.更新密码（先改密码，成功后再作废验证码；失败会回滚，验证码不会被"吃掉"）
            user.setPassword(passwordEncoder.encode(resetPasswordDTO.getPassword()));
            int rows = userService.updateUser(user.getId(), user);
            if(rows == 0){
                throw new BusinessException(400, "密码重置失败");
            }
            //5. 验证码使用成功后才标记已使用
            smsCodeMapper.updateUsed(smsCodeEntity.getId());
            //6. 删除所有刷新令牌，强制用户重新登录
            refreshTokenService.deleteByUserId(user.getId());
            return Result.success("密码重置成功，请重新登录");
        }
    }