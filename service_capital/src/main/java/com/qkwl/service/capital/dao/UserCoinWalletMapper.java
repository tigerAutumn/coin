package com.qkwl.service.capital.dao;

import org.apache.ibatis.annotations.Mapper;
import com.qkwl.common.dto.wallet.UserCoinWallet;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;



/**
 * 用户钱包数据操作接口
 * @author ZKF
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
     * 更新
     * @param record 实体对象
     * @return 成功条数
     */
    int updateByPrimaryKey(UserCoinWallet record);
    
    /**
     * 获取用户所有钱包
     * @param uid 用户ID
     * @return 实体对象列表
     */
    List<UserCoinWallet> selectByUid(@Param("uid") Integer uid);

    /**
     * 修改时查询(无锁)
     * @param uid 用户ID
     * @param coinId 币种ID
     * @return 实体对象
     */
    UserCoinWallet selectByUidAndCoin(@Param("uid") Integer uid, @Param("coinId") Integer coinId);
    
    /**
     * 修改时查询(带行级锁)
     * @param uid 用户ID
     * @param coinId 币种ID
     * @return 实体对象
     */
    UserCoinWallet selectByUidAndCoinLock(@Param("uid") Integer uid, @Param("coinId") Integer coinId);
    
    /**
    *
    * @param amount
    * @param gmtModified
    * @return
    */
    int updateTotal2FrozenCAS(@Param("amount") BigDecimal amount, @Param("gmtModified") Date gmtModified,
            @Param("id") Integer id,  @Param("version") BigInteger version);
    
    /**
     * 乐观锁更新
     * @param 
     * @return 成功条数
     */
    int updateTotalCAS(@Param("total") BigDecimal total, @Param("gmtModified") Date gmtModified,
            @Param("id") Integer id,  @Param("version") BigInteger version);
    
    /**
     * 乐观锁更新
     * @param 
     * @return 成功条数
     */
    int updateFrozenCAS(@Param("frozen") BigDecimal frozen, @Param("gmtModified") Date gmtModified,
            @Param("id") Integer id,  @Param("version") BigInteger version);

    /**
    *
    * @param amount
    * @param gmtModified
    * @return
    */
    int updateFrozen2TotalCAS(@Param("amount") BigDecimal amount, @Param("gmtModified") Date gmtModified,
            @Param("id") Integer id,  @Param("version") BigInteger version);
}
