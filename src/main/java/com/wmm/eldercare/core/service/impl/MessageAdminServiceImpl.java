package com.wmm.eldercare.core.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wmm.eldercare.core.common.BusinessException;
import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.dto.MessageSendDTO;
import com.wmm.eldercare.core.mapper.MessageMapper;
import com.wmm.eldercare.core.mapper.UserMapper;
import com.wmm.eldercare.core.pojo.Message;
import com.wmm.eldercare.core.service.MessageAdminService;
import com.wmm.eldercare.core.vo.MessageAdminListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageAdminServiceImpl implements MessageAdminService {

    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    /**
     * 分页查询消息列表（userId 为空查全部，非空按用户筛选）
     *
     * @param userId   筛选用户 ID（可空）
     * @param pageNum  页码（从 1 开始）
     * @param pageSize 每页条数
     * @return 分页结果
     */
    @Override
    public PageResult<MessageAdminListVO> listMessages(Long userId, Integer pageNum, Integer pageSize) {
        //1.开启分页
        PageHelper.startPage(pageNum, pageSize);
        //2.查询数据
        List<MessageAdminListVO> messages = messageMapper.findAll(userId);
        //3.封装分页结果
        PageInfo<MessageAdminListVO> pageInfo = new PageInfo<>(messages);
        return new PageResult<>(
                pageInfo.getTotal(),
                pageInfo.getList(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getPages()
        );
    }

    /**
     * 发送消息（推送给指定用户）
     * @param dto 消息内容（userId/title/content/type）
     */
    @Override
    public void sendMessage(MessageSendDTO dto) {
        // sendMessage 开头
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new BusinessException(400, "消息标题不能为空");
        }
        if (dto.getContent() == null || dto.getContent().isBlank()) {
            throw new BusinessException(400, "消息内容不能为空");
        }
        //判断用户是否存在
        if (userMapper.findUserById(dto.getUserId()) == null) {
            throw new BusinessException(400, "用户不存在");
        }
        //2.创建消息实体
        Message message = new Message();
        message.setUserId(dto.getUserId());
        message.setTitle(dto.getTitle());
        message.setContent(dto.getContent());
        message.setType(dto.getType());
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());
        message.setIsRead(0);
        message.setDeleted(0);
        messageMapper.insert(message);
    }

    /**
     * 逻辑删除消息（管理端可删任意消息）
     * @param messageId 消息 ID
     */
    @Override
    public void deleteMessage(Long messageId) {
        //1.判断消息是否存在
        if (messageMapper.findById(messageId) == null) {
            throw new BusinessException(400, "消息不存在");
        }
        //2.更新消息状态为已删除
        messageMapper.deleteByIdAdmin(messageId);
    }
}
