package com.qkwl.service.admin.bc.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.qkwl.common.dto.otc.OtcUserTransfer;
@Mapper
public interface OtcUserTransferMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OtcUserTransfer record);

    int insertSelective(OtcUserTransfer record);

    OtcUserTransfer selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OtcUserTransfer record);

    int updateByPrimaryKey(OtcUserTransfer record);
    
	/**
	 * 统计otc资金互转
	 * @param uid
	 * @return  
	 **/
    List<OtcUserTransfer> sumOtcTransfer(@Param("userId") Integer userId,@Param("typeList") List<Integer> typeList);
    
    /**
	 * 统计otc资金互转
	 * @param uid
	 * @return  
	 **/
    OtcUserTransfer sumOtcTransferBalance(@Param("userId") Integer userId,@Param("type") Integer type,@Param("coinId") Integer coinId);
    
    /**
     * 分页查询数据总条数
     * @param map 参数map
     * @return 查询记录数
     */
    int countOtcTransferByParam(Map<String, Object> map);
    
    /**
     * 分页查询数据
     * @param map 参数
     * @return 查询列表
     */
    List<OtcUserTransfer> selectOtcTransferList(Map<String, Object> map);
    
    /**
     * 查询汇总转出数量
     * @param map 参数
     * @return 查询列表
     */
    List<OtcUserTransfer> selectTransferOutAmount(Map<String, Object> map);
    
    /**
     * 查询汇总转出数量
     * @param map 参数
     * @return 查询列表
     */
    List<OtcUserTransfer> selectTransferInAmount(Map<String, Object> map);
}