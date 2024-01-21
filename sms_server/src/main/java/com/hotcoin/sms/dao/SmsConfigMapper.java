package com.hotcoin.sms.dao;
import java.util.List;

import com.hotcoin.sms.model.SmsConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 短信信息数据访问接口
 * @author XXX
 *
 */
@Repository("smsConfigMapper")
@Mapper
public interface SmsConfigMapper {

    /**
     * 主键查询短信信息
     * @return 短信信息信息
     */
    SmsConfig get(@Param("smsclazz") String smsclazz);
    
    /**
     * 查询短信信息 列表
     * @param smsConfig 
     * @return 短信信息信息列表
     */
    List<SmsConfig> selectList(SmsConfig smsConfig);
        
    /**
     * 保存短信信息
     * @param smsConfig 短信信息信息
     */
    void save(SmsConfig smsConfig);
    
    /**
     * 更新短信信息
     * @param smsConfig 短信信息信息
     */
    void update(SmsConfig smsConfig);
    
}
