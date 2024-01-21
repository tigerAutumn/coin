package com.hotcoin.activity.service.impl;

import com.github.pagehelper.Page;
import com.hotcoin.activity.dao.AirdropActivityDetailV2Dao;
import com.hotcoin.activity.model.po.AirdropActivityDetailV2Po;
import com.hotcoin.activity.service.AirdropActivityDetailV2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @author hf
 * @date 2019-06-13 02:29:35
 */
@Service("airdropActivityDetailV2Service")
public class AirdropActivityDetailV2ServiceImpl implements AirdropActivityDetailV2Service {
    /*<AUTOGEN--BEGIN>*/

    @Autowired
    public AirdropActivityDetailV2Dao airdropActivityDetailV2Dao;


    @Override
    public List<AirdropActivityDetailV2Po> selectPaged(AirdropActivityDetailV2Po activityDetailV2Po) {
        return airdropActivityDetailV2Dao.selectPaged(activityDetailV2Po);
    }

    @Override
    public List<AirdropActivityDetailV2Po> selectNotSuccessData() {
        return airdropActivityDetailV2Dao.selectNotSuccessData();
    }

    @Override
    public AirdropActivityDetailV2Po selectByPrimaryKey(Integer id) {
        return airdropActivityDetailV2Dao.selectByPrimaryKey(id);
    }

    @Override
    public Integer deleteByPrimaryKey(Integer id) {
        return airdropActivityDetailV2Dao.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(value = "xaTransMgr", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Integer insert(AirdropActivityDetailV2Po airdropActivityDetailV2) {
        return airdropActivityDetailV2Dao.insert(airdropActivityDetailV2);
    }

    @Override
    @Transactional(value = "xaTransMgr", isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Integer insertSelective(AirdropActivityDetailV2Po airdropActivityDetailV2) {
        return airdropActivityDetailV2Dao.insertSelective(airdropActivityDetailV2);
    }

    @Override
    public Integer insertSelectiveIgnore(AirdropActivityDetailV2Po airdropActivityDetailV2) {
        return airdropActivityDetailV2Dao.insertSelectiveIgnore(airdropActivityDetailV2);
    }

    @Override
    public Integer updateByPrimaryKeySelective(AirdropActivityDetailV2Po airdropActivityDetailV2) {
        return airdropActivityDetailV2Dao.updateByPrimaryKeySelective(airdropActivityDetailV2);
    }

    @Override
    public Integer updateByPrimaryKey(AirdropActivityDetailV2Po airdropActivityDetailV2) {
        return airdropActivityDetailV2Dao.updateByPrimaryKey(airdropActivityDetailV2);
    }

    @Override
    public Integer batchInsert(List<AirdropActivityDetailV2Po> list) {
        return airdropActivityDetailV2Dao.batchInsert(list);
    }

    @Override
    public Integer batchUpdate(List<AirdropActivityDetailV2Po> list) {
        return airdropActivityDetailV2Dao.batchUpdate(list);
    }

    /**
     * 存在即更新
     *
     * @param airdropActivityDetailV2
     * @return
     */
    @Override
    public Integer upsert(AirdropActivityDetailV2Po airdropActivityDetailV2) {
        return airdropActivityDetailV2Dao.upsert(airdropActivityDetailV2);
    }

    /**
     * 存在即更新，可选择具体属性
     *
     * @param airdropActivityDetailV2
     * @return
     */
    @Override
    public Integer upsertSelective(AirdropActivityDetailV2Po airdropActivityDetailV2) {
        return airdropActivityDetailV2Dao.upsertSelective(airdropActivityDetailV2);
    }

    @Override
    public List<AirdropActivityDetailV2Po> query(AirdropActivityDetailV2Po airdropActivityDetailV2) {
        return airdropActivityDetailV2Dao.query(airdropActivityDetailV2);
    }

    @Override
    public Double queryTotalByTypeAndCoin(AirdropActivityDetailV2Po aadv) {
        Double total = airdropActivityDetailV2Dao.queryTotalByTypeAndCoin(aadv);
        return total == null ? 0 : total;
    }

    @Override
    public Long queryTotal() {
        return airdropActivityDetailV2Dao.queryTotal();
    }

    @Override
    public Integer deleteBatch(List<Integer> list) {
        return airdropActivityDetailV2Dao.deleteBatch(list);
    }

    /*<AUTOGEN--END>*/

}
