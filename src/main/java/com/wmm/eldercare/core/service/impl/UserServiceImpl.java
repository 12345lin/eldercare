package com.wmm.eldercare.core.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wmm.eldercare.core.common.BusinessException;
import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.mapper.UserMapper;
import com.wmm.eldercare.core.mapper.UserPointRecordMapper;
import com.wmm.eldercare.core.mapper.PointTransactionMapper;
import com.wmm.eldercare.core.pojo.User;
import com.wmm.eldercare.core.pojo.UserPointRecord;
import com.wmm.eldercare.core.pojo.PointTransaction;
import com.wmm.eldercare.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    private final UserPointRecordMapper userPointRecordMapper;

    private final PointTransactionMapper pointTransactionMapper;

    @Override
    @Transactional
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
        if (user.getPoints() == null) {
            user.setPoints(0);  // 默认0积分，后续赠送100
        }
        int rows = userMapper.addUser(user);
        if (rows == 0) {
            throw new BusinessException(400, "添加用户失败");
        } else {
            // 赠送100积分
            userMapper.updatePoints(user.getId(), 100);
            // 查询更新后的余额
            int balance = userMapper.selectPoints(user.getId());
            // 记录用户积分变化
            UserPointRecord userPointRecord = new UserPointRecord();
            userPointRecord.setUserId(user.getId());
            userPointRecord.setAmount(100);
            userPointRecord.setType("REGISTRATION");
            userPointRecord.setBalance(balance);  // 设置余额
            userPointRecord.setReason("用户注册");
            userPointRecord.setCreateTime(LocalDateTime.now());
            userPointRecord.setDeleted(0);
            userPointRecordMapper.insert(userPointRecord);
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
     * 添加用户积分,并记录积分变化
         * @param userId
     * @param amount
     * @param type
     * @param reason
     * @return
     */
    @Override
    @Transactional
    public Integer addPoints(Long userId, Integer amount, String type, String reason) {
        User user = userMapper.findUserById(userId);
        // 1.检查用户是否存在
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        //2.更新用户积分
        int rows = userMapper.updatePoints(userId, amount);
        if (rows == 0) {
            throw new BusinessException(400, "更新用户积分失败");
        }
        //3.查询更新后的用户积分
        int balance = userMapper.selectPoints(userId);
        //4.更新用户积分记录（保持原有 user_point_record 兼容）
        UserPointRecord userPointRecord = new UserPointRecord();
        userPointRecord.setUserId(userId);
        userPointRecord.setType(type);
        userPointRecord.setAmount(amount);
        userPointRecord.setReason(reason);
        userPointRecord.setBalance(balance);
        userPointRecord.setCreateTime(LocalDateTime.now());
        userPointRecordMapper.insert(userPointRecord);
        //5.写入 point_transaction 获得流水（带 expire_time = 1年后，remain=amount，支持 FIFO 消费与过期）
        PointTransaction tx = new PointTransaction();
        tx.setUserId(userId);
        tx.setType(type);
        tx.setChangeAmount(amount);
        tx.setBalanceAfter(balance);
        tx.setRemainAmount(amount);
        tx.setExpireTime(LocalDateTime.now().plusYears(1));
        tx.setDescription(reason);
        tx.setCreateTime(LocalDateTime.now());
        tx.setUpdateTime(LocalDateTime.now());
        tx.setDeleted(0);
        pointTransactionMapper.insert(tx);
        return balance;
    }

    /**
     * 扣除用户积分,并记录积分变化
     * @param userId
     * @param amount
     * @param type
     * @param reason
     * @return
     */
    @Override
    @Transactional
    public Integer deductPoints(Long userId, Integer amount, String type, String reason) {
        User user = userMapper.findUserById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        // 让 SQL 的原子条件来保证安全，不用 Java 再判断
        int rows = userMapper.deductPoints(userId, amount);
        if (rows == 0) {
            throw new BusinessException(400, "用户积分不足");
        }
        int balance = userMapper.selectPoints(userId);
        UserPointRecord record = new UserPointRecord();
        record.setUserId(userId);
        record.setType(type);
        record.setBalance(balance);
        // 注意：扣减时 amount 应该记负数，方便查流水
        record.setAmount(-amount);
        record.setReason(reason);
        record.setCreateTime(LocalDateTime.now());
        userPointRecordMapper.insert(record);
        // ===== FIFO 消费：按最早获得批次扣减 remain_amount =====
        int deductRemaining = amount;
        List<PointTransaction> batches = pointTransactionMapper.findAvailableBatches(userId);
        for (PointTransaction batch : batches) {
            if (deductRemaining <= 0) break;
            int fromBatch = Math.min(deductRemaining, batch.getRemainAmount());
            pointTransactionMapper.reduceRemain(batch.getId(), fromBatch);
            // 生成消费流水
            PointTransaction consumeTx = new PointTransaction();
            consumeTx.setUserId(userId);
            consumeTx.setType(type);
            consumeTx.setChangeAmount(-fromBatch);
            consumeTx.setBalanceAfter(balance);
            consumeTx.setRemainAmount(0);
            consumeTx.setBatchTxId(batch.getId());
            consumeTx.setDescription(reason);
            consumeTx.setCreateTime(LocalDateTime.now());
            consumeTx.setUpdateTime(LocalDateTime.now());
            consumeTx.setDeleted(0);
            pointTransactionMapper.insert(consumeTx);
            deductRemaining -= fromBatch;
        }
        return balance;
    }

    /**
     * 调整用户积分,并记录积分变化
     * @param userId
     * @param amount
     * @param type
     * @param reason
     * @return
     */
    @Override
    @Transactional
    public Integer adjustPoints(Long userId, Integer amount, String type, String reason) {
        //1.查询用户
        User user = userMapper.findUserById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        //2.原子更新积分：正数加分走 updatePoints（points + amount）
        //  负数扣分走 deductPoints（WHERE points >= 扣减量，不够直接返回0，不会扣成负数）
        int rows;
        if (amount >= 0) {
            rows = userMapper.updatePoints(userId, amount);
        } else {
            rows = userMapper.deductPoints(userId, -amount);
        }
        if (rows == 0) {
            throw new BusinessException(400, "调整后积分不能为负数");
        }
        //3.查询更新后的余额
        int balance = userMapper.selectPoints(userId);
        //4.写入积分流水记录
        UserPointRecord userPointRecord = new UserPointRecord();
        userPointRecord.setUserId(userId);
        userPointRecord.setType(type);
        userPointRecord.setAmount(amount);
        userPointRecord.setReason(reason);
        userPointRecord.setBalance(balance);
        userPointRecord.setCreateTime(LocalDateTime.now());
        userPointRecordMapper.insert(userPointRecord);
        return balance;
    }

    /**
     * 查询用户积分
     * @param userId
     * @return
     */
    @Override
    public Integer getPoints(Long userId) {
        return userMapper.selectPoints(userId);
    }

    /**
     * 分页查询用户列表
     * @param pageNum
     * @param pageSize
     * @return
     */
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

    /**
     * 分页查询用户积分流水记录
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @Override
    public PageResult<UserPointRecord> listPointRecords(Long userId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<UserPointRecord> records = userPointRecordMapper.listByUserId(userId, offset, pageSize);
        long total = userPointRecordMapper.countByUserId(userId);
        int pages = (int) Math.ceil((double) total / pageSize);
        return new PageResult<>(total, records, pageNum, pageSize, pages);
    }
}