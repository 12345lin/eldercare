# AI 智能养老社区管理系统 — 模块开发步骤详解

> 本文档记录每个模块的具体开发步骤，包括：做了什么、加了什么注解、注解有什么用
> 项目路径：`E:\DevProject\Java_Project\Web-Project\eldercare`

---

## 第一部分：认证授权模块（Auth）

### 步骤 1：创建统一响应类 Result.java

**路径：** `core/common/Result.java`

**代码：**
```java
package com.wmm.eldercare.core.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                  // 自动生成 getter/setter/toString/equals/hashCode
@NoArgsConstructor     // 生成无参构造方法
@AllArgsConstructor    // 生成全参构造方法
public class Result<T> {
    private int code;      // 状态码：200成功，500失败
    private String message; // 提示信息
    private T data;        // 返回数据

    // 静态工厂方法：快速创建成功响应
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    // 静态工厂方法：快速创建失败响应
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }
}
```

**做了什么：**
- 创建了一个泛型类，所有接口统一用这个格式返回数据
- 用 Lombok 简化代码，不用手写 getter/setter

---

### 步骤 2：创建 User 实体类

**路径：** `core/pojo/User.java`

**代码：**
```java
package com.wmm.eldercare.core.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data                           // 自动生成 getter/setter/toString
public class User {
    private Long id;             // 用户 ID
    
    private String phone;        // 手机号（登录账号）
    
    private String password;     // 密码（BCrypt 加密存储）
    
    private String realName;     // 真实姓名
    
    private String gender;       // 性别：MALE/FEMALE
    
    private LocalDate birthDate; // 出生日期
    
    private BigDecimal height;   // 身高（cm），用于计算 BMI
    
    private String avatar;       // 头像 URL
    
    private String emergencyContact; // 紧急联系人电话
    
    private String memberLevel;  // 会员等级：NORMAL/SILVER/GOLD/PLATINUM/DIAMOND
    
    private Integer points;      // 积分
    
    private String status;       // 状态：ENABLED/DISABLED
    
    private String role;         // 角色：MEMBER/ADMIN
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private Integer deleted;     // 逻辑删除：0 未删除/1 已删除
}
```

**做了什么：**
- 对应数据库 `user` 表的 17 个字段
- 用 Lombok 的 `@Data` 简化代码

---

### 步骤 3：创建 UserMapper 接口

**路径：** `core/mapper/UserMapper.java`

**代码：**
```java
package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper                        // MyBatis 注解：告诉 Spring 这是一个 Mapper 接口
public interface UserMapper {
    
    // 插入用户（登录注册用）
    @Insert("INSERT INTO user (phone, password, real_name, member_level, points, status, role) " +
            "VALUES (#{phone}, #{password}, #{realName}, #{memberLevel}, #{points}, #{status}, #{role})")
    @SelectKey(                  // 查询主键：插入后获取自增 ID
        keyProperty = "id",
        before = false,
        resultType = Long.class,
        statement = "SELECT LAST_INSERT_ID()"
    )
    int insert(User user);
    
    // 根据手机号查询用户（登录时用）
    @Select("SELECT * FROM user WHERE phone = #{phone} AND deleted = 0")
    User selectByPhone(@Param("phone") String phone);
    
    // 根据 ID 查询用户
    @Select("SELECT * FROM user WHERE id = #{id} AND deleted = 0")
    User selectById(@Param("id") Long id);
    
    // 更新用户信息（修改密码、资料等）
    @Update("UPDATE user SET password = #{password}, update_time = NOW() WHERE id = #{id} AND deleted = 0")
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    
    // 逻辑删除（注销账号）
    @Update("UPDATE user SET deleted = 1, update_time = NOW() WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    // 分页查询用户列表（管理端用）
    @Select("SELECT * FROM user WHERE deleted = 0 " +
            "<if test='keyword != null and keyword != '''>AND (phone LIKE CONCAT('%',#{keyword},'%') OR real_name LIKE CONCAT('%',#{keyword},'%'))</if> " +
            "ORDER BY create_time DESC LIMIT #{offset}, #{size}")
    List<User> selectPage(@Param("keyword") String keyword, 
                          @Param("offset") int offset, 
                          @Param("size") int size);
    
    // 查询总记录数
    @Select("SELECT COUNT(*) FROM user WHERE deleted = 0 " +
            "<if test='keyword != null and keyword != '''>AND (phone LIKE CONCAT('%',#{keyword},'%') OR real_name LIKE CONCAT('%',#{keyword},'%'))</if>")
    long count(@Param("keyword") String keyword);
}
```

**做了什么：**
- 定义了 5 个方法，对应 user 表的基本 CRUD 操作
- 用 MyBatis 的注解方式写 SQL（也可以写在 XML 里）

---

### 步骤 4：创建 JwtUtil 工具类

**路径：** `core/util/JwtUtil.java`

**代码：**
```java
package com.wmm.eldercare.core.util;

import io.jsonwebtoken.*;      // JJWT 库：生成和解析 JWT
import io.jsonwebtoken.security.Keys;  // 密钥生成工具
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    
    // 密钥：实际项目应该从配置文件读取
    private static final String SECRET = "eldercare-secret-key-for-jwt-token-generation-2025";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    
    // Token 有效期
    private static final long ACCESS_TOKEN_EXPIRE = 2 * 3600 * 1000L;      // 2小时
    private static final long REFRESH_TOKEN_EXPIRE = 7 * 24 * 3600 * 1000L; // 7天

    /**
     * 生成 Access Token（短效，用于接口鉴权）
     */
    public static String generateAccessToken(Long userId, String phone, String role) {
        return Jwts.builder()
                .subject(String.valueOf(userId))     // 主题：用户 ID
                .claim("phone", phone)                 // 自定义字段：手机号
                .claim("role", role)                   // 自定义字段：角色
                .issuedAt(new Date())                  // 签发时间
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE))  // 过期时间
                .signWith(KEY)                         // 签名：用密钥加密
                .compact();                            // 生成 JWT 字符串
    }

    /**
     * 生成 Refresh Token（长效，用于无感刷新）
     */
    public static String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "refresh")             // 标记为 Refresh Token
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE))
                .signWith(KEY)
                .compact();
    }

    /**
     * 解析 Token，获取用户 ID
     */
    public static Long parseUserId(String token) {
        return Jwts.parser()
                .verifyWith(KEY)                      // 验证签名
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();                        // 返回 subject（用户 ID）
    }

    /**
     * 验证 Token 是否有效（没过期、签名正确）
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

**做了什么：**
- 提供 4 个静态方法：生成 Token、解析 Token、验证 Token
- 用 JJWT 库操作 JWT

---

### 步骤 5：创建 JWT 过滤器

**路径：** `core/filter/JwtAuthenticationFilter.java`

**代码：**
```java
package com.wmm.eldercare.core.filter;

import com.wmm.eldercare.core.util.JwtUtil;
import jakarta.servlet.*;          // Servlet 过滤器接口
import jakarta.servlet.http.*;     // HTTP 相关类
import org.springframework.stereotype.Component;  // Spring 组件注解
import java.io.IOException;

@Component                          // 交给 Spring 管理，自动注册为过滤器
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                     HttpServletResponse response, 
                                     FilterChain chain) throws ServletException, IOException {
        
        // 1. 从请求头获取 Authorization
        String header = request.getHeader("Authorization");
        
        // 2. 判断是否有 Token
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);  // 去掉 "Bearer " 前缀
            
            // 3. 验证 Token 是否有效
            if (JwtUtil.validateToken(token)) {
                // 4. 解析用户 ID 和角色，存入 Request Attributes
                Long userId = JwtUtil.parseUserId(token);
                String role = (String) Jwts.parser()
                    .verifyWith(JwtUtil.KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("role");
                
                request.setAttribute("userId", userId);   // 供 Controller 使用
                request.setAttribute("role", role);       // 供权限校验使用
            }
        }
        
        // 5. 放行请求（继续往下执行）
        chain.doFilter(request, response);
    }
}
```

**做了什么：**
- 实现 Servlet 过滤器，拦截所有请求
- 从 Header 里提取 Token，验证有效性
- 把用户 ID 和角色存入 request，供后续使用

---

### 步骤 6：创建 AuthController（登录注册）

**路径：** `core/controller/auth/AuthController.java`

**代码：**

```java
package com.wmm.eldercare.core.controller.auth;

import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.pojo.User;
import com.wmm.eldercare.core.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCrypt;  // BCrypt 密码加密

import java.util.HashMap;
import java.util.Map;

@RestController                     // 标注为 REST 控制器，返回 JSON
@RequestMapping("/api/auth")        // 所有接口路径前缀
public class AuthController {

    @Autowired
    private UserMapper userMapper;   // 注入 Mapper

    /**
     * 发送短信验证码（模拟）
     */
    @PostMapping("/sendSmsCode")
    public Result<?> sendSmsCode(@RequestParam String phone) {
        // TODO: 调用短信服务发送验证码
        // 这里只是模拟，实际项目要调阿里云短信 API
        System.out.println("发送验证码到 " + phone + "，验证码：1234");
        return Result.success();
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<?> register(@RequestParam String phone,
                              @RequestParam String code,
                              @RequestParam String password) {
        // 1. 验证短信验证码（实际项目要查 Redis 或数据库）
        if (!"1234".equals(code)) {
            return Result.error(400, "验证码错误");
        }

        // 2. 检查手机号是否已注册
        User existUser = userMapper.selectByPhone(phone);
        if (existUser != null) {
            return Result.error(400, "手机号已注册");
        }

        // 3. BCrypt 加密密码
        String encodedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // 4. 创建新用户，赠送 100 积分
        User user = new User();
        user.setPhone(phone);
        user.setPassword(encodedPassword);
        user.setMemberLevel("NORMAL");
        user.setPoints(100);          // 注册赠送 100 积分
        user.setStatus("ENABLED");
        user.setRole("MEMBER");

        userMapper.insert(user);

        return Result.success("注册成功");
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<?> login(@RequestParam String phone,
                           @RequestParam String password) {
        // 1. 查询用户
        User user = userMapper.selectByPhone(phone);
        if (user == null) {
            return Result.error(400, "手机号或密码错误");
        }

        // 2. 验证密码（BCrypt）
        if (!BCrypt.checkpw(password, user.getPassword())) {
            return Result.error(400, "手机号或密码错误");
        }

        // 3. 检查账号状态
        if (!"ENABLED".equals(user.getStatus())) {
            return Result.error(403, "账号已被禁用");
        }

        // 4. 生成双 Token
        String accessToken = JwtUtil.generateAccessToken(user.getId(), user.getPhone(), user.getRole());
        String refreshToken = JwtUtil.generateRefreshToken(user.getId());

        // 5. 组装返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", accessToken);
        data.put("refreshToken", refreshToken);
        data.put("user", user);

        return Result.success(data);
    }

    /**
     * 刷新 Token
     */
    @PostMapping("/refresh")
    public Result<?> refresh(@RequestParam String refreshToken) {
        // 1. 验证 Refresh Token
        if (!JwtUtil.validateToken(refreshToken)) {
            return Result.error(400, "Refresh Token 无效");
        }

        // 2. 解析用户 ID
        Long userId = JwtUtil.parseUserId(refreshToken);

        // 3. 查询用户获取最新信息
        User user = userMapper.selectById(userId);
        if (user == null || !"ENABLED".equals(user.getStatus())) {
            return Result.error(403, "用户已被禁用");
        }

        // 4. 生成新的 Access Token
        String newAccessToken = JwtUtil.generateAccessToken(user.getId(), user.getPhone(), user.getRole());

        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", newAccessToken);
        data.put("refreshToken", refreshToken);  // Refresh Token 不变

        return Result.success(data);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader("Authorization") String token) {
        // TODO: 将 Token 加入 Redis 黑名单
        return Result.success("登出成功");
    }
}
```

**做了什么：**
- 实现了 4 个接口：发送验证码、注册、登录、刷新 Token、登出
- 登录时生成双 Token（Access Token + Refresh Token）

---

### 步骤 7：SmsCodeMapper + XML（验证码）

**路径：** `core/mapper/SmsCodeMapper.java` + `resources/mapper/SmsCodeMapper.xml`

**Mapper 接口（3 个方法）：**
```java
@Mapper
public interface SmsCodeMapper {
    int insertSmsCode(SmsCode smsCode);        // 插入一条验证码
    SmsCode findByPhone(String phone);          // 查某手机号最新一条（倒序 LIMIT 1）
    int updateUsed(Long id);                    // 标记为已使用 used=1
}
```

**XML 关键点：**
- `findByPhone` 要 `ORDER BY create_time DESC LIMIT 1` 取最新一张"门禁卡"
- sms_code 表没有 deleted 字段，不加 `AND deleted = 0`

### 步骤 8：SmsUtil 工具类（模拟发短信）

**路径：** `core/util/SmsUtil.java`

```java
@Component
@Slf4j
public class SmsUtil {
    public String getSmsCode(String phone) {
        // 关键①：nextInt(1000000) 才能生成 6 位数（nextInt(10000) 只有 4 位）
        // 关键②：用局部变量，别用成员变量（单例并发会互相覆盖）
        String code = String.format("%06d", new Random().nextInt(1000000));
        log.info("【模拟短信】手机号:{} 验证码:{}", phone, code);
        return code;
    }
}
```

### 步骤 9：send-code 发验证码接口

```java
@PostMapping("/send-code")
public Result<String> sendCode(@RequestBody SmsCodeDTO smsCodeDTO) {
    String phone = smsCodeDTO.getPhone();
    // 1. 手机号格式校验（^1\d{10}$ 在 Java 里要写成 ^1\\d{10}$）
    if (phone == null || !phone.matches("^1\\d{10}$")) {
        throw new BusinessException(400, "手机号格式不正确");
    }
    // 2. 生成验证码
    String smsCode = smsUtil.getSmsCode(phone);
    // 3. 存库（必须 new！发验证码是"造新卡"，不是查旧卡）
    SmsCode entity = new SmsCode();
    entity.setPhone(phone);
    entity.setCode(smsCode);
    entity.setExpireTime(LocalDateTime.now().plusMinutes(5));
    entity.setUsed(0);
    entity.setCreateTime(LocalDateTime.now());
    smsCodeMapper.insertSmsCode(entity);
    // 4. 开发阶段把验证码返回，方便测试
    return Result.success(smsCode);
}
```

### 步骤 10：验证码四步校验（register 和 reset-password 共用）

```java
// 1. 判空：没发过验证码 → 拒绝（防 NPE！）
if (entity == null) throw new BusinessException(400, "请先获取验证码");
// 2. 比对：验证码不对 → 拒绝
if (!code.equals(entity.getCode())) throw new BusinessException(400, "验证码错误");
// 3. 过期：过期时间 < 现在 → 拒绝
if (entity.getExpireTime().isBefore(LocalDateTime.now())) throw new BusinessException(400, "验证码已过期");
// 4. 已使用：used == 1 → 拒绝
if (entity.getUsed() == 1) throw new BusinessException(400, "验证码已使用");
```

### 步骤 11：reset-password 重置密码接口

**要点：**
- 加 `@Transactional`（要么全成功要么全失败）
- 顺序：验密码一致 → 验验证码 → 更新密码 → **成功后才 updateUsed** → 删 refreshToken 强制下线
- ⚠️ 不要先 updateUsed 再 updateUser：密码更新失败会把验证码"吃掉"

### 步骤 12：多参数 Mapper 的大坑（updateUser）

**问题：** `int updateUser(@Param("userId") Long id, User user)` — XML 里 `test="phone"` 报错
```
Parameter 'phone' not found. Available parameters are [userId, user, param1, param2]
```

**原因：** `#{}` 取值会去对象里翻属性（#{phone} 能找到 user.phone）；
`test=` 判断只认参数名（test="phone" 找不到就叫"phone"的参数）

**解决：** 参数加 `@Param("user")`，XML 全部用 `user.xxx`
```java
int updateUser(@Param("userId") Long id, @Param("user") User user);
```
```xml
<if test="user.phone != null">phone = #{user.phone},</if>
```

---

## 第二部分：健康记录模块（Health）

### 步骤 1：创建 HealthRecord 实体类

**路径：** `core/pojo/HealthRecord.java`

**代码：**
```java
package com.wmm.eldercare.core.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class HealthRecord {
    private Long id;
    private Long userId;
    private Integer systolic;           // 收缩压
    private Integer diastolic;          // 舒张压
    private BigDecimal bloodSugar;      // 血糖
    private Integer heartRate;          // 心率
    private BigDecimal weight;          // 体重
    private BigDecimal bmi;             // BMI（自动计算）
    private String memo;                // 备注
    private LocalDateTime recordedAt;   // 记录时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}
```

---

### 步骤 2：创建 HealthRecordMapper

**路径：** `core/mapper/HealthRecordMapper.java`

**代码：**
```java
package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.HealthRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface HealthRecordMapper {
    
    @Insert("INSERT INTO health_record (user_id, systolic, diastolic, blood_sugar, heart_rate, weight, bmi, memo, recorded_at) " +
            "VALUES (#{userId}, #{systolic}, #{diastolic}, #{bloodSugar}, #{heartRate}, #{weight}, #{bmi}, #{memo}, NOW())")
    @SelectKey(keyProperty = "id", before = false, resultType = Long.class, 
               statement = "SELECT LAST_INSERT_ID()")
    int insert(HealthRecord record);
    
    @Select("SELECT * FROM health_record WHERE user_id = #{userId} AND deleted = 0 ORDER BY recorded_at DESC LIMIT #{offset}, #{size}")
    List<HealthRecord> selectPage(@Param("userId") Long userId, 
                                   @Param("offset") int offset, 
                                   @Param("size") int size);
    
    @Select("SELECT * FROM health_record WHERE user_id = #{userId} AND deleted = 0 ORDER BY recorded_at DESC")
    List<HealthRecord> selectAll(@Param("userId") Long userId);
}
```

---

### 步骤 3：创建 HealthController

**路径：** `core/controller/health/HealthController.java`

**代码：**
```java
package com.wmm.eldercare.core.controller.health;

import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.pojo.HealthRecord;
import com.wmm.eldercare.core.mapper.HealthRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member/health")
public class HealthController {
    
    @Autowired
    private HealthRecordMapper healthRecordMapper;

    /**
     * 录入健康数据
     */
    @PostMapping
    public Result<?> addHealthRecord(@RequestAttribute("userId") Long userId,
                                      @RequestParam Integer systolic,
                                      @RequestParam Integer diastolic,
                                      @RequestParam BigDecimal bloodSugar,
                                      @RequestParam Integer heartRate,
                                      @RequestParam BigDecimal weight,
                                      @RequestParam(required = false) String memo) {
        // 1. 查询用户身高，计算 BMI
        // TODO: 从 User 表获取身高
        
        // 2. 计算 BMI = 体重(kg) / (身高(m))^2
        BigDecimal bmi = weight.divide(
            height.multiply(height).divide(new BigDecimal("10000"), 4, BigDecimal.ROUND_HALF_UP),
            1, BigDecimal.ROUND_HALF_UP
        );
        
        // 3. 保存健康记录
        HealthRecord record = new HealthRecord();
        record.setUserId(userId);
        record.setSystolic(systolic);
        record.setDiastolic(diastolic);
        record.setBloodSugar(bloodSugar);
        record.setHeartRate(heartRate);
        record.setWeight(weight);
        record.setBmi(bmi);
        record.setMemo(memo);
        
        healthRecordMapper.insert(record);
        
        // 4. 检查是否需要触发健康提醒
        // TODO: 调用健康提醒服务
        
        return Result.success("录入成功");
    }

    /**
     * 查询健康记录列表
     */
    @GetMapping("/list")
    public Result<?> listHealthRecords(@RequestAttribute("userId") Long userId,
                                        @RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size) {
        int offset = (page - 1) * size;
        List<HealthRecord> records = healthRecordMapper.selectPage(userId, offset, size);
        return Result.success(records);
    }

    /**
     * 查询所有健康记录（用于趋势图）
     */
    @GetMapping("/all")
    public Result<?> getAllHealthRecords(@RequestAttribute("userId") Long userId) {
        List<HealthRecord> records = healthRecordMapper.selectAll(userId);
        return Result.success(records);
    }
}
```

---

## 注解速查表

| 注解 | 来源 | 作用 |
|------|------|------|
| `@RestController` | Spring | 标注为 REST 控制器，返回 JSON |
| `@Controller` | Spring | 标注为控制器，可返回视图 |
| `@Service` | Spring | 标注为业务层组件 |
| `@Component` | Spring | 标注为普通组件，交给 Spring 管理 |
| `@Autowired` | Spring | 自动注入依赖 |
| `@RequestMapping` | Spring | 映射请求路径 |
| `@GetMapping` | Spring | 映射 GET 请求 |
| `@PostMapping` | Spring | 映射 POST 请求 |
| `@PutMapping` | Spring | 映射 PUT 请求 |
| `@DeleteMapping` | Spring | 映射 DELETE 请求 |
| `@RequestParam` | Spring | 获取查询参数或表单参数 |
| `@PathVariable` | Spring | 获取路径变量 |
| `@RequestBody` | Spring | 接收请求体（JSON） |
| `@RequestHeader` | Spring | 获取请求头 |
| `@RequestAttribute` | Spring | 获取 request 属性（如 userId） |
| `@Mapper` | MyBatis | 标注为 Mapper 接口 |
| `@Select` | MyBatis | 写 SELECT 查询 SQL |
| `@Insert` | MyBatis | 写 INSERT 插入 SQL |
| `@Update` | MyBatis | 写 UPDATE 更新 SQL |
| `@Delete` | MyBatis | 写 DELETE 删除 SQL |
| `@SelectKey` | MyBatis | 查询主键（插入后获取自增 ID） |
| `@Param` | MyBatis | 命名参数 |
| `@Data` | Lombok | 自动生成 getter/setter/toString |
| `@NoArgsConstructor` | Lombok | 生成无参构造方法 |
| `@AllArgsConstructor` | Lombok | 生成全参构造方法 |
| `@EnableScheduling` | Spring | 开启定时任务支持 |
| `@Scheduled` | Spring | 标注定时任务方法 |
| `@Transactional` | Spring | 开启事务 |

---

## 开发顺序建议

```
Day 1：项目搭建 ✅
Day 2：Result + User + RefreshToken ✅
Day 3：SmsCode + HealthRecord + 其他实体类
Day 4：所有 Mapper 接口
Day 5：AuthController（登录注册）
Day 6：HealthController（健康记录）
Day 7：PointsService（积分系统）
Day 8：ChatController（AI 对话）
Day 9：AppointmentController（体检预约）
Day 10：管理端后端
Day 11-12：前端开发
```

---

**本文档持续更新，每次写完一个模块都要记录步骤！**
