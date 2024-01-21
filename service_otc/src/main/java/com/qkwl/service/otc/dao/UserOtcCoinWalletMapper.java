package com.qkwl.service.otc.dao;

import com.qkwl.common.dto.wallet.UserOtcCoinWallet;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户钱包数据操作接口
 * @author Jany
 */
@Mapper
public interface UserOtcCoinWalletMapper {

	/**
	 * 插入数据
	 * @param record 实体对象
	 * @return 成功条数
	 */
	int insert(UserOtcCoinWallet record);
	
    /**
     * 更新
     * @param record 实体对象
     * @return 成功条数
     */
    int update(UserOtcCoinWallet record);
    
    
    /**
     *乐观锁更新
     * @param record 实体对象
     * @return 成功条数
     */
    int updateCAS(UserOtcCoinWallet record);
    
   /**
    * 乐观锁更新
    * @param 
    * @return 成功条数
    */
   int updateWalletCAS(@Param("total") BigDecimal total, @Param("frozen") BigDecimal frozen, @Param("gmtModified") Date gmtModified,
           @Param("id") Integer id,  @Param("version") BigInteger version);
   
   /**
   *
   * @param frozen
   * @param gmtModified
   * @return
   */
   int updateFrozenCAS(@Param("frozen") BigDecimal frozen,@Param("gmtModified") Date gmtModified,
           @Param("id") Integer id,  @Param("version") BigInteger version);
   
   
   
   /**
   *
   * @param frozen
   * @param gmtModified
   * @return
   */
  int updateUnFrozenCAS(@Param("frozen") BigDecimal frozen,@Param("gmtModified") Date gmtModified,
                      @Param("id") Integer id,  @Param("version") BigInteger version);
   
   
    /**
     * 修改时查询(无锁)
     * @param uid 用户ID
     * @param coinId 币种ID
     * @return 实体对象
     */
    UserOtcCoinWallet select(@Param("uid") Integer uid, @Param("coinId") Integer coinId);
    
    /**
     * 修改时查询(带行级锁)
     * @param uid 用户ID
     * @param coinId 币种ID
     * @return 实体对象
     */
    UserOtcCoinWallet selectLock(@Param("uid") Integer uid, @Param("coinId") Integer coinId);
    
	 /**
     * 修改时查询(无锁)
     * @param uid 用户ID
     * @param coinId 币种ID
     * @return 实体对象
     */
    List<UserOtcCoinWallet> selectByUid(@Param("uid") Integer uid);
    
    /**
     * 修改时查询(无锁)
     * @param uid 用户ID
     * @param coinId 币种ID
     * @return 实体对象
     */
    List<UserOtcCoinWallet> selectByUidAndCoinids(@Param("uid") Integer uid, @Param("coinids") List<Integer> ids);

}
