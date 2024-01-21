package com.qkwl.service.admin.bc.impl.activity_v2;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.activity_v2.po.AdminActivityTradePo;
import com.qkwl.common.rpc.admin.activity_v2.AdminActivityTradeService;
import com.qkwl.service.admin.bc.dao.AdminActivityTradeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author hf
 * @date 2019-06-10 09:50:42
 */
@Service("adminActivityTradeService")
@Slf4j
public class AdminActivityTradeServiceImpl implements AdminActivityTradeService {
    /*<AUTOGEN--BEGIN>*/

    @Autowired
    public AdminActivityTradeMapper adminActivityTradeMapper;


    @Override
    public PageInfo<AdminActivityTradePo> selectPaged(int currentPage, int pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<AdminActivityTradePo> list = adminActivityTradeMapper.selectPaged();
        PageInfo<AdminActivityTradePo> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public AdminActivityTradePo selectByPrimaryKey(Integer id) {
        return adminActivityTradeMapper.selectByPrimaryKey(id);
    }

    @Override

    public Integer deleteByPrimaryKey(Integer id) {
        return adminActivityTradeMapper.deleteByPrimaryKey(id);
    }

    @Override

    public Integer insert(AdminActivityTradePo adminActivityTrade) {
        return adminActivityTradeMapper.insert(adminActivityTrade);
    }

    @Override

    public Integer insertSelective(AdminActivityTradePo adminActivityTrade) {
        return adminActivityTradeMapper.insertSelective(adminActivityTrade);
    }

    @Override

    public Integer insertSelectiveIgnore(AdminActivityTradePo adminActivityTrade) {
        return adminActivityTradeMapper.insertSelectiveIgnore(adminActivityTrade);
    }

    @Override

    public Integer updateByPrimaryKeySelective(AdminActivityTradePo adminActivityTrade) {
        return adminActivityTradeMapper.updateByPrimaryKeySelective(adminActivityTrade);
    }

    @Override

    public Integer updateByPrimaryKey(AdminActivityTradePo adminActivityTrade) {
        return adminActivityTradeMapper.updateByPrimaryKey(adminActivityTrade);
    }

    @Override

    public Integer batchInsert(List<AdminActivityTradePo> list) {
        return adminActivityTradeMapper.batchInsert(list);
    }

    @Override

    public Integer batchUpdate(List<AdminActivityTradePo> list) {
        return adminActivityTradeMapper.batchUpdate(list);
    }

    /**
     * 存在即更新
     *
     * @param adminActivityTrade
     * @return
     */
    @Override

    public Integer upsert(AdminActivityTradePo adminActivityTrade) {
        return adminActivityTradeMapper.upsert(adminActivityTrade);
    }

    /**
     * 存在即更新，可选择具体属性
     *
     * @param adminActivityTrade
     * @return
     */
    @Override

    public Integer upsertSelective(AdminActivityTradePo adminActivityTrade) {
        return adminActivityTradeMapper.upsertSelective(adminActivityTrade);
    }

    @Override
    public List<AdminActivityTradePo> query(AdminActivityTradePo adminActivityTrade) {
        return adminActivityTradeMapper.query(adminActivityTrade);
    }

    @Override
    public Long queryTotal() {
        return adminActivityTradeMapper.queryTotal();
    }

    @Override

    public Integer deleteBatch(List<Integer> list) {
        return adminActivityTradeMapper.deleteBatch(list);
    }

    /*<AUTOGEN--END>*/

}
