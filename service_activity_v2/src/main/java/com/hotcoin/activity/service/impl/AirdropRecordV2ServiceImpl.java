package com.hotcoin.activity.service.impl;

import com.github.pagehelper.Page;
import com.hotcoin.activity.dao.AirdropRecordV2Dao;
import com.hotcoin.activity.model.po.AirdropRecordV2Po;
import com.hotcoin.activity.service.AirdropRecordV2Service;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


/**
 * @author hf
 * @date 2019-06-10 09:50:42
 */
@Service("airdropRecordV2Service")
public class AirdropRecordV2ServiceImpl implements AirdropRecordV2Service {
    /*<AUTOGEN--BEGIN>*/

    @Autowired
    public AirdropRecordV2Dao airdropRecordV2Dao;


    @Override
    public Page<AirdropRecordV2Po> selectPaged(RowBounds rowBounds) {
        return airdropRecordV2Dao.selectPaged(rowBounds);
    }

    @Override
    public AirdropRecordV2Po selectByPrimaryKey(Integer id) {
        return airdropRecordV2Dao.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer deleteByPrimaryKey(Integer id) {
        return airdropRecordV2Dao.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer insert(AirdropRecordV2Po airdropRecordV2) {
        return airdropRecordV2Dao.insert(airdropRecordV2);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer insertSelective(AirdropRecordV2Po airdropRecordV2) {
        return airdropRecordV2Dao.insertSelective(airdropRecordV2);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer insertSelectiveIgnore(AirdropRecordV2Po airdropRecordV2) {
        return airdropRecordV2Dao.insertSelectiveIgnore(airdropRecordV2);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer updateByPrimaryKeySelective(AirdropRecordV2Po airdropRecordV2) {
        return airdropRecordV2Dao.updateByPrimaryKeySelective(airdropRecordV2);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer updateByPrimaryKey(AirdropRecordV2Po airdropRecordV2) {
        return airdropRecordV2Dao.updateByPrimaryKey(airdropRecordV2);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer batchInsert(List<AirdropRecordV2Po> list) {
        return airdropRecordV2Dao.batchInsert(list);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer batchUpdate(List<AirdropRecordV2Po> list) {
        return airdropRecordV2Dao.batchUpdate(list);
    }

    /**
     * 存在即更新
     *
     * @param airdropRecordV2
     * @return
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer upsert(AirdropRecordV2Po airdropRecordV2) {
        return airdropRecordV2Dao.upsert(airdropRecordV2);
    }

    /**
     * 存在即更新，可选择具体属性
     *
     * @param airdropRecordV2
     * @return
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer upsertSelective(AirdropRecordV2Po airdropRecordV2) {
        return airdropRecordV2Dao.upsertSelective(airdropRecordV2);
    }

    @Override
    public List<AirdropRecordV2Po> query(AirdropRecordV2Po airdropRecordV2) {
        return airdropRecordV2Dao.query(airdropRecordV2);
    }

    @Override
    public Long queryTotal() {
        return airdropRecordV2Dao.queryTotal();
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer deleteBatch(List<Integer> list) {
        return airdropRecordV2Dao.deleteBatch(list);
    }

    /*<AUTOGEN--END>*/

}
