package com.hotcoin.activity.service.impl;

import com.github.pagehelper.Page;
import com.hotcoin.activity.dao.AdminActivityTradeDao;
import com.hotcoin.activity.model.po.AdminActivityTradePo;
import com.hotcoin.activity.service.AdminActivityTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @author hf
 * @date 2019-06-10 09:50:42
 */
@Service("adminActivityTradeService")
public class AdminActivityTradeServiceImpl implements AdminActivityTradeService {
    /*<AUTOGEN--BEGIN>*/

    @Autowired
    public AdminActivityTradeDao adminActivityTradeDao;


    @Override
    public Page<AdminActivityTradePo> selectPaged() {
        return adminActivityTradeDao.selectPaged();
    }

    @Override
    public AdminActivityTradePo selectByPrimaryKey(Integer id) {
        return adminActivityTradeDao.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer deleteByPrimaryKey(Integer id) {
        return adminActivityTradeDao.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer insert(AdminActivityTradePo adminActivityTrade) {
        return adminActivityTradeDao.insert(adminActivityTrade);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer insertSelective(AdminActivityTradePo adminActivityTrade) {
        return adminActivityTradeDao.insertSelective(adminActivityTrade);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer insertSelectiveIgnore(AdminActivityTradePo adminActivityTrade) {
        return adminActivityTradeDao.insertSelectiveIgnore(adminActivityTrade);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer updateByPrimaryKeySelective(AdminActivityTradePo adminActivityTrade) {
        return adminActivityTradeDao.updateByPrimaryKeySelective(adminActivityTrade);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer updateByPrimaryKey(AdminActivityTradePo adminActivityTrade) {
        return adminActivityTradeDao.updateByPrimaryKey(adminActivityTrade);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer batchInsert(List<AdminActivityTradePo> list) {
        return adminActivityTradeDao.batchInsert(list);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer batchUpdate(List<AdminActivityTradePo> list) {
        return adminActivityTradeDao.batchUpdate(list);
    }

    /**
     * 存在即更新
     *
     * @param adminActivityTrade
     * @return
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer upsert(AdminActivityTradePo adminActivityTrade) {
        return adminActivityTradeDao.upsert(adminActivityTrade);
    }

    /**
     * 存在即更新，可选择具体属性
     *
     * @param adminActivityTrade
     * @return
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer upsertSelective(AdminActivityTradePo adminActivityTrade) {
        return adminActivityTradeDao.upsertSelective(adminActivityTrade);
    }

    @Override
    public List<AdminActivityTradePo> query(AdminActivityTradePo adminActivityTrade) {
        return adminActivityTradeDao.query(adminActivityTrade);
    }

    @Override
    public Long queryTotal() {
        return adminActivityTradeDao.queryTotal();
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer deleteBatch(List<Integer> list) {
        return adminActivityTradeDao.deleteBatch(list);
    }

    /*<AUTOGEN--END>*/

}
