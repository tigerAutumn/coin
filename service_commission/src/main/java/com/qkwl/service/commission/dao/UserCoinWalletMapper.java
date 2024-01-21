package com.qkwl.service.commission.dao;

import com.qkwl.common.dto.wallet.UserCoinWallet;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户钱包数据操作接口
 * @author Jany
 */
@Mapper
public interface UserCoinWalletMapper {

	/**
	 * 插入数据
	 * @param record 实体对象
	 * @return 成功条数
	 */
	int insert(UserCoinWallet record);
	
	 /**
     * 修改时查询(无锁)
     * @param uid 用户ID
     * @param coinId 币种ID
     * @return 实体对象
     */
    UserCoinWallet select(@Param("uid") Integer uid, @Param("coinId") Integer coinId);
	
    /**
     * 更新
     * @param record 实体对象
     * @return 成功条数
     */
    int update(UserCoinWallet record);

    /**
     * 乐观锁更新
     * @param record 实体对象
     * @return 成功条数
     */
    int updateCAS(UserCoinWallet record);

    /**
     * 乐观锁更新
     * @param 
     * @return 成功条数
     */
    int updateWalletCAS(@Param("total") BigDecimal total, @Param("gmtModified") Date gmtModified,
            @Param("id") Integer id,  @Param("version") BigInteger version);

    /**
    *
    * @param borrow
    * @param gmtModified
    * @return
    */
    int updateBorrowCAS(@Param("borrow") BigDecimal borrow, @Param("gmtModified") Date gmtModified,
            @Param("id") Integer id,  @Param("version") BigInteger version);
    
    /**
    *
    * @param borrow
    * @param gmtModified
    * @return
    */
    int updateUnBorrowCAS(@Param("borrow") BigDecimal borrow, @Param("gmtModified") Date gmtModified,
            @Param("id") Integer id,  @Param("version") BigInteger version);
    
    
    
   
    

}
