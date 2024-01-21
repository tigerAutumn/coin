package com.qkwl.service.admin.bc.dao;

import com.qkwl.common.dto.activity_v2.po.AdminActivityTradePo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 的dao层
 *
 * @author hf
 * @date 2019-06-10 09:20:44
 */
@Mapper
public interface AdminActivityTradeMapper {

    /*<AUTOGEN--BEGIN>*/

    List<AdminActivityTradePo> selectPaged();

    AdminActivityTradePo selectByPrimaryKey(Integer id);

    Integer deleteByPrimaryKey(Integer id);

    Integer insert(AdminActivityTradePo adminActivityTrade);

    Integer insertSelective(AdminActivityTradePo adminActivityTrade);

    Integer insertSelectiveIgnore(AdminActivityTradePo adminActivityTrade);

    Integer updateByPrimaryKeySelective(AdminActivityTradePo adminActivityTrade);

    Integer updateByPrimaryKey(AdminActivityTradePo adminActivityTrade);

    Integer batchInsert(List<AdminActivityTradePo> list);

    Integer batchUpdate(List<AdminActivityTradePo> list);

    /**
     * 存在即更新
     *
     * @param adminActivityTrade
     * @return
     */
    Integer upsert(AdminActivityTradePo adminActivityTrade);

    /**
     * 存在即更新，可选择具体属性
     *
     * @param adminActivityTrade
     * @return
     */
    Integer upsertSelective(AdminActivityTradePo adminActivityTrade);

    List<AdminActivityTradePo> query(AdminActivityTradePo adminActivityTrade);

    Long queryTotal();

    Integer deleteBatch(List<Integer> list);

    /*<AUTOGEN--END>*/

}