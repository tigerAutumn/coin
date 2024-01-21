package com.hotcoin.activity.service.impl;

import com.github.pagehelper.PageInfo;
import com.hotcoin.activity.dao.UserCoinWalletDao;
import com.hotcoin.activity.model.po.UserCoinWalletPo;
import com.hotcoin.activity.service.UserCoinWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author hf
 * @date 2019-07-01 06:43:56
 * @since jdk 1.8
 */
@Service("userCoinWalletService")
public class UserCoinWalletServiceImpl implements UserCoinWalletService {
    /*<AUTOGEN--BEGIN>*/

    @Autowired
    public UserCoinWalletDao userCoinWalletDao;


    @Override
    public PageInfo<UserCoinWalletPo> selectPaged() {
        return userCoinWalletDao.selectPaged();
    }

    @Override
    public UserCoinWalletPo selectByPrimaryKey(Integer id) {
        return userCoinWalletDao.selectByPrimaryKey(id);
    }

    @Override
    public Integer deleteByPrimaryKey(Integer id) {
        return userCoinWalletDao.deleteByPrimaryKey(id);
    }

    @Override
    public Integer insert(UserCoinWalletPo userCoinWallet) {
        return userCoinWalletDao.insert(userCoinWallet);
    }

    @Override
    public Integer insertSelective(UserCoinWalletPo userCoinWallet) {
        return userCoinWalletDao.insertSelective(userCoinWallet);
    }

    @Override
    public Integer insertSelectiveIgnore(UserCoinWalletPo userCoinWallet) {
        return userCoinWalletDao.insertSelectiveIgnore(userCoinWallet);
    }

    @Override
    public Integer updateByPrimaryKeySelective(UserCoinWalletPo userCoinWallet) {
        return userCoinWalletDao.updateByPrimaryKeySelective(userCoinWallet);
    }

    @Override
    public Integer updateByPrimaryKey(UserCoinWalletPo userCoinWallet) {
        return userCoinWalletDao.updateByPrimaryKey(userCoinWallet);
    }

    @Override
    public Integer batchInsert(List<UserCoinWalletPo> list) {
        return userCoinWalletDao.batchInsert(list);
    }

    @Override
    public Integer batchUpdate(List<UserCoinWalletPo> list) {
        return userCoinWalletDao.batchUpdate(list);
    }

    /**
     * 存在即更新
     *
     * @param userCoinWallet
     * @return
     */
    @Override
    public Integer upsert(UserCoinWalletPo userCoinWallet) {
        return userCoinWalletDao.upsert(userCoinWallet);
    }

    /**
     * 钱包操作用到
     *
     * @param userCoinWallet
     * @return
     */
    @Override
    public Integer update(UserCoinWalletPo userCoinWallet) {
        return userCoinWalletDao.update(userCoinWallet);
    }

    /**
     * 存在即更新，可选择具体属性
     *
     * @param userCoinWallet
     * @return
     */
    @Override
    public Integer upsertSelective(UserCoinWalletPo userCoinWallet) {
        return userCoinWalletDao.upsertSelective(userCoinWallet);
    }

    @Override
    public List<UserCoinWalletPo> query(UserCoinWalletPo userCoinWallet) {
        return userCoinWalletDao.query(userCoinWallet);
    }

    @Override
    public Long queryTotal() {
        return userCoinWalletDao.queryTotal();
    }

    @Override
    public Integer deleteBatch(List<Integer> list) {
        return userCoinWalletDao.deleteBatch(list);
    }

    /*<AUTOGEN--END>*/

}
