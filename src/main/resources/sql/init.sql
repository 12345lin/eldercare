-- =============================================
-- AI 智能养老社区管理系统 - 建表脚本
-- 数据库: eldercare
-- 字符集: utf8mb4
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS eldercare 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_0900_ai_ci;

USE eldercare;

-- =============================================
-- 1. 用户表（user）
-- =============================================
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt 加密）',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别',
  `birth_date` DATE DEFAULT NULL COMMENT '出生日期',
  `height` DECIMAL(5,1) DEFAULT NULL COMMENT '身高（cm）',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像 URL',
  `emergency_contact` VARCHAR(20) DEFAULT NULL COMMENT '紧急联系人电话',
  `member_level` VARCHAR(20) DEFAULT 'NORMAL' COMMENT '会员等级：NORMAL/SILVER/GOLD/PLATINUM/DIAMOND',
  `points` INT DEFAULT 0 COMMENT '积分',
  `status` VARCHAR(20) DEFAULT 'ENABLED' COMMENT '状态：ENABLED/DISABLED',
  `role` VARCHAR(20) NOT NULL COMMENT '角色：MEMBER/ADMIN',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0 未删除/1 已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_status` (`status`),
  KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- =============================================
-- 2. 刷新令牌表（refresh_token）
-- =============================================
CREATE TABLE `refresh_token` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `token` VARCHAR(500) NOT NULL COMMENT 'Refresh Token 值',
  `expire_time` DATETIME NOT NULL COMMENT '过期时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='刷新令牌表';

-- =============================================
-- 3. 短信验证码表（sms_code）
-- =============================================
CREATE TABLE `sms_code` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录 ID',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `code` VARCHAR(10) NOT NULL COMMENT '验证码',
  `expire_time` DATETIME NOT NULL COMMENT '过期时间',
  `used` TINYINT DEFAULT 0 COMMENT '是否已使用：0 未使用/1 已使用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_phone` (`phone`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='短信验证码表';

-- =============================================
-- 4. 健康记录表（health_record）
-- =============================================
CREATE TABLE `health_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `systolic` INT DEFAULT NULL COMMENT '收缩压（mmHg）',
  `diastolic` INT DEFAULT NULL COMMENT '舒张压（mmHg）',
  `blood_sugar` DECIMAL(4,1) DEFAULT NULL COMMENT '血糖（mmol/L）',
  `heart_rate` INT DEFAULT NULL COMMENT '心率（次/分）',
  `weight` DECIMAL(4,1) DEFAULT NULL COMMENT '体重（kg）',
  `bmi` DECIMAL(3,1) DEFAULT NULL COMMENT 'BMI 指数',
  `memo` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `recorded_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_recorded` (`user_id`, `recorded_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='健康记录表';

-- =============================================
-- 5. 问卷表（questionnaire）
-- =============================================
CREATE TABLE `questionnaire` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '问卷 ID',
  `title` VARCHAR(200) NOT NULL COMMENT '问卷标题',
  `description` VARCHAR(1000) DEFAULT NULL COMMENT '问卷描述',
  `status` VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态：DRAFT 草稿/PUBLISHED 已发布',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='问卷表';

-- =============================================
-- 6. 题目表（question）
-- =============================================
CREATE TABLE `question` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '题目 ID',
  `questionnaire_id` BIGINT NOT NULL COMMENT '问卷 ID',
  `content` VARCHAR(500) NOT NULL COMMENT '题目内容',
  `type` VARCHAR(20) NOT NULL COMMENT '类型：SINGLE 单选/MULTIPLE 多选/TEXT 文本',
  `options` JSON DEFAULT NULL COMMENT '选项 JSON 数组',
  `sort_order` INT DEFAULT 0 COMMENT '排序号',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_questionnaire_id` (`questionnaire_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='题目表';

-- =============================================
-- 7. 评测结果表（assessment_result）
-- =============================================
CREATE TABLE `assessment_result` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评测结果 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `questionnaire_id` BIGINT NOT NULL COMMENT '问卷 ID',
  `answers` JSON DEFAULT NULL COMMENT '答案快照 JSON',
  `ai_score` INT DEFAULT NULL COMMENT 'AI 评分（百分制）',
  `ai_suggestion` TEXT DEFAULT NULL COMMENT 'AI 建议',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_questionnaire_id` (`questionnaire_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评测结果表';

-- =============================================
-- 8. 体检套餐表（appointment_package）
-- =============================================
CREATE TABLE `appointment_package` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '套餐 ID',
  `name` VARCHAR(200) NOT NULL COMMENT '套餐名称',
  `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图 URL',
  `description` TEXT DEFAULT NULL COMMENT '套餐描述',
  `price` INT DEFAULT 0 COMMENT '价格（积分抵扣）',
  `suitable_people` VARCHAR(200) DEFAULT NULL COMMENT '适合人群',
  `items` JSON DEFAULT NULL COMMENT '包含项目列表 JSON',
  `status` VARCHAR(20) DEFAULT 'ENABLED' COMMENT '状态：ENABLED/DISABLED',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='体检套餐表';

-- =============================================
-- 9. 预约时段表（appointment_slot）
-- =============================================
CREATE TABLE `appointment_slot` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '时间段 ID',
  `package_id` BIGINT NOT NULL COMMENT '套餐 ID',
  `appoint_date` DATE NOT NULL COMMENT '预约日期',
  `time_range` VARCHAR(50) NOT NULL COMMENT '时间段，如 09:00-10:00',
  `max_count` INT DEFAULT 10 COMMENT '最大预约人数',
  `current_count` INT DEFAULT 0 COMMENT '当前已预约人数',
  `status` VARCHAR(20) DEFAULT 'AVAILABLE' COMMENT '状态：AVAILABLE/FULL/CLOSED',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_package_date` (`package_id`, `appoint_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='预约时段表';

-- =============================================
-- 10. 预约表（appointment）
-- =============================================
CREATE TABLE `appointment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '预约 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `slot_id` BIGINT NOT NULL COMMENT '时间段 ID',
  `package_id` BIGINT NOT NULL COMMENT '套餐 ID',
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING/CONFIRMED/CANCELED/COMPLETED',
  `report_url` VARCHAR(500) DEFAULT NULL COMMENT '体检报告 URL',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_slot_id` (`slot_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='预约表';

-- =============================================
-- 11. 社区活动表（community_activity）
-- =============================================
CREATE TABLE `community_activity` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '活动 ID',
  `title` VARCHAR(200) NOT NULL COMMENT '活动标题',
  `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图 URL',
  `content` TEXT DEFAULT NULL COMMENT '活动内容',
  `registration_start` DATETIME DEFAULT NULL COMMENT '报名开始时间',
  `registration_end` DATETIME DEFAULT NULL COMMENT '报名结束时间',
  `activity_start` DATETIME DEFAULT NULL COMMENT '活动开始时间',
  `activity_end` DATETIME DEFAULT NULL COMMENT '活动结束时间',
  `max_participants` INT DEFAULT NULL COMMENT '人数上限',
  `current_participants` INT DEFAULT 0 COMMENT '当前报名人数',
  `status` VARCHAR(20) DEFAULT 'DRAFT' COMMENT '状态：DRAFT/REGISTRATING/IN_PROGRESS/ENDED',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社区活动表';

-- =============================================
-- 12. 活动报名表（activity_registration）
-- =============================================
CREATE TABLE `activity_registration` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '报名 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `activity_id` BIGINT NOT NULL COMMENT '活动 ID',
  `check_in_status` VARCHAR(20) DEFAULT 'NOT_CHECKED_IN' COMMENT '签到状态：NOT_CHECKED_IN/CHECKED_IN',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_activity` (`user_id`, `activity_id`),
  KEY `idx_activity_id` (`activity_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='活动报名表';

-- =============================================
-- 13. 健康指导表（health_guidance）
-- =============================================
CREATE TABLE `health_guidance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '指导 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `type` VARCHAR(20) NOT NULL COMMENT '类型：DIET/EXERCISE/DAILY/DATA_SUMMARY',
  `content` TEXT NOT NULL COMMENT '指导内容',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读：0 未读/1 已读',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_type` (`user_id`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='健康指导表';

-- =============================================
-- 14. AI 会话表（ai_conversation_session）
-- =============================================
CREATE TABLE `ai_conversation_session` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '会话 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `session_name` VARCHAR(100) DEFAULT NULL COMMENT '会话名称',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI 会话表';

-- =============================================
-- 15. AI 对话消息表（ai_conversation_message）
-- =============================================
CREATE TABLE `ai_conversation_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息 ID',
  `session_id` BIGINT NOT NULL COMMENT '会话 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `role` VARCHAR(20) NOT NULL COMMENT '角色：user/assistant',
  `message` TEXT NOT NULL COMMENT '消息内容',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_session_id` (`session_id`),
  KEY `idx_user_session` (`user_id`, `session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI 对话消息表';

-- =============================================
-- 16. 消息表（message）
-- =============================================
CREATE TABLE `message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `title` VARCHAR(200) NOT NULL COMMENT '消息标题',
  `content` TEXT DEFAULT NULL COMMENT '消息内容',
  `type` VARCHAR(20) DEFAULT NULL COMMENT '消息类型：APPOINTMENT/ACTIVITY/SYSTEM',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读：0 未读/1 已读',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='站内消息表';

-- =============================================
-- 17. 系统配置表（sys_config）
-- =============================================
CREATE TABLE `sys_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置 ID',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` TEXT DEFAULT NULL COMMENT '配置值',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '配置描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统配置表';

-- =============================================
-- 初始化数据
-- =============================================

-- 系统配置
INSERT INTO sys_config (config_key, config_value, description) VALUES
('ai_chat_system_prompt', '你是一位专业的健康顾问，请用亲切、易懂的语言回答用户的健康问题。', 'AI 对话系统提示词'),
('register_bonus_points', '100', '注册赠送积分'),
('checkin_bonus_points', '50', '活动签到赠送积分'),
('health_assessment_min_score', '60', '健康评测及格分数线'),
('access_token_expire_hours', '2', 'Access Token 有效期（小时）'),
('refresh_token_expire_days', '7', 'Refresh Token 有效期（天）');

-- 默认管理员（密码：Admin@123456）
INSERT INTO user (phone, password, real_name, member_level, points, status, role) VALUES
('13800000000', '$2b$10$5xxJYAxX3bB35VkjlRAuauILyrcKEUXJINVQXrWPYl6vhfZlIiy46', '系统管理员', 'PLATINUM', 99999, 'ENABLED', 'ADMIN');

-- 测试会员（密码：Test@123456）
INSERT INTO user (phone, password, real_name, member_level, points, status, role) VALUES
('13800138000', '$2b$10$La.Q.aZ.SUB5Ej3neFdzGOUYLva/QuO7sALyOPaBCxyYkro9Cpzjm', '测试用户', 'NORMAL', 1000, 'ENABLED', 'MEMBER');

-- 示例问卷
INSERT INTO questionnaire (title, description, status) VALUES
('基础健康状况调查问卷', '通过简单的问题了解您的基本健康状况', 'PUBLISHED');

-- 问卷题目
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
