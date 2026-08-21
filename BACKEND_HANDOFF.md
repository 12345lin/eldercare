# 颐锦康养系统 - 后端开发交接文档

> 更新时间：2026-08-19
> 项目路径：`E:\DevProject\Java_Project\Web-Project\eldercare`

---

## 📁 项目结构

```
eldercare/
├── src/main/java/com/wmm/eldercare/
│   ├── EldercareApplication.java     # 启动类
│   ├── core/
│   │   ├── common/
│   │   │   ├── Result.java          # 统一响应
│   │   │   ├── PageResult.java      # 分页响应
│   │   │   └── BusinessException.java  # 业务异常
│   │   ├── config/
│   │   │   ├── CorsConfig.java      # 跨域配置
│   │   │   ├── MybatisPlusConfig.java  # MyBatis 配置
│   │   │   └── AliyunOSSProperties.java  # OSS 配置
│   │   ├── util/
│   │   │   ├── JwtUtil.java         # JWT 工具
│   │   │   └── AliyunOSSOperator.java  # OSS 工具
│   │   ├── mapper/                  # MyBatis Mapper
│   │   ├── pojo/                    # 实体类
│   │   ├── vo/                      # 视图对象
│   │   ├── dto/                     # 数据传输对象
│   │   └── service/                 # 服务层
│   ├── member/
│   │   └── controller/              # 会员端控制器
│   └── admin/
│       └── controller/              # 管理端控制器
├── src/main/resources/
│   ├── mapper/                      # MyBatis XML
│   ├── application.yaml             # 配置文件
│   └── db/                          # 数据库脚本
└── pom.xml
```

---

## 🎯 已实现功能

### 1. 用户模块
- [x] 用户登录/注册
- [x] 发送验证码
- [x] 重置密码
- [x] 获取个人资料
- [x] 更新个人资料
- [x] 修改密码
- [x] 上传头像
- [x] 获取用户统计

### 2. 消息模块
- [x] 发送系统消息
- [x] 获取消息列表
- [x] 获取消息详情（自动标已读）
- [x] 删除消息
- [x] 统计未读数

### 3. 活动模块
- [x] 活动列表
- [x] 活动详情
- [x] 活动报名
- [x] 签到
- [x] 取消报名
- [x] 管理端 CRUD

### 4. 预约模块
- [x] 套餐列表
- [x] 时段查询
- [x] 预约提交
- [x] 预约取消
- [x] 管理端状态流转

### 5. 评测模块
- [x] 问卷列表
- [x] 问卷详情
- [x] 提交评测
- [x] AI 评分
- [x] 结果查看
- [x] 管理端 CRUD

### 6. 健康记录模块
- [x] 健康记录列表
- [x] 健康记录详情
- [x] 健康指标趋势
- [x] 删除健康记录

### 7. 积分模块
- [x] 积分明细
- [x] 积分统计
- [x] 积分消费

### 8. 管理端模块
- [x] 用户管理
- [x] 消息管理
- [x] 系统配置
- [x] 活动管理
- [x] 评测管理
- [x] 预约管理

---

## 🗄️ 数据库设计

### 核心表结构

#### user - 用户表
```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phone VARCHAR(20) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50),
    gender VARCHAR(10),
    birth_date DATE,
    height DECIMAL(5,2),
    avatar VARCHAR(500),
    emergency_contact VARCHAR(50),
    member_level VARCHAR(20) DEFAULT 'NORMAL',
    points INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ENABLED',
    role VARCHAR(20) DEFAULT 'MEMBER',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);
```

#### message - 消息表
```sql
CREATE TABLE message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    title VARCHAR(200),
    content TEXT,
    is_read TINYINT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);
```

#### activity - 活动表
```sql
CREATE TABLE activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    cover_url VARCHAR(500),
    description TEXT,
    activity_start DATETIME,
    activity_end DATETIME,
    max_participants INT,
    current_participants INT DEFAULT 0,
    location VARCHAR(200),
    status VARCHAR(20) DEFAULT 'DRAFT',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);
```

#### appointment - 预约表
```sql
CREATE TABLE appointment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    package_id BIGINT NOT NULL,
    slot_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);
```

#### appointment_package - 预约套餐表
```sql
CREATE TABLE appointment_package (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    cover_url VARCHAR(500),
    description TEXT,
    price INT NOT NULL,
    suitable_people VARCHAR(200),
    items VARCHAR(1000),
    status VARCHAR(20) DEFAULT 'ENABLED',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);
```

#### appointment_slot - 预约时段表
```sql
CREATE TABLE appointment_slot (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    package_id BIGINT NOT NULL,
    appoint_date DATE NOT NULL,
    time_range VARCHAR(50) NOT NULL,
    max_count INT DEFAULT 20,
    current_count INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'OPEN',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);
```

#### questionnaire - 问卷表
```sql
CREATE TABLE questionnaire (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'DRAFT',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);
```

#### assessment_result - 评测结果表
```sql
CREATE TABLE assessment_result (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    questionnaire_id BIGINT NOT NULL,
    answers TEXT,
    ai_score DECIMAL(5,2),
    ai_suggestion TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);
```

#### health_record - 健康记录表
```sql
CREATE TABLE health_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    record_type VARCHAR(50) NOT NULL,
    title VARCHAR(200),
    value DECIMAL(10,2),
    unit VARCHAR(20),
    note TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);
```

#### point_record - 积分记录表
```sql
CREATE TABLE point_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    change_points INT NOT NULL,
    balance INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    reason VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### sys_config - 系统配置表
```sql
CREATE TABLE sys_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(100) UNIQUE NOT NULL,
    config_value TEXT,
    description VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);
```

---

## 🔐 安全设计

### JWT 认证
```java
// Token 生成
String token = JwtUtil.generateToken(
    String.valueOf(userId),
    phone,
    role
);

// Token 有效期：2小时
private static final long EXPIRATION = 2 * 60 * 60 * 1000;
```

### 权限控制
```java
// 管理员接口需要 ADMIN 角色
@PreAuthorize("hasRole('ADMIN')")
```

### 密码加密
```java
// BCrypt 加密
String encodedPassword = passwordEncoder.encode(rawPassword);
```

---

## 📦 核心依赖

```xml
<!-- Spring Boot -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- MyBatis Plus -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.5</version>
</dependency>

<!-- PageHelper -->
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>2.1.0</version>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>

<!-- Redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- OSS -->
<dependency>
    <groupId>com.aliyun.oss</groupId>
    <artifactId>aliyun-sdk-oss</artifactId>
    <version>3.18.4</version>
</dependency>

<!-- Spring AI -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

---

## ⚙️ 配置文件

### application.yaml
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/eldercare?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  redis:
    host: localhost
    port: 6379
    database: 0
  
  ai:
    openai:
      api-key: sk-qJ1cVyTkwWwQzmGNyT805oYeaUwZXDHbqzyLuXSYi2Uw5u7o
      base-url: https://api.agnes.ai/v1
      chat:
        options:
          model: agnes-2.5-flash

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.wmm.eldercare.core.pojo
  configuration:
    map-underscore-to-camel-case: true

aliyun:
  oss:
    endpoint: oss-cn-beijing.aliyuncs.com
    access-key-id: ${OSS_ACCESS_KEY_ID}
    access-key-secret: ${OSS_ACCESS_KEY_SECRET}
    bucket-name: wmmya

jwt:
  secret: your-secret-key-here
  expiration: 7200000
```

---

## 🔄 接口设计规范

### 统一响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 分页响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10
  }
}
```

### 错误响应格式
```json
{
  "code": 400,
  "message": "业务错误信息",
  "data": null
}
```

---

## 🚀 部署指南

### 开发环境
```bash
# 1. 启动 MySQL
# 2. 启动 Redis
# 3. 设置环境变量
export OSS_ACCESS_KEY_ID=your_access_key
export OSS_ACCESS_KEY_SECRET=your_access_secret

# 4. 启动应用
cd E:\DevProject\Java_Project\Web-Project\eldercare
.\mvnw spring-boot:run
```

### 生产环境
```bash
# 打包
.\mvnw clean package -DskipTests

# 运行
java -jar target/eldercare-1.0.0.jar
```

---

## 📝 开发规范

### 1. 包命名规范
```
com.wmm.eldercare
├── core/           # 核心模块
│   ├── common/     # 通用类
│   ├── config/     # 配置类
│   ├── util/       # 工具类
│   ├── mapper/     # 数据访问层
│   ├── pojo/       # 实体类
│   ├── vo/         # 视图对象
│   ├── dto/        # 数据传输对象
│   └── service/    # 服务层
├── member/         # 会员端模块
│   └── controller/ # 控制器
└── admin/          # 管理端模块
    └── controller/ # 控制器
```

### 2. 异常处理规范
- Service 层：抛 `BusinessException(code, message)`
- Controller 层：统一异常处理器捕获
- 全局异常：返回统一错误格式

### 3. 分页规范
- 使用 PageHelper
- Mapper 只查原始数据
- Service 层处理分页逻辑
- 返回 PageResult<T>

---

## 🐛 常见问题

### 1. JWT Token 过期
- 默认 2 小时有效期
- 前端需要刷新 Token

### 2. OSS 上传失败
- 检查环境变量是否正确
- 检查网络是否连通

### 3. 数据库连接失败
- 检查 MySQL 是否启动
- 检查账号密码是否正确

### 4. Redis 连接失败
- 检查 Redis 是否启动
- 检查端口是否正确

---

## 📞 联系方式

- 项目负责人：宝宝
- 开发时间：2026-08
- 最后更新：2026-08-19

---

**祝开发顺利！** 🐴💪
