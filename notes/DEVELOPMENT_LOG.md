# AI 智能养老社区管理系统 — 开发日志

> 项目路径：`E:\DevProject\Java_Project\Web-Project\eldercare`
> 包名：`com.wmm.eldercare`
> 技术栈：Spring Boot 4.1.0 + Java 25 + MyBatis + Redis + JWT + Spring AI + Agnes

---

## 第 1 天 — 2026-08-14

### 今日工作内容

| 任务 | 状态 | 说明 |
|------|------|------|
| 新建项目（IDEA） | ✅ | 用户自己建的项目 |
| 修复 pom.xml 依赖错误 | ✅ | 修正了 `spring-boot-starter-webmvc` → `spring-boot-starter-web` |
| 配置 Spring AI + Agnes | ✅ | 使用免费 Agnes API，配置好 application.yaml |
| 编写建表 SQL | ✅ | 17张表 + 初始化数据，保存到 `src/main/resources/sql/init.sql` |
| 创建实现指南文档 | ✅ | `IMPLEMENTATION_GUIDE.md` |
| 创建模块开发步骤文档 | ✅ | `MODULE_STEPS.md` |
| **用户自建 Result.java** | ✅ | 统一响应封装，位于 `core/common/Result.java` |
| **用户自建 User.java** | ✅ | 用户实体类，位于 `core/pojo/User.java` |

### 用户问的问题

| # | 问题 | 小码的回答要点 |
|---|------|--------------|
| 1 | JDK 25 在 IDEA 里为什么 Java 选项没有？ | IDEA 新建项目时选 SDK，不是选 Java version。选 `25 Oracle OpenJDK 25.0.1` 就行 |
| 2 | `E:\Program Files\Java\jdk` 是哪个版本？ | JDK 26（不是 25），但 Spring Boot 4.1 完全支持 |
| 3 | Spring Boot 新版本怎么下载？ | start.spring.io 官网生成，或 Maven 手动创建项目 |
| 4 | 枚举类型要不要用？ | 推荐关键业务状态用枚举（UserStatus、AppointmentStatus 等），简单的用 String |
| 5 | 前端传过来的是数字吗？ | 前后端统一用字符串传输枚举值（"ENABLED" 而不是 1），简单直观 |
| 6 | Spring AI 是什么技术？ | Spring 官方的 AI 集成框架，像 MyBatis 操作数据库一样操作 AI 模型，一行代码调接口 |
| 7 | 能不能用 Agnes（免费）？ | 可以！Agnes 是 OpenAI 兼容格式，配置 base-url 和 api-key 就能用 |
| 8 | api key 写在哪？ | 写在 `application.yaml` 的 `spring.ai.openai.api-key` |
| 9 | `.hermes-tmp.VtElBk` 是什么文件？ | Hermes 临时文件，空文件，可以删掉。已添加到 `.gitignore` |
| 10 | users 表里的密码是加密后加上去的吗？ | 是的，设计文档里给的 BCrypt 哈希值，对应密码 Admin@123456 和 Test@123456 |

### 用户犯的错误 / 需要注意的地方

| # | 错误/问题 | 说明 |
|---|---------|------|
| 1 | pom.xml 依赖写错了 | 写了 `spring-boot-starter-webmvc`，正确应该是 `spring-boot-starter-web` |
| 2 | 项目一开始用的是老版本 Spring Boot | 用户先用 IDEA 向导建的项目，是 Spring Boot 2.6.x，后来用户自己重建了 |
| 3 | 数据库密码没改 | application.yaml 里写的 `password: 123456`，用户说"我自己配置"，所以让小码没动，用户自己改 |

### 遇到的问题

| # | 问题 | 解决方案 |
|---|------|---------|
| 1 | 网络不好，start.spring.io 连不上 | 小码尝试了多种方式，最后用户自己在 IDEA 里建了项目 |
| 2 | Maven 没安装 | 项目里有 Maven Wrapper（mvnw），可以用，但 Windows 下网络问题导致下载失败 |
| 3 | IDEA 不识别 Maven 项目 | 小码创建了 `.idea/misc.xml`、`modules.xml`、`eldercare.iml` 配置文件 |

---

## 第 2 天 — 2026-08-15

### 今日工作内容

| 任务 | 状态 | 说明 |
|------|------|------|
| 用户编写 Result.java | ✅ | 统一响应封装，用了 Lombok、静态工厂方法 |
| 用户编写 User.java | ✅ | 用户实体类，17个字段都写全了 |
| 用户编写 RefreshToken.java | ✅ | 刷新令牌实体类 |
| 用户编写 smsCode.java（有小问题） | ⚠️ | 类名大小写、字段类型有问题，小码已修正 |

### 用户问的问题

无（今日自主开发）

### 今日代码 Review

#### Result.java 评价
- ✅ 结构清晰，静态工厂方法设计好
- ✅ 用了 Lombok 简化代码
- 💡 建议：`data` 字段改为 `public` 保持一致
- 💡 命名用 `fail` 而不是 `error` 也可以，看团队习惯

#### User.java 评价
- ✅ 字段完整，类型正确
- ✅ 用了 LocalDate、BigDecimal 等正确类型
- ✅ 注释清晰
- 💡 包名用的是 `core.pojo` 而不是 `core.entity`，不影响功能
- 💡 可考虑加 `@TableName("user")` 注解（如果后面用 MyBatis Plus）

#### RefreshToken.java 评价
- ✅ 字段正确，没有逻辑删除字段（符合设计文档）
- ✅ 没有写 createTime 注释，但字段名用了 creationTime，可以统一

#### ⚠️ smsCode.java 问题（已修正）
| 问题 | 用户的代码 | 修正后 |
|------|-----------|--------|
| 类名大小写 | `public class smsCode` | `public class SmsCode` |
| used 类型 | `Boolean used` | `Integer used` |
| createTime 类型 | `LocalDate creationTime` | `LocalDateTime createTime` |

---

## 第 3 天 — 2026-08-16

### 今日工作内容

| 任务 | 状态 | 说明 |
|------|------|------|
| 小码编写剩余 14 个实体类 | ✅ | 用户说都是重复代码，让小码帮忙写 |
| 用户问 POJO/DTO/VO 关系 | ✅ | 用比喻解释：学生、报名表、成绩单 |

### 用户问的问题

| # | 问题 | 小码的回答 |
|---|------|-----------|
| 1 | 你按照文档把实体类都给我写上吧，我写了三个了，剩下的都是重复代码 | 小码帮你检查了你写的三个，发现 smsCode 有几个问题，然后帮你写了剩下的 14 个 |
| 2 | pojo，dto，vo 的关系 | 用比喻解释：数据库的学生（POJO）、报名表格（DTO）、成绩单（VO） |

### 今日完成内容

#### 已存在的实体类（用户写的）
| # | 类名 | 状态 |
|---|------|------|
| 1 | User.java | ✅ 用户已写，没问题 |
| 2 | RefreshToken.java | ✅ 用户已写，没问题 |
| 3 | SmsCode.java | ⚠️ 用户写了但有问题，小码已修正 |

#### 小码帮写的实体类（14个）
| # | 类名 | 对应表名 | 说明 |
|---|------|---------|------|
| 4 | HealthRecord.java | health_record | 健康记录 |
| 5 | Questionnaire.java | questionnaire | 问卷 |
| 6 | Question.java | question | 题目 |
| 7 | AssessmentResult.java | assessment_result | 评测结果 |
| 8 | AppointmentPackage.java | appointment_package | 体检套餐 |
| 9 | AppointmentSlot.java | appointment_slot | 预约时段 |
| 10 | Appointment.java | appointment | 预约记录 |
| 11 | CommunityActivity.java | community_activity | 社区活动 |
| 12 | ActivityRegistration.java | activity_registration | 活动报名 |
| 13 | HealthGuidance.java | health_guidance | 健康指导 |
| 14 | AiConversationSession.java | ai_conversation_session | AI 会话 |
| 15 | AiConversationMessage.java | ai_conversation_message | AI 消息 |
| 16 | Message.java | message | 站内消息 |
| 17 | SysConfig.java | sys_config | 系统配置 |

### 实体类编写规范

所有实体类统一使用：
- ✅ Lombok 注解：`@Data`、`@NoArgsConstructor`、`@AllArgsConstructor`
- ✅ 时间类型：`LocalDateTime`（包含时分秒）或 `LocalDate`（仅日期）
- ✅ 金额/体重：`BigDecimal`
- ✅ 逻辑删除：`private Integer deleted`，0 未删除，1 已删除
- ✅ 状态字段：`String`（后面可以改成枚举）

---

## 第 4 天 — 2026-08-17

### 今日完成内容

| 任务 | 状态 | 说明 |
|------|------|------|
| 完成认证模块 · 验证码 | ✅ | SmsCodeMapper + XML + SmsUtil + SmsCodeDTO |
| send-code 发验证码接口 | ✅ | 手机号校验(^1\d{10}$) + 5 分钟过期 + 存库 |
| register 加验证码校验 | ✅ | 四步校验:判空→比对→过期→已使用 |
| reset-password 重置密码接口 | ✅ | 双密码校验 + 验证码四步校验 + 换锁强制下线 |
| 登录修复 | ✅ | 新增 findByPhoneWithPassword(带密码查询) |
| 修复 updateUser 参数绑定 Bug | ✅ | @Param("user") + XML 加 user. 前缀 |
| Spring Boot 4 测试依赖 | ✅ | 加了 spring-boot-starter-webmvc-test |
| 认证模块 6 个接口 | ✅ 全部完成 | send-code/register/login/refresh/logout/reset-password |
| 健康记录模块 CRUD | ✅ 完成 | 4 个接口:添加(自动算BMI)/分页/详情/逻辑删除 |
| 健康趋势分析接口 | ✅ 完成 | GET /api/health-records/trend,最近6个月 avg/max/min |
| BMI 自动计算 | ✅ 验证通过 | 身高170cm+体重68kg → BMI=23.5(体重/身高m²) |
| git 提交推送 | ✅ 完成 | commit 1a9a884,已 push 到 GitHub main
| 积分系统(宝宝写) | ✅ 完成 | 查看积分/流水/管理员调整/防超扣,5个用例全通过 |
| 积分bug修复(小码+宝宝) | ✅ | ① session→getAttribute ② adjustPoints补setAmount |
| 防超扣原子化改造 | ✅ | 扣分走 deductPoints(WHERE points>=扣减量),不再先扣后回滚,并发安全 |

### 今日用户问的问题

| # | 问题 | 小码的回答要点 |
|---|------|--------------|
| 1 | 验证码要真发短信吗？ | 开发阶段模拟打印日志，不花钱；生产才接阿里云短信 SDK |
| 2 | 重置密码流程？ | 一般前端分三步：发验证码→验码→输新密码，后端一个接口收齐即可 |
| 3 | IDEA 绿色光标补全时有时无 | 是 AI 补全插件，网络/额度影响，属正常现象 |
| 4 | Redis 怎么连？ | 启动 redis-server，端口 6379，Tiny RDM 可视化工具连接 |
| 5 | Spring Boot 4 测试怎么配？ | MockMvc 拆到 webmvc-test 模块，Jackson 3 包名是 tools.jackson |

### 今日踩坑记录（重要！）

**坑 1：send-code 里用 findByPhone 而不是 new SmsCode()**
- 发验证码要**新建**对象，注册/重置要**查询**旧记录，别搞混

**坑 2：登录报"手机号或密码错误"**
- 原因：findByPhone 的 SQL 没查 password 字段 → user.getPassword() 为 null
- 解决：新增 findByPhoneWithPassword 专门给登录用

**坑 3：updateUser 报 Parameter 'phone' not found**
- 原因：Mapper 方法两个参数时，XML 里 `test="phone"` 找不到参数名
- 解决：User 参数加 `@Param("user")`，XML 全部改 `user.xxx`
- 教训：`#{}` 取值会翻对象属性，`test=` 只认参数名！两种规则不一样

**坑 4：验证码被"吃掉"（系统异常但验证码已使用）**
- 原因：updateUsed 在 updateUser 之前执行，改密码失败验证码已作废
- 解决：加 @Transactional 事务 + 把 updateUsed 挪到成功之后

**坑 5：Spring Boot 4.x 测试环境（新版本大坑！）**
- `@AutoConfigureMockMvc` 不在 spring-boot-starter-test 里，要加 `spring-boot-starter-webmvc-test`
- Jackson 3 包名变成 `tools.jackson.databind`（不是 com.fasterxml）
- 测试类跑不通，暂时放弃，改用 Postman/接口工具手动测试

**坑 6：添加记录 500 "系统异常" — curl 中文编码坑（不是代码问题！）**
- 现象：curl 发 `memo:"小码测试"` → JSON parse error: Invalid UTF-8 middle byte 0xeb
- 原因：Windows bash 里 curl -d 的中文被编码成 GBK，服务器按 UTF-8 解析失败
- 解决：Apifox 正常(UTF-8)；命令行测试用纯英文 memo，或加 `--data-binary` 手动指定 UTF-8
- 教训：**先怀疑工具，再怀疑代码**！服务端堆栈 `HttpMessageNotReadableException` 一看就懂

**坑 7："登录已过期" 排查套路**
- 现象：明明登录成功，接口却 401
- 排查：1) 从 Apifox 响应复制 accessToken(不是日志里的 refreshToken) 2) 别带引号/Bearer 后要有空格 3) 用 Python 验签 token(secret在application.yaml) 最靠谱
- 本坑不是代码问题，是复制粘贴问题

## 明日计划

| 任务 | 优先级 | 说明 |
|------|--------|------|
| 开始积分系统（PointsService） | ⭐⭐⭐ | 按 MODULE_STEPS 顺序：健康记录 → 积分系统 |

---

## 明日计划

| 任务 | 优先级 | 说明 |
|------|--------|------|
| 开始会员端业务模块 | ⭐⭐⭐ | 按 MODULE_STEPS 第二部分：健康记录 HealthRecord |
| HealthRecordMapper + XML | ⭐⭐⭐ | 照 UserMapper 的样式写 |
| HealthRecordService + impl | ⭐⭐⭐ | 业务逻辑层 |
| HealthRecordController | ⭐⭐⭐ | api/controller 下，分页查询 |
| Postman 验证健康记录 CRUD | ⭐⭐⭐ | 4 个接口:增/查/详情/删 |

---

## 项目文件结构（更新）

```
eldercare/
├── pom.xml                          ✅ 依赖完整
├── src/main/java/com/wmm/eldercare/
│   ├── ElderCareApplication.java    ✅ 启动类
│   ├── admin/                       ✅ 目录已建
│   ├── api/                         ✅ 目录已建
│   └── core/                        ✅ 目录已建
│       ├── common/
│       │   └── Result.java          ✅ 用户已写
│       ├── config/
│       ├── controller/
│       ├── dto/
│       ├── enums/
│       ├── exception/
│       ├── filter/
│       ├── mapper/
│       ├── pojo/
│       │   ├── User.java            ✅ 用户已写
│       │   ├── RefreshToken.java    ✅ 用户已写
│       │   ├── SmsCode.java         ✅ 小码已写（修正版）
│       │   ├── HealthRecord.java    ✅ 小码已写
│       │   ├── Questionnaire.java   ✅ 小码已写
│       │   ├── Question.java        ✅ 小码已写
│       │   ├── AssessmentResult.java ✅ 小码已写
│       │   ├── AppointmentPackage.java ✅ 小码已写
│       │   ├── AppointmentSlot.java ✅ 小码已写
│       │   ├── Appointment.java     ✅ 小码已写
│       │   ├── CommunityActivity.java ✅ 小码已写
│       │   ├── ActivityRegistration.java ✅ 小码已写
│       │   ├── HealthGuidance.java  ✅ 小码已写
│       │   ├── AiConversationSession.java ✅ 小码已写
│       │   ├── AiConversationMessage.java ✅ 小码已写
│       │   ├── Message.java         ✅ 小码已写
│       │   └── SysConfig.java       ✅ 小码已写
│       ├── service/
│       ├── util/
│       └── vo/
├── src/main/resources/
│   ├── application.yaml             ✅ 已配置
│   ├── sql/
│   │   └── init.sql                 ✅ 建表脚本
│   └── static/
├── IMPLEMENTATION_GUIDE.md          ✅
├── DEVELOPMENT_LOG.md               ✅
├── MODULE_STEPS.md                  ✅
├── ENTITY_CHECKLIST.md              ✅
└── docs/
    └── agnes-config.md              ✅
```

---

## 第 5 天 — 2026-08-18

### 今日工作内容

| 任务 | 状态 | 说明 |
|------|------|------|
| 健康评测模块(宝宝写) | ✅ 完成 | 问卷列表/详情/提交(AI评分)/历史查询 |
| AI对话模块(宝宝写) | ✅ 完成 | 会话增删查/消息发送(AI回复)/消息历史 |
| 小码查看+测试评测/AI对话 | ✅ | 发现并修复 4 处问题(见踩坑) |
| 体检预约模块(宝宝写) | ✅ 完成 | 套餐列表/按时段查询/预约/取消/我的预约 |
| 异常统一换成 BusinessException | ✅ | 宝宝提出,预约模块抛错带状态码,与其他模块一致 |
| git 提交推送 | ✅ 完成 | commit 6636f2c + d775919,已 push 到 GitHub main |

### 今日用户问的问题

| # | 问题 | 小码的回答要点 |
|---|------|--------------|
| 1 | 昨天写完健康记录,接下来写什么? | 还剩5个模块:健康评测⭐、AI对话⭐⭐、体检预约⭐⭐⭐⭐、社区活动、消息通知;推荐先做依赖最少的健康评测 |
| 2 | 就剩这五个模块了吗? | 对,会员端业务模块就这些;管理端+前端两个还没开始 |
| 3 | 预约模块的异常应该抛什么? | 统一抛 `BusinessException(code, message)`,和其他 Service 保持一致(宝宝自己判断对了!) |
| 4 | 我为什么不让你修改方法? | 宝宝只是想让小码看其他 Service 的异常风格,不是让改方法签名——**先问清楚需求再动手** |

### 今日踩坑记录(重要!)

**坑 1:评测提交 500 — addPoints 漏 setAmount**
- 现象:提交评测后加分报错
- 原因:UserPointRecord 流水没 setAmount,amount 为 null 插不进 NOT NULL 列
- 教训:insert 前对照表结构逐字段核对

**坑 2:Spring AI 2.0 配置变了**
- 现象:ChatClient 注入失败/AI 调用不了
- 解决:需要 `spring.ai.model.chat=openai` 配置 + 自定义 ChatClient Bean(新建 AiChatConfig)

**坑 3:评测历史查不到 — 结果漏 setDeleted**
- 现象:提交成功但历史查询为空
- 原因:插入 AssessmentResult 没 setDeleted(0),查询条件 `deleted=0` 匹配不到
- 教训:写 insert 时业务表的 deleted/status 等公共字段必须设置

**坑 4:AI 返回 JSON 解析失败**
- 现象:AI 评分返回带 markdown 代码块(```json ... ```),直接 parse 失败
- 解决:解析前先剥离 markdown 代码块,健壮解析

**坑 5:patch 工具改缩进翻车(小码的坑!)**
- 现象:多空格缩进行的替换产生重复缩进,AssessmentServiceImpl 修了 3 次
- 教训:改大段缩进/多行代码不要用 patch 抠,read_file 读全文件 → write_file 全量重写

**坑 6:write_file 写 XML 转义引号**
- 现象:写入的 XML 双引号变成 `\"`,MyBatis 解析报错
- 解决:`sed -i 's/\\\"/"/g' *.xml` 全局修复(Questionnaire/Question/AssessmentResult 的 XML 都中招)

### 明日计划

| 任务 | 优先级 | 说明 |
|------|--------|------|
| 社区活动模块 | ⭐⭐⭐ | 会员端最后一个纯业务模块:活动列表/详情/报名/我的报名 |
| 消息模块 | ⭐⭐ | 站内消息:列表/已读/未读数 |
| 个人中心 | ⭐⭐ | 资料查询/修改/改密码收尾 |
| 管理端 7 个模块 | ⭐⭐ | 用户/健康记录/评测/预约/活动/消息/配置管理 |

### 项目进度速览

```
认证 ✅ → 健康记录 ✅ → 积分系统 ✅ → 健康评测 ✅ → AI对话 ✅ → 体检预约 ✅
会员端剩余:社区活动 / 站内消息 / 个人中心
管理端:7 个模块待开发(admin 下现有 Auth/User/PointsAdmin)
前端:两个项目(eldercare-member / eldercare-admin)还没开始
```

---

**日志持续更新中...**
