package com.qkwl.service.admin.bc.impl.activity_v2;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.activity_v2.bo.AirdropActivityDetailV2Bo;
import com.qkwl.common.dto.activity_v2.po.AirdropActivityDetailV2Po;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.rpc.admin.activity_v2.AirdropActivityDetailV2Service;
import com.qkwl.service.admin.bc.dao.AirdropActivityDetailV2Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * @author hf
 * @date 2019-06-13 02:29:35
 */
@Service("airdropActivityDetailV2Service")
@Slf4j
public class AirdropActivityDetailV2ServiceImpl implements AirdropActivityDetailV2Service {
    /*<AUTOGEN--BEGIN>*/

    @Autowired
    public AirdropActivityDetailV2Mapper airdropActivityDetailV2Mapper;


    @Override
    public Pagination<AirdropActivityDetailV2Po> selectPaged(AirdropActivityDetailV2Bo bo) {
        Pagination<AirdropActivityDetailV2Po> pageInfo = new Pagination();
        log.info("func selectPaged activityDetail query param:[{}],airDropTime is[{}] ", JSON.toJSONString(bo), bo.getAirDropTime().getTime());
        try {
            List<AirdropActivityDetailV2Po> list = new ArrayList<>();
            if (countSelectPageList(bo) > 0) {
                list = airdropActivityDetailV2Mapper.selectPaged(bo);
            }
            log.info("func selectPaged activityDetail query result:[{}] ", JSON.toJSONString(list));
            pageInfo.setData(list);
        } catch (Exception e) {
            log.error("query activityDetail ->{}", e);
            return pageInfo;
        }
        return pageInfo;
    }

    @Override
    public int countSelectPageList(AirdropActivityDetailV2Bo bo) {
        Long count = airdropActivityDetailV2Mapper.countSelectPageList(bo);
        if (count == null) {
            return 0;
        } else {
            return count.intValue();
        }
    }

    @Override
    public PageInfo<AirdropActivityDetailV2Po> selectPagedByType(int currentPage, int pageSize, int type) {
        PageHelper.startPage(currentPage, pageSize);
        List<AirdropActivityDetailV2Po> list = airdropActivityDetailV2Mapper.selectPagedByType(type);
        PageInfo<AirdropActivityDetailV2Po> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public List<AirdropActivityDetailV2Po> selectNotSuccessData() {
        return airdropActivityDetailV2Mapper.selectNotSuccessData();
    }

    @Override
    public AirdropActivityDetailV2Po selectByPrimaryKey(Integer id) {
        return airdropActivityDetailV2Mapper.selectByPrimaryKey(id);
    }

    @Override

    public Integer deleteByPrimaryKey(Integer id) {
        return airdropActivityDetailV2Mapper.deleteByPrimaryKey(id);
    }

    @Override
    public Integer insert(AirdropActivityDetailV2Po airdropActivityDetailV2) {
        return airdropActivityDetailV2Mapper.insert(airdropActivityDetailV2);
    }

    @Override

    public Integer insertSelective(AirdropActivityDetailV2Po airdropActivityDetailV2) {
        return airdropActivityDetailV2Mapper.insertSelective(airdropActivityDetailV2);
    }

    @Override
    public Integer insertSelectiveIgnore(AirdropActivityDetailV2Po airdropActivityDetailV2) {
        return airdropActivityDetailV2Mapper.insertSelectiveIgnore(airdropActivityDetailV2);
    }

    @Override
    public Integer updateByPrimaryKeySelective(AirdropActivityDetailV2Po airdropActivityDetailV2) {
        return airdropActivityDetailV2Mapper.updateByPrimaryKeySelective(airdropActivityDetailV2);
    }

    @Override

    public Integer updateByPrimaryKey(AirdropActivityDetailV2Po airdropActivityDetailV2) {
        return airdropActivityDetailV2Mapper.updateByPrimaryKey(airdropActivityDetailV2);
    }

    @Override

    public Integer batchInsert(List<AirdropActivityDetailV2Po> list) {
        return airdropActivityDetailV2Mapper.batchInsert(list);
    }

    @Override

    public Integer batchUpdate(List<AirdropActivityDetailV2Po> list) {
        return airdropActivityDetailV2Mapper.batchUpdate(list);
    }

    /**
     * 存在即更新
     *
     * @param airdropActivityDetailV2
     * @return
     */
    @Override

    public Integer upsert(AirdropActivityDetailV2Po airdropActivityDetailV2) {
        return airdropActivityDetailV2Mapper.upsert(airdropActivityDetailV2);
    }

    /**
     * 存在即更新，可选择具体属性
     *
     * @param airdropActivityDetailV2
     * @return
     */
    @Override

    public Integer upsertSelective(AirdropActivityDetailV2Po airdropActivityDetailV2) {
        return airdropActivityDetailV2Mapper.upsertSelective(airdropActivityDetailV2);
    }

    @Override
    public List<AirdropActivityDetailV2Po> query(AirdropActivityDetailV2Po airdropActivityDetailV2) {
        return airdropActivityDetailV2Mapper.query(airdropActivityDetailV2);
    }

    @Override
    public Double queryTotalByTypeAndCoin(AirdropActivityDetailV2Po aadv) {
        Double total = airdropActivityDetailV2Mapper.queryTotalByTypeAndCoin(aadv);
        return total == null ? 0 : total;
    }

    @Override
    public Long queryTotal() {
        return airdropActivityDetailV2Mapper.queryTotal();
    }

    @Override

    public Integer deleteBatch(List<Integer> list) {
        return airdropActivityDetailV2Mapper.deleteBatch(list);
    }

    /*<AUTOGEN--END>*/

}
