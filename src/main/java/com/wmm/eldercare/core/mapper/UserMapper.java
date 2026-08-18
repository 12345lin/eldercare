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
}
