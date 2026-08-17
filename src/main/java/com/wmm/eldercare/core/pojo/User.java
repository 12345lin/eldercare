package com.wmm.eldercare.core.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * @author wmm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;                    // 用户ID
    private String phone;               // 手机号
    private String password;            // 密码
    private String realName;            // 真实姓名
    private String gender;              // 性别
    private LocalDate birthDate;        // 出生日期
    private BigDecimal height;          // 身高
    private String avatar;              // 头像URL
    private String emergencyContact;    // 紧急联系人
    private String memberLevel;         // 会员等级（NORMAL/SILVER/GOLD/PLATINUM/DIAMOND）
    private Integer points;             // 积分
    private String status;              // 状态（ENABLED/DISABLED）
    private String role;                // 角色（MEMBER/ADMIN）
    private LocalDateTime createTime;   // 创建时间
    private LocalDateTime updateTime;   // 更新时间
    private Integer deleted;            // 逻辑删除标记（0-未删除，1-已删除）
}