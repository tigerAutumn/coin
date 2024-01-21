package com.qkwl.service.admin.bc.impl.activity_v2;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.activity_v2.po.AdminActivityRechargePo;
import com.qkwl.common.rpc.admin.activity_v2.AdminActivityRechargeService;
import com.qkwl.service.admin.bc.dao.AdminActivityRechargeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author hf
 * @date 2019-06-10 09:50:42
 */
@Slf4j
@Service("adminActivityRechargeService")
public class AdminActivityRechargeServiceImpl implements AdminActivityRechargeService {
    /*<AUTOGEN--BEGIN>*/

    @Autowired
    public AdminActivityRechargeMapper adminActivityRechargeMapper;


    @Override
    public PageInfo<AdminActivityRechargePo> selectPaged(int currentPage, int pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<AdminActivityRechargePo> list = adminActivityRechargeMapper.selectPaged();
        PageInfo<AdminActivityRechargePo> pageInfo = new PageInfo<>(list);
        log.error("get param is {},{},result is{}", currentPage, pageSize, JSON.toJSONString(pageInfo));
        return pageInfo;
    }

    @Override
    public AdminActivityRechargePo selectByPrimaryKey(Integer id) {
        return adminActivityRechargeMapper.selectByPrimaryKey(id);
    }

    @Override
    public Integer deleteByPrimaryKey(Integer id) {
        return adminActivityRechargeMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Integer insert(AdminActivityRechargePo adminActivityRecharge) {
        return adminActivityRechargeMapper.insert(adminActivityRecharge);
    }

    @Override
    public Integer insertSelective(AdminActivityRechargePo adminActivityRecharge) {
        return adminActivityRechargeMapper.insertSelective(adminActivityRecharge);
    }

    @Override
    public Integer insertSelectiveIgnore(AdminActivityRechargePo adminActivityRecharge) {
        return adminActivityRechargeMapper.insertSelectiveIgnore(adminActivityRecharge);
    }

    @Override
    public Integer updateByPrimaryKeySelective(AdminActivityRechargePo adminActivityRecharge) {
        return adminActivityRechargeMapper.updateByPrimaryKeySelective(adminActivityRecharge);
    }

    @Override
    public Integer updateByPrimaryKey(AdminActivityRechargePo adminActivityRecharge) {
        return adminActivityRechargeMapper.updateByPrimaryKey(adminActivityRecharge);
    }

    @Override
    public Integer batchInsert(List<AdminActivityRechargePo> list) {
        return adminActivityRechargeMapper.batchInsert(list);
    }

    @Override
    public Integer batchUpdate(List<AdminActivityRechargePo> list) {
        return adminActivityRechargeMapper.batchUpdate(list);
    }

    /**
     * 存在即更新
     *
     * @param adminActivityRecharge
     * @return
     */
    @Override
    public Integer upsert(AdminActivityRechargePo adminActivityRecharge) {
        return adminActivityRechargeMapper.upsert(adminActivityRecharge);
    }

    /**
     * 存在即更新，可选择具体属性
     *
     * @param adminActivityRecharge
     * @return
     */
    @Override
    public Integer upsertSelective(AdminActivityRechargePo adminActivityRecharge) {
        return adminActivityRechargeMapper.upsertSelective(adminActivityRecharge);
    }

    @Override
    public List<AdminActivityRechargePo> query(AdminActivityRechargePo adminActivityRecharge) {
        return adminActivityRechargeMapper.query(adminActivityRecharge);
    }

    @Override
    public Long queryTotal() {
        return adminActivityRechargeMapper.queryTotal();
    }

    @Override
    public Integer deleteBatch(List<Integer> list) {
        return adminActivityRechargeMapper.deleteBatch(list);
    }

    /*<AUTOGEN--END>*/

}
