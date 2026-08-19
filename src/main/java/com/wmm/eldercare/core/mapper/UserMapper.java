package com.wmm.eldercare.core.mapper;

import com.wmm.eldercare.core.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    int addUser(User user);

    User findUserById(Long id);

    // 个人中心/改密码专用：查询用户时带上密码字段（用于旧密码校验）
    User findByIdWithPassword(@Param("id") Long id);

    int updateUser(@Param("userId") Long id, @Param("user") User user);

    int deleteUser(Long id);

    List<User> listUsers();

    int batchDeleteUsers(@Param("ids") List<Long> ids);

    User findByPhone(@Param("phone") String phone);

    // 登录专用：查询用户时带上密码字段（用于密码校验）
    User findByPhoneWithPassword(@Param("phone") String phone);

    int updatePoints(@Param("userId") Long userId, @Param("points") Integer points);

    int deductPoints(@Param("userId") Long userId, @Param("points") Integer points);

    int selectPoints(@Param("userId") Long userId);

    /**
     * 管理端分页查询用户列表（keyword 空查全部，非空按手机号/姓名模糊搜索；不查密码）
     */
    List<User> findAll(@Param("keyword") String keyword);

    /**
     * 管理端启用/禁用用户（更新 status 字段）
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}
