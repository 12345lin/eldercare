# AI 智能养老社区管理系统 — 实现指南

> 根据桌面需求文档 + 详细设计文档编写  
> 项目路径：`E:\DevProject\Java_Project\Web-Project\eldercare`  
> 包名：`com.wmm.eldercare`（保持不变）

---

## 一、技术栈确认

| 层 | 技术 | 版本 |
|---|------|------|
| 语言 | Java | 25.0.1 |
| 框架 | Spring Boot | 4.1.0 |
| ORM | MyBatis | 3.5.19（mybatis-spring-boot-starter 4.1.0） |
| 数据库 | MySQL | 9.7.2 |
| 缓存 | Redis | 8.8.0 |
| JWT | JJWT | 0.13.0 |
| AI | Spring AI + DeepSeek | 2.0.0 |
| 参数校验 | Jakarta Validation | 3.1.1 |
| 会员端前端 | Vue3 + Vant | Vite 8.2 |
| 管理端前端 | Vue3 + Element Plus | Vite 8.2 |

---

## 二、目录结构

```
eldercare/
├── pom.xml                          ← 已有，需要更新依赖
├── src/main/java/com/wmm/eldercare/
│   ├── ElderCareApplication.java    ← 已有，需加 @EnableScheduling
│   ├── core/                        ← 公共模块
│   │   ├── common/
│   │   │   └── Result.java          ← 统一响应
│   │   ├── config/
│   │   │   ├── MyBatisConfig.java
│   │   │   ├── RedisConfig.java
│   │   │   ├── CorsConfig.java
│   │   │   └── SecurityConfig.java
│   │   ├── entity/                  ← 17个实体类
│   │   ├── mapper/                  ← 17个Mapper接口
│   │   ├── service/                 ← 业务层
│   │   ├── controller/
│   │   │   ├── auth/AuthController.java
│   │   │   ├── health/HealthController.java
│   │   │   ├── assessment/AssessmentController.java
│   │   │   ├── chat/ChatController.java
│   │   │   ├── appointment/AppointmentController.java
│   │   │   ├── activity/ActivityController.java
│   │   │   ├── profile/ProfileController.java
│   │   │   └── message/MessageController.java
│   │   ├── filter/
│   │   │   └── JwtAuthenticationFilter.java
│   │   ├── exception/
│   │   │   ├── BusinessException.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── util/
│   │   │   ├── JwtUtil.java
│   │   │   └── SmsUtil.java
│   │   ├── enums/                   ← 枚举类
│   │   ├── dto/                     ← 请求DTO
│   │   └── vo/                      ← 响应VO
│   ├── admin/                       ← 管理端
│   │   ├── controller/
│   │   │   ├── DashboardController.java
│   │   │   ├── MemberController.java
│   │   │   ├── AppointmentController.java
│   │   │   ├── AssessmentController.java
│   │   │   ├── ActivityController.java
│   │   │   ├── MessageController.java
│   │   │   └── ConfigController.java
│   │   └── service/
│   └── api/                         ← 会员端
│       ├── controller/              ← 上面已列
│       └── service/
├── src/main/resources/
│   ├── application.yml              ← 已有，需更新
│   ├── mapper/                      ← MyBatis XML
│   └── sql/                         ← 建表SQL
└── src/test/java/com/wmm/eldercare/
    └── ElderCareApplicationTests.java
```

---

## 三、数据库（17张表）

### 3.1 建库语句

```sql
CREATE DATABASE IF NOT EXISTS eldercare 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_0900_ai_ci;
USE eldercare;
```

### 3.2 17张表清单

| # | 表名 | 说明 |
|---|------|------|
| 1 | `user` | 用户表（会员+管理员） |
| 2 | `refresh_token` | 刷新令牌表 |
| 3 | `sms_code` | 短信验证码表 |
| 4 | `health_record` | 健康记录表 |
| 5 | `questionnaire` | 问卷表 |
| 6 | `question` | 题目表 |
| 7 | `assessment_result` | 评测结果表 |
| 8 | `appointment_package` | 体检套餐表 |
| 9 | `appointment_slot` | 预约时段表 |
| 10 | `appointment` | 预约记录表 |
| 11 | `community_activity` | 社区活动表 |
| 12 | `activity_registration` | 活动报名表 |
| 13 | `health_guidance` | 健康指导表 |
| 14 | `ai_conversation_session` | AI会话表 |
| 15 | `ai_conversation_message` | AI消息表 |
| 16 | `message` | 站内消息表 |
| 17 | `sys_config` | 系统配置表 |

### 3.3 初始化数据

```sql
-- 系统配置
INSERT INTO sys_config (config_key, config_value, description) VALUES
('ai_chat_system_prompt', '你是一位专业的健康顾问，请用亲切、易懂的语言回答用户的健康问题。', 'AI 对话系统提示词'),
('register_bonus_points', '100', '注册赠送积分'),
('checkin_bonus_points', '50', '活动签到赠送积分'),
('health_assessment_min_score', '60', '健康评测及格分数线'),
('access_token_expire_hours', '2', 'Access Token 有效期（小时）'),
('refresh_token_expire_days', '7', 'Refresh Token 有效期（天）');

-- 默认管理员（密码：Admin@123456，BCrypt加密）
INSERT INTO user (phone, password, real_name, member_level, points, status, role) VALUES
('13800000000', '$2b$10$5xxJYAxX3bB35VkjlRAuauILyrcKEUXJINVQXrWPYl6vhfZlIiy46', '系统管理员', 'PLATINUM', 99999, 'ENABLED', 'ADMIN');

-- 测试会员（密码：Test@123456，BCrypt加密）
INSERT INTO user (phone, password, real_name, member_level, points, status, role) VALUES
('13800138000', '$2b$10$La.Q.aZ.SUB5Ej3neFdzGOUYLva/QuO7sALyOPaBCxyYkro9Cpzjm', '测试用户', 'NORMAL', 1000, 'ENABLED', 'MEMBER');

-- 示例问卷
INSERT INTO questionnaire (title, description, status) VALUES
('基础健康状况调查问卷', '通过简单的问题了解您的基本健康状况', 'PUBLISHED');

INSERT INTO question (questionnaire_id, content, type, options, sort_order) VALUES
(1, '您的年龄是？', 'SINGLE', '["18-30岁", "31-45岁", "46-60岁", "60岁以上"]', 1),
(1, '您的睡眠质量如何？', 'SINGLE', '["很好，每天睡 7-8 小时", "一般，偶尔失眠", "较差，经常失眠", "非常差，严重影响生活"]', 2),
(1, '您每周运动几次？', 'SINGLE', '["几乎不运动", "1-2 次", "3-4 次", "5 次以上"]', 3),
(1, '请简要描述您目前的健康状况', 'TEXT', NULL, 4);

-- 示例体检套餐
INSERT INTO appointment_package (name, description, price, suitable_people, items, status) VALUES
('基础体检套餐', '适合健康人群的基础体检，包含常规检查项目', 500, '所有人群', 
 '[\"血常规\", \"尿常规\", \"肝功能\", \"肾功能\", \"心电图\", \"胸部 X 光\"]', 'ENABLED'),
('中老年体检套餐', '针对中老年人的全面体检，包含心脑血管等专项检查', 1000, '45 岁以上人群',
 '[\"基础套餐全部项目\", \"肿瘤标志物\", \"颈动脉彩超\", \"骨密度检测\", \"甲状腺功能\"]', 'ENABLED'),
('女性专属套餐', '针对女性健康特点设计的专项体检套餐', 800, '女性人群',
 '[\"基础套餐全部项目\", \"妇科检查\", \"乳腺彩超\", \"HPV 检测\", \"TCT 检查\"]', 'ENABLED');
```

---

## 四、实现步骤（按顺序来）

### 阶段一：基础设施（第 1-2 天）

#### 步骤 1：更新 pom.xml

**现有依赖：** Web、MySQL、Lombok、Test  
**需要新增依赖：**

```xml
<!-- MyBatis -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>4.1.0</version>
</dependency>

<!-- Redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.13.0</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.13.0</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.13.0</version>
    <scope>runtime</scope>
</dependency>

<!-- Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Spring AI -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
    <version>2.0.0</version>
</dependency>
```

#### 步骤 2：更新 application.yml

```yaml
server:
  port: 8080

spring:
  application:
    name: eldercare
  datasource:
    url: jdbc:mysql://localhost:3306/eldercare?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
    username: root
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
  redis:
    host: localhost
    port: 6379
    password:
    database: 0
  servlet:
    multipart:
      max-file-size: 20MB
      max-request-size: 20MB

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.wmm.eldercare.core.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

logging:
  level:
    com.wmm.eldercare: debug
```

#### 步骤 3：启动类加 @EnableScheduling

```java
@SpringBootApplication
@EnableScheduling   // ← 加这一行
public class ElderCareApplication { ... }
```

---

### 阶段二：核心框架（第 2-3 天）

#### 步骤 4：Result.java（统一响应）

```java
package com.wmm.eldercare.core.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }
}
```

#### 步骤 5：创建 17 个 Entity 类

按文档第 571-1063 行的字段定义，每张表一个 Entity。  
示例（User.java）：

```java
package com.wmm.eldercare.core.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String phone;
    private String password;
    private String realName;
    private String gender;
    private LocalDate birthDate;
    private BigDecimal height;
    private String avatar;
    private String emergencyContact;
    private String memberLevel;  // NORMAL/SILVER/GOLD/PLATINUM/DIAMOND
    private Integer points;
    private String status;       // ENABLED/DISABLED
    private String role;         // MEMBER/ADMIN
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}
```

**其余 16 个 Entity 按同样方式创建**（参考设计文档 6.3 节）。

#### 步骤 6：创建 Mapper 接口 + XML

每个 Entity 对应一个 Mapper。示例（UserMapper.java）：

```java
package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.entity.User;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserMapper {
    int insert(User user);
    User selectById(@Param("id") Long id);
    User selectByPhone(@Param("phone") String phone);
    int update(User user);
    int deleteById(@Param("id") Long id);  // 逻辑删除
    List<User> selectPage(@Param("keyword") String keyword, 
                          @Param("offset") int offset, 
                          @Param("size") int size);
    long count(@Param("keyword") String keyword);
}
```

XML 文件放在 `resources/mapper/UserMapper.xml`。

---

### 阶段三：认证模块（第 3-4 天）⭐ 重点

#### 步骤 7：JwtUtil.java

```java
package com.wmm.eldercare.core.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    private static final String SECRET = "eldercare-secret-key-for-jwt-token-generation-2025";
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    private static final long ACCESS_TOKEN_EXPIRE = 2 * 3600 * 1000L;   // 2小时
    private static final long REFRESH_TOKEN_EXPIRE = 7 * 24 * 3600 * 1000L; // 7天

    public static String generateAccessToken(Long userId, String phone, String role) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("phone", phone)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE))
                .signWith(KEY)
                .compact();
    }

    public static String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRE))
                .signWith(KEY)
                .compact();
    }

    public static Long parseUserId(String token) {
        return Jwts.parser().verifyWith(KEY).build()
                .parseSignedClaims(token).getPayload().getSubject(), Long.class);
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

#### 步骤 8：JwtAuthenticationFilter.java

```java
package com.wmm.eldercare.core.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                     HttpServletResponse response, 
                                     FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            // 验证Token，设置用户信息到Request Attributes
            request.setAttribute("userId", JwtUtil.parseUserId(token));
            request.setAttribute("role", getRoleFromToken(token));
        }
        chain.doFilter(request, response);
    }
}
```

#### 步骤 9：AuthController.java（会员端认证接口）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/auth/sendSmsCode` | 发送短信验证码 |
| POST | `/api/auth/register` | 注册 |
| POST | `/api/auth/login` | 登录 |
| POST | `/api/auth/refresh` | 刷新Token |
| POST | `/api/auth/logout` | 登出 |
| POST | `/api/auth/resetPassword` | 密码找回 |

**登录接口核心逻辑：**
```java
@PostMapping("/login")
public Result<?> login(@RequestBody LoginDTO dto) {
    // 1. 查用户
    User user = userService.findByPhone(dto.getPhone());
    // 2. 验证密码（BCrypt）
    if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
        return Result.error(400, "手机号或密码错误");
    }
    // 3. 检查状态
    if (!"ENABLED".equals(user.getStatus())) {
        return Result.error(403, "账号已被禁用");
    }
    // 4. 生成双Token
    String accessToken = JwtUtil.generateAccessToken(user.getId(), user.getPhone(), user.getRole());
    String refreshToken = JwtUtil.generateRefreshToken(user.getId());
    // 5. 保存Refresh Token到数据库
    refreshTokenService.save(user.getId(), refreshToken);
    // 6. 返回
    return Result.success(Map.of(
        "accessToken", accessToken,
        "refreshToken", refreshToken,
        "user", user
    ));
}
```

---

### 阶段四：健康记录模块（第 4-5 天）

#### HealthController.java 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/member/health` | 录入健康数据 |
| GET | `/api/member/health/history` | 查询历史 |
| GET | `/api/member/health/trend` | 趋势分析 |

**录入时自动计算 BMI：**
```java
// BMI = 体重(kg) / (身高(m))^2
double bmi = weight / Math.pow(height / 100.0, 2);
```

**健康提醒触发逻辑：**
```java
if (systolic < 90 || systolic >= 140) {
    // 触发健康提醒 → 生成 health_guidance + 站内消息
}
```

---

### 阶段五：积分系统（第 5 天）

#### 核心：原子操作防止并发

```java
// 扣积分（原子操作，防止超扣）
@Update("UPDATE user SET points = points - #{amount} WHERE id = #{userId} AND points >= #{amount}")
int deductPoints(@Param("userId") Long userId, @Param("amount") int amount);

// 还积分
@Update("UPDATE user SET points = points + #{amount} WHERE id = #{userId}")
int refundPoints(@Param("userId") Long userId, @Param("amount") int amount);
```

**定时任务清理过期积分：**
```java
@Scheduled(cron = "0 0 2 * * ?")  // 每天凌晨2点
public void cleanExpiredPoints() {
    // 清理 1 年前获取且未使用的积分
}
```

---

### 阶段六：AI 对话模块（第 5-6 天）

#### Spring AI 配置（application.yml 追加）

```yaml
spring:
  ai:
    openai:
      api-key: ${DEEPSEEK_API_KEY}
      base-url: https://api.deepseek.com
      chat:
        options:
          model: deepseek-chat
          temperature: 0.7
```

#### ChatController.java（SSE 流式）

```java
@GetMapping(value = "/api/member/chat/{sessionId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public SseEmitter streamChat(@PathVariable Long sessionId, 
                              @RequestParam String message) {
    SseEmitter emitter = new SseEmitter(60_000L);
    // 1. 获取最近10轮对话上下文
    // 2. 调用 chatClient.stream() 流式响应
    // 3. 逐字推送到前端
    // 4. 保存对话记录
    return emitter;
}
```

---

### 阶段七：体检预约模块（第 6 天）

#### 核心：事务 + 并发控制

```java
@Transactional
public void bookAppointment(Long userId, Long slotId) {
    // 1. 校验积分充足
    // 2. 原子扣积分
    userMapper.deductPoints(userId, packagePrice);
    // 3. 原子增加预约数
    appointmentSlotMapper.incrementCount(slotId);
    // 4. 创建预约记录
    appointmentMapper.insert(appointment);
}
```

---

### 阶段八：管理端后端（第 6-7 天）

#### 接口清单

| 模块 | 路径前缀 | 主要接口 |
|------|---------|---------|
| 仪表盘 | `/api/admin/dashboard` | GET 统计概览 |
| 会员管理 | `/api/admin/members` | 分页列表、详情、启用/禁用、等级调整、积分调整、重置密码 |
| 体检管理 | `/api/admin/appointment` | 套餐CRUD、时段批量生成、预约管理、报告上传 |
| 评测管理 | `/api/admin/assessment` | 问卷CRUD、题目CRUD |
| 活动管理 | `/api/admin/activity` | 活动CRUD、签到查看 |
| 消息管理 | `/api/admin/message` | 消息列表、推送、批量推送 |
| 系统配置 | `/api/admin/config` | 配置列表、获取、更新 |

---

### 阶段九：前端（第 7 天+）

#### 会员端（Vue3 + Vant）

**页面清单：**
1. 登录/注册页
2. 首页（健康概览）
3. 健康记录页（录入 + 图表）
4. AI 对话页
5. 体检预约页
6. 社区活动页
7. 个人中心页

#### 管理端（Vue3 + Element Plus）

**页面清单：**
1. 登录页
2. 仪表盘
3. 会员管理
4. 体检管理
5. 评测管理
6. 活动管理
7. 消息管理
8. 系统配置

---

## 五、每日任务清单

| 天 | 任务 | 产出 |
|---|------|------|
| Day 1 | 更新 pom.xml + application.yml + 创建目录结构 | 项目骨架 |
| Day 2 | 创建数据库（17张表）+ 实体类 + Mapper | 数据层 |
| Day 3 | 认证模块（JWT + AuthController） | 登录注册 |
| Day 4 | 健康记录 + 积分系统 | 健康模块 |
| Day 5 | AI对话 + 体检预约 | AI + 预约模块 |
| Day 6 | 管理端后端（所有接口） | 管理端接口 |
| Day 7 | 会员端前端 + 管理端前端 + 联调测试 | 全功能 |

---

## 六、关键注意事项

1. **密码加密**：用 `BCrypt.hashpw(password)` 存库，登录时用 `BCrypt.checkpw()` 验证
2. **逻辑删除**：所有业务表用 `deleted` 字段，查询时加 `WHERE deleted = 0`
3. **事务边界**：预约/积分操作必须用 `@Transactional` 包裹
4. **SSE 异常处理**：客户端断开要捕获异常，已接收内容要保存
5. **文件上传**：报告存储到 `data/upload/report/yyyyMM/{uuid}.pdf`，最多20MB，仅PDF
6. **限流**：登录 6次/分钟，短信 3次/分钟，单日最多10条验证码

---

## 七、需要小码做什么？

回复数字告诉小码下一步：

**1** — 帮你更新 pom.xml（加依赖）  
**2** — 帮你创建目录结构  
**3** — 给你写 Result.java  
**4** — 给你写 User.java 实体类模板  
**5** — 给你写完整的建表 SQL  
**6** — 给你写 JwtUtil.java  
**7** — 给你写 AuthController.java  
**8** — 开始写第一个模块（你看完文档自己写，小码 review）

**选一个？** 🐴
