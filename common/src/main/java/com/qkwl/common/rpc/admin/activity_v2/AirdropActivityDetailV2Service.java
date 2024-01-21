package com.qkwl.common.rpc.admin.activity_v2;

import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.activity_v2.bo.AirdropActivityDetailV2Bo;
import com.qkwl.common.dto.activity_v2.po.AirdropActivityDetailV2Po;
import com.qkwl.common.dto.common.Pagination;

import java.util.List;

/**
 * service层
 *
 * @author hf
 * @date 2019-06-13 02:29:35
 */
public interface AirdropActivityDetailV2Service {

    /*<AUTOGEN--BEGIN>*/

    Pagination<AirdropActivityDetailV2Po> selectPaged(AirdropActivityDetailV2Bo bo);

    int countSelectPageList(AirdropActivityDetailV2Bo bo);

    PageInfo<AirdropActivityDetailV2Po> selectPagedByType(int currentPage, int pageSize, int type);

    List<AirdropActivityDetailV2Po> selectNotSuccessData();

    AirdropActivityDetailV2Po selectByPrimaryKey(Integer id);

    Integer deleteByPrimaryKey(Integer id);

    Integer insert(AirdropActivityDetailV2Po airdropActivityDetailV2);

    Integer insertSelective(AirdropActivityDetailV2Po airdropActivityDetailV2);

    Integer insertSelectiveIgnore(AirdropActivityDetailV2Po airdropActivityDetailV2);

    Integer updateByPrimaryKeySelective(AirdropActivityDetailV2Po airdropActivityDetailV2);

    Integer updateByPrimaryKey(AirdropActivityDetailV2Po airdropActivityDetailV2);

    Integer batchInsert(List<AirdropActivityDetailV2Po> list);

    Integer batchUpdate(List<AirdropActivityDetailV2Po> list);

    /**
     * 存在即更新
     *
     * @param airdropActivityDetailV2
     * @return
     */
    Integer upsert(AirdropActivityDetailV2Po airdropActivityDetailV2);

    /**
     * 存在即更新，可选择具体属性
     *
     * @param airdropActivityDetailV2
     * @return
     */
    Integer upsertSelective(AirdropActivityDetailV2Po airdropActivityDetailV2);

    List<AirdropActivityDetailV2Po> query(AirdropActivityDetailV2Po airdropActivityDetailV2);

    /**
     * 1:注册空投活动,2:充值空投活动3:交易空投活动
     *
     * @param aadv
     * @return
     */
    Double queryTotalByTypeAndCoin(AirdropActivityDetailV2Po aadv);

    Long queryTotal();

    Integer deleteBatch(List<Integer> list);

    /*<AUTOGEN--END>*/

}
