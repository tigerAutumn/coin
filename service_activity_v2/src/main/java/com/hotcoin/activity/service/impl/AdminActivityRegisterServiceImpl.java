package com.hotcoin.activity.service.impl;

import com.github.pagehelper.Page;
import com.hotcoin.activity.dao.AdminActivityRegisterDao;
import com.hotcoin.activity.model.po.AdminActivityRegisterPo;
import com.hotcoin.activity.service.AdminActivityRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @author hf
 * @date 2019-06-10 09:50:42
 */
@Service("adminActivityRegisterService")
public class AdminActivityRegisterServiceImpl implements AdminActivityRegisterService {
    /*<AUTOGEN--BEGIN>*/

    @Autowired
    public AdminActivityRegisterDao adminActivityRegisterDao;

    @Override
    public Page<AdminActivityRegisterPo> selectPaged() {
        return adminActivityRegisterDao.selectPaged();
    }

    @Override
    public AdminActivityRegisterPo selectByPrimaryKey(Integer id) {
        return adminActivityRegisterDao.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer deleteByPrimaryKey(Integer id) {
        return adminActivityRegisterDao.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer insert(AdminActivityRegisterPo adminActivityRegister) {
        return adminActivityRegisterDao.insert(adminActivityRegister);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer insertSelective(AdminActivityRegisterPo adminActivityRegister) {
        return adminActivityRegisterDao.insertSelective(adminActivityRegister);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer insertSelectiveIgnore(AdminActivityRegisterPo adminActivityRegister) {
        return adminActivityRegisterDao.insertSelectiveIgnore(adminActivityRegister);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer updateByPrimaryKeySelective(AdminActivityRegisterPo adminActivityRegister) {
        return adminActivityRegisterDao.updateByPrimaryKeySelective(adminActivityRegister);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer updateByPrimaryKey(AdminActivityRegisterPo adminActivityRegister) {
        return adminActivityRegisterDao.updateByPrimaryKey(adminActivityRegister);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer batchInsert(List<AdminActivityRegisterPo> list) {
        return adminActivityRegisterDao.batchInsert(list);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer batchUpdate(List<AdminActivityRegisterPo> list) {
        return adminActivityRegisterDao.batchUpdate(list);
    }

    /**
     * 存在即更新
     *
     * @param adminActivityRegister
     * @return
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer upsert(AdminActivityRegisterPo adminActivityRegister) {
        return adminActivityRegisterDao.upsert(adminActivityRegister);
    }

    /**
     * 存在即更新，可选择具体属性
     *
     * @param adminActivityRegister
     * @return
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer upsertSelective(AdminActivityRegisterPo adminActivityRegister) {
        return adminActivityRegisterDao.upsertSelective(adminActivityRegister);
    }

    @Override
    public List<AdminActivityRegisterPo> query(AdminActivityRegisterPo adminActivityRegister) {
        return adminActivityRegisterDao.query(adminActivityRegister);
    }

    @Override
    public Long queryTotal() {
        return adminActivityRegisterDao.queryTotal();
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer deleteBatch(List<Integer> list) {
        return adminActivityRegisterDao.deleteBatch(list);
    }

    /*<AUTOGEN--END>*/

}
