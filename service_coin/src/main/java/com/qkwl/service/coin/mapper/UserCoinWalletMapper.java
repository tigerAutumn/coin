package com.qkwl.service.coin.mapper;

import com.qkwl.common.dto.wallet.UserCoinWallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
     * 修改时查询(带行级锁)
     * @param uid 用户ID
     * @param coinId 币种ID
     * @return 实体对象
     */
    UserCoinWallet selectByUidAndCoin(@Param("uid") Integer uid, @Param("coinId") Integer coinId);

    /**
     * 根据用户ID查询所有钱包
     * @param uid
     * @return
     */
    List<UserCoinWallet> selectUid(@Param("uid") Integer uid);

}
