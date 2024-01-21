package com.hotcoin.webchat.service;

import com.github.pagehelper.PageHelper;
import com.hotcoin.webchat.mapper.chat.ChatMessageMapper;
import com.hotcoin.webchat.mapper.user.FUserMapper;
import com.hotcoin.webchat.model.ChatMessage;
import com.hotcoin.webchat.model.FUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ChatMessageService {

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageService.class);

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private FUserMapper fUserMapper;


    @Transactional(rollbackFor={Exception.class})
    public  List<ChatMessage> selectList(ChatMessage qChatMessage, int pageNum, int pageSize) {
        ChatMessage queryChatMessage = new ChatMessage();
        queryChatMessage.setOrderId(qChatMessage.getOrderId());
        PageHelper.startPage(pageNum, pageSize);
        List<ChatMessage> chatMessageList =  chatMessageMapper.selectList(queryChatMessage);
        Date date = new Date();
        chatMessageMapper.updateSendState(qChatMessage.getOrderId(),Long.valueOf(qChatMessage.getReceiver()),date);
        logger.info("selectList orderId:{},receiver:{},date:{}",qChatMessage.getOrderId(),qChatMessage.getReceiver(),date);
         return chatMessageList;
    }

    public  int selectUnReadCount(ChatMessage qChatMessage) {
        logger.info("selectUnReadCount orderId:{},receiver:{}",qChatMessage.getOrderId(),qChatMessage.getReceiver());
        return chatMessageMapper.selectUnReadCount(qChatMessage.getOrderId(),Long.valueOf(qChatMessage.getReceiver()));
    }

    public void insert(ChatMessage chatMessage){
        chatMessageMapper.insert(chatMessage);
    }

    /**
     * 查询用户信息
     * @param fid
     * @return
     */
    public FUser selectByPrimaryKey(Long fid) {
        try {
            return fUserMapper.selectByPrimaryKey(fid);
        }catch (Exception ex){
            logger.info("fid :{} is not exist. ex:{} ",fid,ex);
            return null;
        }
    }
}
