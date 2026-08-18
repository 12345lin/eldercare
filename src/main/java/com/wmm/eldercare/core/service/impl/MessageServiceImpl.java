package com.wmm.eldercare.core.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wmm.eldercare.core.common.BusinessException;
import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.mapper.MessageMapper;
import com.wmm.eldercare.core.pojo.Message;
import com.wmm.eldercare.core.service.MessageService;
import com.wmm.eldercare.core.vo.MessageDetailVO;
import com.wmm.eldercare.core.vo.MessageListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;
    @Override
    public PageResult<MessageListVO> listMessages(Long userId, Integer pageNum, Integer pageSize) {
        //1.开启分页查询
        PageHelper.startPage(pageNum, pageSize);
        //2.查询消息列表
        List<Message> messages = messageMapper.findByUserId(userId);

        PageInfo<Message> pageInfo = new PageInfo<>(messages);

        //3.转换为VO列表
        List<MessageListVO> voList = messages.stream().map(msg -> {
                    return new MessageListVO(
                    msg.getId(),
                    msg.getTitle(),
                    msg.getType(),
                    msg.getIsRead(),
                    msg.getCreateTime().toString()
            );
        }).filter(v -> v != null).collect(Collectors.toList());


        //4.返回分页结果
        return new PageResult<>(
                pageInfo.getTotal(),
                voList,
                pageNum,
                pageSize,
                pageInfo.getPages()
        );
    }

    @Override
    public MessageDetailVO getDetail(Long userId, Long messageId) {
        Message message = messageMapper.findByIdAndUserId(messageId,userId);
        if (message == null) {
            throw new BusinessException(404, "消息不存在");
        }
       //2.标记已读状态
        messageMapper.markRead(messageId, userId);
        //3.返回消息详情（显示已读后的状态）
        MessageDetailVO vo = new MessageDetailVO(
                message.getId(),
                message.getTitle(),
                message.getContent(),
                message.getType(),
                1,
                message.getCreateTime().toString()
        );
        return vo;
    }

    @Override
    public void markRead(Long userId, Long messageId) {
        Message message = messageMapper.findByIdAndUserId(messageId,userId);
        if (message == null) {
            throw new BusinessException(404, "消息不存在");
        }
        // 2. 校验消息状态是否未读
        if (message.getIsRead() == 1) {
            throw new BusinessException(400, "消息已读，无需重复操作");
        }
        // 3. 更新消息状态
        message.setIsRead(1);
        int rows = messageMapper.markRead(messageId,userId);
        if (rows == 0) {
            throw new BusinessException(500, "标记已读失败，请稍后重试");
        }
    }

    @Override
    public void markAllRead(Long userId) {
        messageMapper.markAllRead(userId);
    }

    @Override
    public void deleteMessage(Long userId, Long messageId) {
        Message message = messageMapper.findByIdAndUserId(messageId,userId);
        if (message == null) {
            throw new BusinessException(404, "消息不存在");
        }
        if(message.getIsRead() == 0) {
            throw new BusinessException(400, "未读消息，删除失败");
        }
        int rows = messageMapper.deleteById(messageId,userId);
        if (rows == 0) {
            throw new BusinessException(500, "删除失败，请稍后重试");
        }
    }

    @Override
    public Integer countUnread(Long userId) {
        return messageMapper.countUnread(userId);
    }
}
