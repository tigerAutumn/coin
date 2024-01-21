package com.hotcoin.webchat.mapper.chat;
import java.util.Date;
import java.util.List;

import com.hotcoin.webchat.model.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;


/**
 * OTC聊天数据访问接口
 * @author XXX
 *
 */
@Repository("chatMessageMapper")
@Mapper
public interface ChatMessageMapper {

    /**
     * 主键查询OTC聊天
     * @param msgId
     * @return OTC聊天信息
     */
    ChatMessage get(@Param("msgId") Long msgId);
    
    /**
     * 查询OTC聊天 列表
     * @param chatMessage 
     * @return OTC聊天信息列表
     */
    List<ChatMessage> selectList(ChatMessage chatMessage);
        
    /**
     * 保存OTC聊天
     * @param chatMessage OTC聊天信息
     */
    void insert(ChatMessage chatMessage);
    
    /**
     * 更新OTC聊天
     * @param chatMessage OTC聊天信息
     */
    void update(ChatMessage chatMessage);

    /**
     * 更新otc聊天的状态
     * @param orderId
     * @param recv
     * @param createTime
     */
    void updateSendState(@Param("orderId") String orderId,@Param("recv") Long recv,@Param("createTime")Date createTime);

    int selectUnReadCount(@Param("orderId") String orderId,@Param("recv") Long recv);

}
