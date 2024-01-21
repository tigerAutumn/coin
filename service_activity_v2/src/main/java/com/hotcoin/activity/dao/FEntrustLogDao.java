package com.hotcoin.activity.dao;

import com.github.pagehelper.Page;
import com.hotcoin.activity.model.param.FEntrustLogDto;
import com.hotcoin.activity.model.po.FEntrustLogPo;
import com.hotcoin.activity.model.resp.FEntrustLogResp;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * dao层
 *
 * @author hf
 * @date 2019-06-12 03:01:58
 */
@Mapper
public interface FEntrustLogDao {

    /*<AUTOGEN--BEGIN>*/

    Page<FEntrustLogPo> selectPaged();

    FEntrustLogPo selectByPrimaryKey(Integer fid);

    Integer deleteByPrimaryKey(Integer fid);

    Integer insert(FEntrustLogPo fEntrustLog);

    Integer insertSelective(FEntrustLogPo fEntrustLog);

    Integer insertSelectiveIgnore(FEntrustLogPo fEntrustLog);

    Integer updateByPrimaryKeySelective(FEntrustLogPo fEntrustLog);

    Integer updateByPrimaryKey(FEntrustLogPo fEntrustLog);

    Integer batchInsert(List<FEntrustLogPo> list);

    Integer batchUpdate(List<FEntrustLogPo> list);

    /**
     * 存在即更新
     *
     * @param fEntrustLog
     * @return
     */
    Integer upsert(FEntrustLogPo fEntrustLog);

    /**
     * 存在即更新，可选择具体属性
     *
     * @param fEntrustLog
     * @return
     */
    Integer upsertSelective(FEntrustLogPo fEntrustLog);

    List<FEntrustLogPo> query(FEntrustLogPo fEntrustLog);

    List<FEntrustLogResp> queryTradeByParam(FEntrustLogDto fEntrustLogDto);

    Long queryTotal();

    Integer deleteBatch(List<Integer> list);

    /*<AUTOGEN--END>*/

}