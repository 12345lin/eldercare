package com.wmm.eldercare.core.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wmm.eldercare.core.common.BusinessException;
import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.mapper.UserMapper;
import com.wmm.eldercare.core.pojo.User;
import com.wmm.eldercare.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public int addUser(User user) {
        // 调用数据访问层添加用户
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setDeleted(0);
        // 设置用户默认值
        if (user.getRole() == null) {
            user.setRole("MEMBER");
        }
        if (user.getStatus() == null) {
            user.setStatus("ENABLED");
        }
        if (user.getMemberLevel() == null) {
            user.setMemberLevel("NORMAL");
        }
        int rows = userMapper.addUser(user);
        if (rows == 0) {
            throw new BusinessException(400, "添加用户失败");
        } else {
            user.setPassword(null);
            return rows;
        }
    }


    /**
     * 根据用户ID查询用户
     * @param id
     * @return
     */
    @Override
    public User findUserById(Long id) {
        // 调用数据访问层根据用户ID查询用户
        User user = userMapper.findUserById(id);
        // 检查用户是否存在
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    /**
     * 更新用户
     * @param id
     * @param user
     * @return
     */
    @Override
    public int updateUser(Long id, User user) {
        // 调用数据访问层更新用户
        user.setUpdateTime(LocalDateTime.now());
        int rows  = userMapper.updateUser(id,user);
        if (rows == 0) {
            throw new BusinessException(400, "更新用户失败");
        } else {
            return rows;
        }
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @Override
    public int deleteUser(Long id) {
        // 调用数据访问层删除用户
        int rows = userMapper.deleteUser(id);
        if (rows == 0) {
            throw new BusinessException(400, "删除用户失败");
        } else {
            return rows;
        }
    }

    /**
     * 批量删除用户
     * @param ids
     * @return
     */
    @Override
    public int batchDeleteUsers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "ID列表不能为空");
        }
        int rows = userMapper.batchDeleteUsers(ids);
        if (rows == 0) {
            throw new BusinessException(400, "批量删除用户失败");
        }
        return rows;
    }

    /**
     * 根据手机号查询用户
     * @param phone
     * @return
     */
    @Override
    public User findByPhone(String phone) {
        return userMapper.findByPhone(phone);
    }

    /**
     * 登录专用：根据用户手机号查询用户（包含密码）
     * @param phone
     * @return
     */
    @Override
    public User findByPhoneWithPassword(String phone) {
        return userMapper.findByPhoneWithPassword(phone);
    }

    /**
     * 分页查询用户
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult<User> listUsers(Integer pageNum, Integer pageSize) {
        // 1. 开启分页（PageHelper 会自动在 SQL 后加 LIMIT）
        PageHelper.startPage(pageNum, pageSize);

        // 2. 查询数据（PageHelper 会自动拦截并分页）
        List<User> users = userMapper.listUsers();

        // 3. 封装分页结果
        PageInfo<User> pageInfo = new PageInfo<>(users);
        return new PageResult<>(
                pageInfo.getTotal(),
                pageInfo.getList(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getPages()
        );
    }
}