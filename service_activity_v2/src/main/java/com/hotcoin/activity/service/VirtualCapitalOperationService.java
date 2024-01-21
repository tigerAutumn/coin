package com.hotcoin.activity.service;

import com.github.pagehelper.Page;
import com.hotcoin.activity.model.param.VirtualCapitalOperationDto;
import com.hotcoin.activity.model.po.VirtualCapitalOperationPo;
import com.hotcoin.activity.model.resp.VirtualCapitalOperationResp;

import java.util.List;

/**
 * service层
 *
 * @author hf
 * @date 2019-06-12 03:05:41
 */
public interface VirtualCapitalOperationService {

    /*<AUTOGEN--BEGIN>*/

    Page<VirtualCapitalOperationPo> selectPaged();

    VirtualCapitalOperationPo selectByPrimaryKey(Integer fid);

    Integer deleteByPrimaryKey(Integer fid);

    Integer insert(VirtualCapitalOperationPo virtualCapitalOperation);

    Integer insertSelective(VirtualCapitalOperationPo virtualCapitalOperation);

    Integer insertSelectiveIgnore(VirtualCapitalOperationPo virtualCapitalOperation);

    Integer updateByPrimaryKeySelective(VirtualCapitalOperationPo virtualCapitalOperation);

    Integer updateByPrimaryKey(VirtualCapitalOperationPo virtualCapitalOperation);

    Integer batchInsert(List<VirtualCapitalOperationPo> list);

    Integer batchUpdate(List<VirtualCapitalOperationPo> list);

    /**
     * 存在即更新
     *
     * @param virtualCapitalOperation
     * @return
     */
    Integer upsert(VirtualCapitalOperationPo virtualCapitalOperation);

    /**
     * 存在即更新，可选择具体属性
     *
     * @param virtualCapitalOperation
     * @return
     */
    Integer upsertSelective(VirtualCapitalOperationPo virtualCapitalOperation);

    List<VirtualCapitalOperationPo> query(VirtualCapitalOperationPo virtualCapitalOperation);

    /**
     * 查询某段时间内的充值记录
     *
     * @param startTime
     * @param endTime
     * @return
     */
    /**
     * 通过参数查询
     * @param vcod
     * @return
     */
    List<VirtualCapitalOperationResp> queryRechargeRecordByDtoParam(VirtualCapitalOperationDto vcod);

    Long queryTotal();

    Integer deleteBatch(List<Integer> list);

    /*<AUTOGEN--END>*/

}
