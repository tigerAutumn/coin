package com.hotcoin.notice.dao.base;

import com.hotcoin.notice.entity.MessageTemplateEntity;
import com.hotcoin.notice.entity.MessageTemplateEntityCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MessageTemplateBaseMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_template
     *
     * @mbg.generated Thu Aug 01 10:09:29 CST 2019
     */
    long countByExample(MessageTemplateEntityCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_template
     *
     * @mbg.generated Thu Aug 01 10:09:29 CST 2019
     */
    int deleteByExample(MessageTemplateEntityCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_template
     *
     * @mbg.generated Thu Aug 01 10:09:29 CST 2019
     */
    int insert(MessageTemplateEntity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_template
     *
     * @mbg.generated Thu Aug 01 10:09:29 CST 2019
     */
    int insertSelective(MessageTemplateEntity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_template
     *
     * @mbg.generated Thu Aug 01 10:09:29 CST 2019
     */
    List<MessageTemplateEntity> selectByExampleWithBLOBs(MessageTemplateEntityCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_template
     *
     * @mbg.generated Thu Aug 01 10:09:29 CST 2019
     */
    List<MessageTemplateEntity> selectByExample(MessageTemplateEntityCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_template
     *
     * @mbg.generated Thu Aug 01 10:09:29 CST 2019
     */
    int updateByExampleSelective(@Param("record") MessageTemplateEntity record, @Param("example") MessageTemplateEntityCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_template
     *
     * @mbg.generated Thu Aug 01 10:09:29 CST 2019
     */
    int updateByExampleWithBLOBs(@Param("record") MessageTemplateEntity record, @Param("example") MessageTemplateEntityCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table message_template
     *
     * @mbg.generated Thu Aug 01 10:09:29 CST 2019
     */
    int updateByExample(@Param("record") MessageTemplateEntity record, @Param("example") MessageTemplateEntityCriteria example);
}