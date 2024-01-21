package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.qkwl.common.dto.otc.OtcPayment;

@Mapper
public interface OtcPaymentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OtcPayment record);

    int insertSelective(OtcPayment record);

    OtcPayment selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OtcPayment record);

    int updateByPrimaryKey(OtcPayment record);
    
    List<OtcPayment> selectAll();
    
    /**
     * otc法币分页查询的总记录数
     * @param map 参数map
     * @return 查询记录数
     */
    int countOtcPaymentListByParam(Map<String, Object> map);
    
    /**
     * otc法币分页查询
     * @param map 参数map
     * @return 用户列表
     */
	List<OtcPayment> getOtcPaymentPageList(Map<String, Object> map);
	
}