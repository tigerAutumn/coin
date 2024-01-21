package com.hotcoin.activity.dao;

import com.github.pagehelper.Page;
import com.hotcoin.activity.model.po.AirdropRecordV2Po;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * 的dao层
 *
 * @author hf
 * @date 2019-06-10 09:20:46
 */
@Mapper
public interface AirdropRecordV2Dao {

    /*<AUTOGEN--BEGIN>*/

    Page<AirdropRecordV2Po> selectPaged(RowBounds rowBounds);

    AirdropRecordV2Po selectByPrimaryKey(Integer id);

    Integer deleteByPrimaryKey(Integer id);

    Integer insert(AirdropRecordV2Po airdropRecordV2);

    Integer insertSelective(AirdropRecordV2Po airdropRecordV2);

    Integer insertSelectiveIgnore(AirdropRecordV2Po airdropRecordV2);

    Integer updateByPrimaryKeySelective(AirdropRecordV2Po airdropRecordV2);

    Integer updateByPrimaryKey(AirdropRecordV2Po airdropRecordV2);

    Integer batchInsert(List<AirdropRecordV2Po> list);

    Integer batchUpdate(List<AirdropRecordV2Po> list);

    /**
     * 存在即更新
     *
     * @param airdropRecordV2
     * @return
     */
    Integer upsert(AirdropRecordV2Po airdropRecordV2);

    /**
     * 存在即更新，可选择具体属性
     *
     * @param airdropRecordV2
     * @return
     */
    Integer upsertSelective(AirdropRecordV2Po airdropRecordV2);

    List<AirdropRecordV2Po> query(AirdropRecordV2Po airdropRecordV2);

    Long queryTotal();

    Integer deleteBatch(List<Integer> list);

    /*<AUTOGEN--END>*/

}