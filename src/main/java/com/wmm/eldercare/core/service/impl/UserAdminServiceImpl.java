package com.wmm.eldercare.core.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wmm.eldercare.core.common.BusinessException;
import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.mapper.UserMapper;
import com.wmm.eldercare.core.pojo.User;
import com.wmm.eldercare.core.service.UserAdminService;
import com.wmm.eldercare.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {

    private final UserMapper userMapper;
    private final UserService userService;

    @Override
    public PageResult<User> listUsers(String keyword, Integer pageNum, Integer pageSize) {
        // 1. 开启分页
        PageHelper.startPage(pageNum, pageSize);
        // 2. 查询用户列表（不查密码）
        List<User> list = userMapper.findAll(keyword);
        // 3. 封装分页结果
        PageInfo<User> pageInfo = new PageInfo<>(list);
        return new PageResult<>(
                pageInfo.getTotal(),
                pageInfo.getList(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getPages()
        );
    }

    @Override
    public User getUser(Long id) {
        User user = userService.findUserById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        User userWithPassword = userMapper.findByIdWithPassword(id);
        user.setMemberLevel(userWithPassword != null ? userWithPassword.getMemberLevel() : user.getMemberLevel());
        return user;
    }

    @Override
    public void addUser(User user) {
        // 1. 校验手机号非空
        if (user.getPhone() == null || user.getPhone().isBlank()) {
            throw new BusinessException(400, "手机号不能为空");
        }
        // 2. 校验手机号是否已存在
        if (userService.findByPhone(user.getPhone()) != null) {
            throw new BusinessException(400, "手机号已注册");
        }
        // 3. 新增用户（UserService 负责 BCrypt 加密 etc）
        userService.addUser(user);
    }

    @Override
    public void updateUser(Long id, User user) {
        // 校验用户存在
        if (userService.findUserById(id) == null) {
            throw new BusinessException(404, "用户不存在");
        }
        userService.updateUser(id, user);
    }

    @Override
    public void updateStatus(Long id, String status) {
        if (!"ENABLED".equals(status) && !"DISABLED".equals(status)) {
            throw new BusinessException(400, "非法的状态值");
        }
        if (userService.findUserById(id) == null) {
            throw new BusinessException(404, "用户不存在");
        }
        int rows = userMapper.updateStatus(id, status);
        if (rows == 0) {
            throw new BusinessException(500, "更新状态失败");
        }
    }

    @Override
    public void deleteUser(Long id) {
        if (userService.findUserById(id) == null) {
            throw new BusinessException(404, "用户不存在");
        }
        userService.deleteUser(id);
    }

    @Override
    public void batchDeleteUsers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "参数不能为空");
        }
        userService.batchDeleteUsers(ids);
    }
}