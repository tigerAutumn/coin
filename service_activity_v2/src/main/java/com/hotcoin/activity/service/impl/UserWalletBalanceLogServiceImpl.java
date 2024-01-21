package com.hotcoin.activity.service.impl;

import com.github.pagehelper.PageInfo;
import com.hotcoin.activity.dao.UserWalletBalanceLogDao;
import com.hotcoin.activity.model.po.UserWalletBalanceLogPo;
import com.hotcoin.activity.service.UserWalletBalanceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author hf
 * @date 2019-07-01 06:43:58
 * @since jdk 1.8
 */
@Service("userWalletBalanceLogService")
public class UserWalletBalanceLogServiceImpl implements UserWalletBalanceLogService {
    /*<AUTOGEN--BEGIN>*/

    @Autowired
    public UserWalletBalanceLogDao userWalletBalanceLogDao;


    @Override
    public PageInfo<UserWalletBalanceLogPo> selectPaged() {
        return userWalletBalanceLogDao.selectPaged();
    }

    @Override
    public UserWalletBalanceLogPo selectByPrimaryKey(Integer id) {
        return userWalletBalanceLogDao.selectByPrimaryKey(id);
    }

    @Override
    public Integer deleteByPrimaryKey(Integer id) {
        return userWalletBalanceLogDao.deleteByPrimaryKey(id);
    }

    @Override
    public Integer insert(UserWalletBalanceLogPo userWalletBalanceLog) {
        return userWalletBalanceLogDao.insert(userWalletBalanceLog);
    }

    @Override
    public Integer insertSelective(UserWalletBalanceLogPo userWalletBalanceLog) {
        return userWalletBalanceLogDao.insertSelective(userWalletBalanceLog);
    }

    @Override
    public Integer insertSelectiveIgnore(UserWalletBalanceLogPo userWalletBalanceLog) {
        return userWalletBalanceLogDao.insertSelectiveIgnore(userWalletBalanceLog);
    }

    @Override
    public Integer updateByPrimaryKeySelective(UserWalletBalanceLogPo userWalletBalanceLog) {
        return userWalletBalanceLogDao.updateByPrimaryKeySelective(userWalletBalanceLog);
    }

    @Override
    public Integer updateByPrimaryKey(UserWalletBalanceLogPo userWalletBalanceLog) {
        return userWalletBalanceLogDao.updateByPrimaryKey(userWalletBalanceLog);
    }

    @Override
    public Integer batchInsert(List<UserWalletBalanceLogPo> list) {
        return userWalletBalanceLogDao.batchInsert(list);
    }

    @Override
    public Integer batchUpdate(List<UserWalletBalanceLogPo> list) {
        return userWalletBalanceLogDao.batchUpdate(list);
    }

    /**
     * 存在即更新
     *
     * @param userWalletBalanceLog
     * @return
     */
    @Override
    public Integer upsert(UserWalletBalanceLogPo userWalletBalanceLog) {
        return userWalletBalanceLogDao.upsert(userWalletBalanceLog);
    }

    /**
     * 存在即更新，可选择具体属性
     *
     * @param userWalletBalanceLog
     * @return
     */
    @Override
    public Integer upsertSelective(UserWalletBalanceLogPo userWalletBalanceLog) {
        return userWalletBalanceLogDao.upsertSelective(userWalletBalanceLog);
    }

    @Override
    public List<UserWalletBalanceLogPo> query(UserWalletBalanceLogPo userWalletBalanceLog) {
        return userWalletBalanceLogDao.query(userWalletBalanceLog);
    }

    @Override
    public Long queryTotal() {
        return userWalletBalanceLogDao.queryTotal();
    }

    @Override
    public Integer deleteBatch(List<Integer> list) {
        return userWalletBalanceLogDao.deleteBatch(list);
    }

    /*<AUTOGEN--END>*/

}
