package com.qkwl.initMarket.mapper;

import com.qkwl.common.dto.wallet.UserCoinWallet;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

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
     *
     * @param frozen
     * @param gmtModified
     * @return
     */
    int updateFrozenCAS(@Param("frozen") BigDecimal frozen,@Param("gmtModified") Date gmtModified,
                        @Param("id") Integer id,@Param("uid") Integer uid,@Param("version") BigInteger version);

}
