package com.hotcoin.activity.service;

import com.github.pagehelper.PageInfo;
import com.hotcoin.activity.model.po.UserCoinWalletPo;

import java.util.List;

/**
 * service层
 *
 * @author hf
 * @date 2019-07-01 06:43:56
 * @since jdk 1.8
 */
public interface UserCoinWalletService {

    /*<AUTOGEN--BEGIN>*/

    PageInfo<UserCoinWalletPo> selectPaged();

    UserCoinWalletPo selectByPrimaryKey(Integer id);

    Integer deleteByPrimaryKey(Integer id);

    Integer insert(UserCoinWalletPo userCoinWallet);

    Integer insertSelective(UserCoinWalletPo userCoinWallet);

    Integer insertSelectiveIgnore(UserCoinWalletPo userCoinWallet);

    Integer updateByPrimaryKeySelective(UserCoinWalletPo userCoinWallet);

    Integer updateByPrimaryKey(UserCoinWalletPo userCoinWallet);

    Integer batchInsert(List<UserCoinWalletPo> list);

    Integer batchUpdate(List<UserCoinWalletPo> list);

    /**
     * 存在即更新
     *
     * @param userCoinWallet
     * @return
     */
    Integer upsert(UserCoinWalletPo userCoinWallet);

    /**
     * 只在阿里云事物更新用到
     *
     * @param userCoinWallet
     * @return
     */
    Integer update(UserCoinWalletPo userCoinWallet);

    /**
     * 存在即更新，可选择具体属性
     *
     * @param userCoinWallet
     * @return
     */
    Integer upsertSelective(UserCoinWalletPo userCoinWallet);

    List<UserCoinWalletPo> query(UserCoinWalletPo userCoinWallet);

    Long queryTotal();

    Integer deleteBatch(List<Integer> list);

    /*<AUTOGEN--END>*/

}
