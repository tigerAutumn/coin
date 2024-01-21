package com.qkwl.service.coin.mapper;

import com.qkwl.common.dto.capital.FVirtualCapitalOperationDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 虚拟币充值提现记录
 * @author TT
 */
@Mapper
public interface FVirtualCapitalOperationMapper {
	
	/**
	 * 新增记录
	 * @param record
	 * @return
	 */
    int insert(FVirtualCapitalOperationDTO record);

    /**
     * 根据id查询记录
     * @param funiquenumber
     * @return
     */
    List<FVirtualCapitalOperationDTO> selectByTxid(@Param("funiquenumber") String funiquenumber);

    /**
     * 更新记录
     * @param record
     * @return
     */
    int updateByPrimaryKey(FVirtualCapitalOperationDTO record);

    /**
     * 查找未到账
     * @param fcoinid
     * @return
     */
    List<FVirtualCapitalOperationDTO> seletcGoing(@Param("fcoinid") int fcoinid, @Param("fconfirmations") int fconfirmations);

    /**
     * 查询记录总条数
     * @param map 参数map
     * @return 查询记录数
     */
    int countVirtualCapitalOperation(Map<String, Object> map);
    
    
    /**
     * 更新确认数
     * @param coinId 币种id
     * @param blockNumber 区块高度
     * @param type 类型
     * @param statusList 更改状态类型
     * @return
     */
    int updateConfirm(@Param("coinId") Integer coinId, @Param("type") Integer type,@Param("blockNumber") BigInteger blockNumber,@Param("statusList") List<Integer> statusList);
    
    
    /**
     * 根据参数查询
     * @param coinId 币种id
     * @param confirmations 确认数，大于此确认数
     * @param type 类型
     * @param statusList 状态
     * @return
     */
    List<FVirtualCapitalOperationDTO> seletcByParam(@Param("coinId") Integer coinId, @Param("confirmations") Integer confirmations, @Param("type") Integer type,@Param("statusList") List<Integer> statusList);
    
}