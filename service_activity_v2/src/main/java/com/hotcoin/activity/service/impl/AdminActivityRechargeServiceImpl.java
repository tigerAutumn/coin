package com.hotcoin.activity.service.impl;

import com.github.pagehelper.Page;
import com.hotcoin.activity.dao.AdminActivityRechargeDao;
import com.hotcoin.activity.model.po.AdminActivityRechargePo;
import com.hotcoin.activity.service.AdminActivityRechargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @author hf
 * @date 2019-06-10 09:50:42
 */
@Service("adminActivityRechargeService")
public class AdminActivityRechargeServiceImpl implements AdminActivityRechargeService {
    /*<AUTOGEN--BEGIN>*/

    @Autowired
    public AdminActivityRechargeDao adminActivityRechargeDao;


    @Override
    public Page<AdminActivityRechargePo> selectPaged() {
        return adminActivityRechargeDao.selectPaged();
    }

    @Override
    public AdminActivityRechargePo selectByPrimaryKey(Integer id) {
        return adminActivityRechargeDao.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer deleteByPrimaryKey(Integer id) {
        return adminActivityRechargeDao.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer insert(AdminActivityRechargePo adminActivityRecharge) {
        return adminActivityRechargeDao.insert(adminActivityRecharge);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer insertSelective(AdminActivityRechargePo adminActivityRecharge) {
        return adminActivityRechargeDao.insertSelective(adminActivityRecharge);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer insertSelectiveIgnore(AdminActivityRechargePo adminActivityRecharge) {
        return adminActivityRechargeDao.insertSelectiveIgnore(adminActivityRecharge);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer updateByPrimaryKeySelective(AdminActivityRechargePo adminActivityRecharge) {
        return adminActivityRechargeDao.updateByPrimaryKeySelective(adminActivityRecharge);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer updateByPrimaryKey(AdminActivityRechargePo adminActivityRecharge) {
        return adminActivityRechargeDao.updateByPrimaryKey(adminActivityRecharge);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer batchInsert(List<AdminActivityRechargePo> list) {
        return adminActivityRechargeDao.batchInsert(list);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer batchUpdate(List<AdminActivityRechargePo> list) {
        return adminActivityRechargeDao.batchUpdate(list);
    }

    /**
     * 存在即更新
     *
     * @param adminActivityRecharge
     * @return
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer upsert(AdminActivityRechargePo adminActivityRecharge) {
        return adminActivityRechargeDao.upsert(adminActivityRecharge);
    }

    /**
     * 存在即更新，可选择具体属性
     *
     * @param adminActivityRecharge
     * @return
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer upsertSelective(AdminActivityRechargePo adminActivityRecharge) {
        return adminActivityRechargeDao.upsertSelective(adminActivityRecharge);
    }

    @Override
    public List<AdminActivityRechargePo> query(AdminActivityRechargePo adminActivityRecharge) {
        return adminActivityRechargeDao.query(adminActivityRecharge);
    }

    @Override
    public Long queryTotal() {
        return adminActivityRechargeDao.queryTotal();
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer deleteBatch(List<Integer> list) {
        return adminActivityRechargeDao.deleteBatch(list);
    }

    /*<AUTOGEN--END>*/

}
