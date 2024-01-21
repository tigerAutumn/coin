package com.hotcoin.activity.service.impl;
import com.github.pagehelper.Page;
import com.hotcoin.activity.dao.AdminActivityItemsDao;
import com.hotcoin.activity.model.po.AdminActivityItemsPo;
import com.hotcoin.activity.service.AdminActivityItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


/**
 * @author hf
 * @date 2019-06-10 09:50:42
 */
@Service("adminActivityItemsService")
public class AdminActivityItemsServiceImpl implements AdminActivityItemsService {
    /*<AUTOGEN--BEGIN>*/

    @Autowired
    public AdminActivityItemsDao adminActivityItemsDao;


    @Override
    public Page<AdminActivityItemsPo> selectPaged() {
        return adminActivityItemsDao.selectPaged();
    }

    @Override
    public AdminActivityItemsPo selectByPrimaryKey(Integer id) {
        return adminActivityItemsDao.selectByPrimaryKey(id);
    }

    @Override
    public Integer deleteByPrimaryKey(Integer id) {
        return adminActivityItemsDao.deleteByPrimaryKey(id);
    }

    @Override
    public Integer insert(AdminActivityItemsPo adminActivityItems) {
        return adminActivityItemsDao.insert(adminActivityItems);
    }

    @Override
    public Integer insertSelective(AdminActivityItemsPo adminActivityItems) {
        return adminActivityItemsDao.insertSelective(adminActivityItems);
    }

    @Override
    public Integer insertSelectiveIgnore(AdminActivityItemsPo adminActivityItems) {
        return adminActivityItemsDao.insertSelectiveIgnore(adminActivityItems);
    }

    @Override
    public Integer updateByPrimaryKeySelective(AdminActivityItemsPo adminActivityItems) {
        return adminActivityItemsDao.updateByPrimaryKeySelective(adminActivityItems);
    }

    @Override
    public Integer updateByPrimaryKey(AdminActivityItemsPo adminActivityItems) {
        return adminActivityItemsDao.updateByPrimaryKey(adminActivityItems);
    }

    @Override
    public Integer batchInsert(List<AdminActivityItemsPo> list) {
        return adminActivityItemsDao.batchInsert(list);
    }

    @Override
    public Integer batchUpdate(List<AdminActivityItemsPo> list) {
        return adminActivityItemsDao.batchUpdate(list);
    }

    /**
     * 存在即更新
     *
     * @param adminActivityItems
     * @return
     */
    @Override
    public Integer upsert(AdminActivityItemsPo adminActivityItems) {
        return adminActivityItemsDao.upsert(adminActivityItems);
    }

    /**
     * 存在即更新，可选择具体属性
     *
     * @param adminActivityItems
     * @return
     */
    @Override
    public Integer upsertSelective(AdminActivityItemsPo adminActivityItems) {
        return adminActivityItemsDao.upsertSelective(adminActivityItems);
    }

    @Override
    public List<AdminActivityItemsPo> query(AdminActivityItemsPo adminActivityItems) {
        return adminActivityItemsDao.query(adminActivityItems);
    }
    @Override
    public Long queryTotal() {
        return adminActivityItemsDao.queryTotal();
    }

    @Override
    public Integer deleteBatch(List<Integer> list) {
        return adminActivityItemsDao.deleteBatch(list);
    }

    /*<AUTOGEN--END>*/

}
