package com.hotcoin.activity.service.impl;

import com.github.pagehelper.Page;
import com.hotcoin.activity.dao.VirtualCapitalOperationDao;
import com.hotcoin.activity.model.param.VirtualCapitalOperationDto;
import com.hotcoin.activity.model.po.VirtualCapitalOperationPo;
import com.hotcoin.activity.model.resp.VirtualCapitalOperationResp;
import com.hotcoin.activity.service.VirtualCapitalOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


/**
 * &egrave;????????&egrave;&mu;?&eacute;?&lsquo;?&mu;???
 *
 * @author hf
 * @date 2019-06-12 03:05:41
 */
@Service("virtualCapitalOperationService")
public class VirtualCapitalOperationServiceImpl implements VirtualCapitalOperationService {
    /*<AUTOGEN--BEGIN>*/

    @Autowired
    public VirtualCapitalOperationDao virtualCapitalOperationDao;


    @Override
    public Page<VirtualCapitalOperationPo> selectPaged() {
        return virtualCapitalOperationDao.selectPaged();
    }

    @Override
    public VirtualCapitalOperationPo selectByPrimaryKey(Integer fid) {
        return virtualCapitalOperationDao.selectByPrimaryKey(fid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteByPrimaryKey(Integer fid) {
        return virtualCapitalOperationDao.deleteByPrimaryKey(fid);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer insert(VirtualCapitalOperationPo virtualCapitalOperation) {
        return virtualCapitalOperationDao.insert(virtualCapitalOperation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer insertSelective(VirtualCapitalOperationPo virtualCapitalOperation) {
        return virtualCapitalOperationDao.insertSelective(virtualCapitalOperation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer insertSelectiveIgnore(VirtualCapitalOperationPo virtualCapitalOperation) {
        return virtualCapitalOperationDao.insertSelectiveIgnore(virtualCapitalOperation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateByPrimaryKeySelective(VirtualCapitalOperationPo virtualCapitalOperation) {
        return virtualCapitalOperationDao.updateByPrimaryKeySelective(virtualCapitalOperation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateByPrimaryKey(VirtualCapitalOperationPo virtualCapitalOperation) {
        return virtualCapitalOperationDao.updateByPrimaryKey(virtualCapitalOperation);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchInsert(List<VirtualCapitalOperationPo> list) {
        return virtualCapitalOperationDao.batchInsert(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchUpdate(List<VirtualCapitalOperationPo> list) {
        return virtualCapitalOperationDao.batchUpdate(list);
    }

    /**
     * 存在即更新
     *
     * @param virtualCapitalOperation
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer upsert(VirtualCapitalOperationPo virtualCapitalOperation) {
        return virtualCapitalOperationDao.upsert(virtualCapitalOperation);
    }

    /**
     * 存在即更新，可选择具体属性
     *
     * @param virtualCapitalOperation
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer upsertSelective(VirtualCapitalOperationPo virtualCapitalOperation) {
        return virtualCapitalOperationDao.upsertSelective(virtualCapitalOperation);
    }

    @Override
    public List<VirtualCapitalOperationPo> query(VirtualCapitalOperationPo virtualCapitalOperation) {
        return virtualCapitalOperationDao.query(virtualCapitalOperation);
    }

    @Override
    public List<VirtualCapitalOperationResp> queryRechargeRecordByDtoParam(VirtualCapitalOperationDto vcod) {
        return virtualCapitalOperationDao.queryRechargeRecordByDtoParam(vcod);
    }

    @Override
    public Long queryTotal() {
        return virtualCapitalOperationDao.queryTotal();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteBatch(List<Integer> list) {
        return virtualCapitalOperationDao.deleteBatch(list);
    }

    /*<AUTOGEN--END>*/

}
