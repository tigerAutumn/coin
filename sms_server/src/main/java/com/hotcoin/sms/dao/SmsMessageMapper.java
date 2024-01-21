package com.hotcoin.sms.dao;
import java.util.List;

import com.hotcoin.sms.model.SmsMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 短信信息数据访问接口
 * @author XXX
 *
 */
@Repository("smsMessageDao")
@Mapper
public interface SmsMessageMapper {

    /**
     * 主键查询短信信息
     * @param sendId
     * @param sendchannel
     * @return 短信信息信息
     */
    SmsMessage get(@Param("sendId") String sendId,@Param("sendchannel")String sendchannel);
    
    /**
     * 查询短信信息 列表
     * @param smsMessage 
     * @return 短信信息信息列表
     */
    List<SmsMessage> selectList(SmsMessage smsMessage);
        
    /**
     * 保存短信信息
     * @param smsMessage 短信信息信息
     */
    int save(SmsMessage smsMessage);
    
    /**
     * 更新短信信息
     * @param smsMessage 短信信息信息
     */
    void update(SmsMessage smsMessage);
    
}
