package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.user.CommissionRecord;

@Mapper
public interface CommissionRecordMapper {

    /**
     * 更新
     * @param record
     * @return
     */
    int update(CommissionRecord record);

    /**
     *
     *
     * @param id
     * @return
     */
    CommissionRecord select(Integer id);

    /**
     * 获取总数
     * @param params
     * @return
     */
    int getCount(Map<String,String> params);

    /**
     * 分页获取
     *
     * @param params
     * @return
     */
    List<CommissionRecord> getPageRecord(Map<String,String> params);

    /**
     * 返佣记录分页查询的总记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countCommissionRecordListByParam(Map<String, Object> map);

    /**
     * 返佣记录分页查询
     * @param map 参数map
     * @return 返佣记录列表
     */
	List<CommissionRecord> getCommissionRecordPageList(Map<String, Object> map);

}
