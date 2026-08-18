package com.wmm.eldercare.core.service;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.pojo.User;
import com.wmm.eldercare.core.pojo.UserPointRecord;

import java.util.List;

public interface UserService {
    int addUser(User user);

    User findUserById(Long id);

    int updateUser(Long id, User user);

    int deleteUser(Long id);

    PageResult<User> listUsers(Integer pageNum, Integer pageSize);

    int batchDeleteUsers(List<Long> ids);

    User findByPhone(String phone);

    User findByPhoneWithPassword(String phone);

    Integer addPoints(Long userId, Integer amount, String type, String reason);

    Integer deductPoints(Long userId, Integer amount, String type, String reason);

    Integer adjustPoints(Long userId, Integer amount, String type, String reason);

    Integer getPoints(Long userId);

    PageResult<UserPointRecord> listPointRecords(Long userId, Integer pageNum, Integer pageSize);
}