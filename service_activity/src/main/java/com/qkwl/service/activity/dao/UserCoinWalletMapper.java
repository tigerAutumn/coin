package com.qkwl.service.activity.dao;

import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.dto.wallet.UserCoinWalletSnapshot;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * 用户钱包数据操作接口
 * @author ZKF
 */
@Mapper
public interface UserCoinWalletMapper {

    /**
     * 更新
     * @param record 实体对象
     * @return 成功条数
     */
    int update(UserCoinWallet record);

    /**
     * 修改时查询(无锁)
     * @param uid 用户ID
     * @param coinId 币种ID
     * @return 实体对象
     */
    UserCoinWallet select(@Param("uid") Integer uid, @Param("coinId") Integer coinId);
    
    /**
     * 修改时查询(带行级锁)
     * @param uid 用户ID
     * @param coinId 币种ID
     * @return 实体对象
     */
    UserCoinWallet selectLock(@Param("uid") Integer uid, @Param("coinId") Integer coinId);

    /**
     * 总量
     * @param fcoinid
     * @return
     */
    UserCoinWallet selectSum(@Param("coinId") Integer fcoinid);
    
    /**
     * 快照钱包
     * @param airdrop
     * @return
     */
    void snapshotWallet(@Param("coinId") Integer coinId,@Param("limit")BigDecimal limit);
    
    
    List<UserCoinWalletSnapshot> selectSnapshotByCoinId(@Param("coinId") Integer coinId,
    		@Param("start")String start,@Param("end")String end);
    
    BigDecimal selectTotalAmount(@Param("coinId") Integer coinId,
    		@Param("start")String start,@Param("end")String end);
    
    /**
    *
    * @param amount
    * @param gmtModified
    * @return
    */
    int updateFrozen2TotalCAS(@Param("amount") BigDecimal amount, @Param("gmtModified") Date gmtModified,
            @Param("id") Integer id,  @Param("version") BigInteger version);
    
    /**
     * 修改时查询(无锁)
     * @param uid 用户ID
     * @param coinId 币种ID
     * @return 实体对象
     */
    UserCoinWallet selectByUidAndCoin(@Param("uid") Integer uid, @Param("coinId") Integer coinId);
    
    /**
	 * 插入数据
	 * @param record 实体对象
	 * @return 成功条数
	 */
    int insert(UserCoinWallet record);
}
