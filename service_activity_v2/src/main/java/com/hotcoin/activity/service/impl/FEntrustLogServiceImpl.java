package com.hotcoin.activity.service.impl;

import com.github.pagehelper.Page;
import com.hotcoin.activity.dao.FEntrustLogDao;
import com.hotcoin.activity.model.param.FEntrustLogDto;
import com.hotcoin.activity.model.po.FEntrustLogPo;
import com.hotcoin.activity.model.resp.FEntrustLogResp;
import com.hotcoin.activity.service.FEntrustLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


/**
 * ?ˉ???D&mu;??????&eacute;????????????
 *
 * @author hf
 * @date 2019-06-12 03:01:58
 */
@Service("fEntrustLogService")
public class FEntrustLogServiceImpl implements FEntrustLogService {
    /*<AUTOGEN--BEGIN>*/

    @Autowired
    public FEntrustLogDao fEntrustLogDao;


    @Override
    public Page<FEntrustLogPo> selectPaged() {
        return fEntrustLogDao.selectPaged();
    }

    @Override
    public FEntrustLogPo selectByPrimaryKey(Integer fid) {
        return fEntrustLogDao.selectByPrimaryKey(fid);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer deleteByPrimaryKey(Integer fid) {
        return fEntrustLogDao.deleteByPrimaryKey(fid);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer insert(FEntrustLogPo fEntrustLog) {
        return fEntrustLogDao.insert(fEntrustLog);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer insertSelective(FEntrustLogPo fEntrustLog) {
        return fEntrustLogDao.insertSelective(fEntrustLog);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer insertSelectiveIgnore(FEntrustLogPo fEntrustLog) {
        return fEntrustLogDao.insertSelectiveIgnore(fEntrustLog);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer updateByPrimaryKeySelective(FEntrustLogPo fEntrustLog) {
        return fEntrustLogDao.updateByPrimaryKeySelective(fEntrustLog);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer updateByPrimaryKey(FEntrustLogPo fEntrustLog) {
        return fEntrustLogDao.updateByPrimaryKey(fEntrustLog);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer batchInsert(List<FEntrustLogPo> list) {
        return fEntrustLogDao.batchInsert(list);
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer batchUpdate(List<FEntrustLogPo> list) {
        return fEntrustLogDao.batchUpdate(list);
    }

    /**
     * 存在即更新
     *
     * @param fEntrustLog
     * @return
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer upsert(FEntrustLogPo fEntrustLog) {
        return fEntrustLogDao.upsert(fEntrustLog);
    }

    /**
     * 存在即更新，可选择具体属性
     *
     * @param fEntrustLog
     * @return
     */
    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer upsertSelective(FEntrustLogPo fEntrustLog) {
        return fEntrustLogDao.upsertSelective(fEntrustLog);
    }

    @Override
    public List<FEntrustLogPo> query(FEntrustLogPo fEntrustLog) {
        return fEntrustLogDao.query(fEntrustLog);
    }

    @Override
    public List<FEntrustLogResp> queryTradeByParam(FEntrustLogDto fEntrustLogDto) {
        return fEntrustLogDao.queryTradeByParam(fEntrustLogDto);
    }

    @Override
    public Long queryTotal() {
        return fEntrustLogDao.queryTotal();
    }

    @Override
    @Transactional(rollbackFor=Exception.class)
    public Integer deleteBatch(List<Integer> list) {
        return fEntrustLogDao.deleteBatch(list);
    }

    /*<AUTOGEN--END>*/

}
