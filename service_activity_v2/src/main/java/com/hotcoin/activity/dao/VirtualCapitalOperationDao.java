package com.hotcoin.activity.dao;

import com.github.pagehelper.Page;
import com.hotcoin.activity.model.param.VirtualCapitalOperationDto;
import com.hotcoin.activity.model.po.VirtualCapitalOperationPo;
import com.hotcoin.activity.model.resp.VirtualCapitalOperationResp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * &egrave;????????&egrave;&mu;?&eacute;?&lsquo;?&mu;???的dao层
 *
 * @author hf
 * @date 2019-06-12 03:05:41
 */
@Mapper
public interface VirtualCapitalOperationDao {

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


    List<VirtualCapitalOperationResp> queryRechargeRecordByDtoParam(VirtualCapitalOperationDto vcod);

    Long queryTotal();

    Integer deleteBatch(List<Integer> list);

    /*<AUTOGEN--END>*/

}