# 实体类编写清单

> 根据 `02-AI 智能养老社区管理系统-项目详细设计文档.md` 第 6.3 节编写
> 项目路径：`E:\DevProject\Java_Project\Web-Project\eldercare`

---

## 实体类清单（17个）

| # | 类名 | 文件路径 | 对应表名 | 状态 | 编写者 |
|---|------|---------|---------|------|--------|
| 1 | User | `core/pojo/User.java` | user | ✅ 已完成 | 用户 |
| 2 | RefreshToken | `core/pojo/RefreshToken.java` | refresh_token | ✅ 已完成 | 用户 |
| 3 | SmsCode | `core/pojo/SmsCode.java` | sms_code | ✅ 已完成（已修正） | 小码（修正用户版本） |
| 4 | HealthRecord | `core/pojo/HealthRecord.java` | health_record | ✅ 已完成 | 小码 |
| 5 | Questionnaire | `core/pojo/Questionnaire.java` | questionnaire | ✅ 已完成 | 小码 |
| 6 | Question | `core/pojo/Question.java` | question | ✅ 已完成 | 小码 |
| 7 | AssessmentResult | `core/pojo/AssessmentResult.java` | assessment_result | ✅ 已完成 | 小码 |
| 8 | AppointmentPackage | `core/pojo/AppointmentPackage.java` | appointment_package | ✅ 已完成 | 小码 |
| 9 | AppointmentSlot | `core/pojo/AppointmentSlot.java` | appointment_slot | ✅ 已完成 | 小码 |
| 10 | Appointment | `core/pojo/Appointment.java` | appointment | ✅ 已完成 | 小码 |
| 11 | CommunityActivity | `core/pojo/CommunityActivity.java` | community_activity | ✅ 已完成 | 小码 |
| 12 | ActivityRegistration | `core/pojo/ActivityRegistration.java` | activity_registration | ✅ 已完成 | 小码 |
| 13 | HealthGuidance | `core/pojo/HealthGuidance.java` | health_guidance | ✅ 已完成 | 小码 |
| 14 | AiConversationSession | `core/pojo/AiConversationSession.java` | ai_conversation_session | ✅ 已完成 | 小码 |
| 15 | AiConversationMessage | `core/pojo/AiConversationMessage.java` | ai_conversation_message | ✅ 已完成 | 小码 |
| 16 | Message | `core/pojo/Message.java` | message | ✅ 已完成 | 小码 |
| 17 | SysConfig | `core/pojo/SysConfig.java` | sys_config | ✅ 已完成 | 小码 |

---

## 编写记录

### 第 1 天（2026-08-14）
- 小码帮助用户搭建项目骨架
- 用户自己编写了 User.java

### 第 2 天（2026-08-15）
- 用户自己编写了 Result.java（统一响应）
- 用户自己编写了 User.java（实体类）
- 用户自己编写了 RefreshToken.java（实体类）
- 用户编写了 smsCode.java，但有 3 个小问题：
  - 类名大小写错误：`smsCode` → `SmsCode`
  - used 字段类型错误：`Boolean` → `Integer`
  - createTime 字段类型错误：`LocalDate` → `LocalDateTime`
- 小码修正了这 3 个问题

### 第 3 天（2026-08-16）
- 用户说剩下的 14 个都是重复代码，让小码帮忙写
- 小码按照设计文档编写了所有剩余实体类
- 所有实体类统一使用 Lombok 注解简化代码

---

## 实体类模板

```java
package com.wmm.eldercare.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * XXX 实体类
 *
 * @author wmm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Xxx {
    private Long id;                    // 主键 ID
    // ... 其他字段
    private LocalDateTime createTime;   // 创建时间
    private LocalDateTime updateTime;   // 更新时间
    private Integer deleted;            // 逻辑删除：0 未删除/1 已删除
}
```

---

## 下一步

实体类全部写完！接下来要写：

1. **Mapper 接口**（17个）— 数据访问层
2. **Mapper XML**（17个）— SQL 映射文件
3. **JwtUtil** — JWT 工具类
4. **AuthController** — 登录注册接口

---

**全部实体类编写完成！✅**
