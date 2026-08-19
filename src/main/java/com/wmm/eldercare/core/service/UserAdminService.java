package com.wmm.eldercare.core.service;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.pojo.User;

import java.util.List;

/**
 * 管理端用户管理 Service
 */
public interface UserAdminService {

    /**
     * 分页查询用户列表（keyword 按手机号/姓名模糊搜索）
     *
     * @param keyword  搜索关键字（可空）
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageResult<User> listUsers(String keyword, Integer pageNum, Integer pageSize);

    /**
     * 查询用户详情（清除密码字段）
     *
     * @param id 用户 ID
     * @return 用户信息
     */
    User getUser(Long id);

    /**
     * 新增用户（校验手机号唯一 + 密码加密）
     *
     * @param user 用户信息
     */
    void addUser(User user);

    /**
     * 修改用户信息
     *
     * @param id   用户 ID
     * @param user 修改内容
     */
    void updateUser(Long id, User user);

    /**
     * 启用/禁用用户
     *
     * @param id     用户 ID
     * @param status ENABLED / DISABLED
     */
    void updateStatus(Long id, String status);

    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户 ID
     */
    void deleteUser(Long id);

    /**
     * 批量删除用户
     *
     * @param ids 用户 ID 列表
     */
    void batchDeleteUsers(List<Long> ids);
}