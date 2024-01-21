package com.qkwl.service.admin.bc.impl.activity_v2;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.activity_v2.po.AdminActivityRegisterPo;
import com.qkwl.common.rpc.admin.activity_v2.AdminActivityRegisterService;
import com.qkwl.service.admin.bc.dao.AdminActivityRegisterMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author hf
 * @date 2019-06-10 09:50:42
 */
@Slf4j
@Service("adminActivityRegisterService")
public class AdminActivityRegisterServiceImpl implements AdminActivityRegisterService {
    /*<AUTOGEN--BEGIN>*/

    @Autowired
    public AdminActivityRegisterMapper adminActivityRegisterMapper;

    @Override
    public PageInfo<AdminActivityRegisterPo> selectPaged(int currentPage, int pageSize) {
        PageHelper.startPage(currentPage, pageSize);
        List<AdminActivityRegisterPo> list = adminActivityRegisterMapper.selectPaged();
        PageInfo<AdminActivityRegisterPo> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }

    @Override
    public AdminActivityRegisterPo selectByPrimaryKey(Integer id) {
        return adminActivityRegisterMapper.selectByPrimaryKey(id);
    }

    @Override
    public Integer deleteByPrimaryKey(Integer id) {
        return adminActivityRegisterMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Integer insert(AdminActivityRegisterPo adminActivityRegister) {
        return adminActivityRegisterMapper.insert(adminActivityRegister);
    }

    @Override
    public Integer insertSelective(AdminActivityRegisterPo adminActivityRegister) {
        log.error("save register content ->{}", JSON.toJSONString(adminActivityRegister));
        return adminActivityRegisterMapper.insertSelective(adminActivityRegister);
    }

    @Override
    public Integer insertSelectiveIgnore(AdminActivityRegisterPo adminActivityRegister) {
        return adminActivityRegisterMapper.insertSelectiveIgnore(adminActivityRegister);
    }

    @Override
    public Integer updateByPrimaryKeySelective(AdminActivityRegisterPo adminActivityRegister) {
        return adminActivityRegisterMapper.updateByPrimaryKeySelective(adminActivityRegister);
    }

    @Override
    public Integer updateByPrimaryKey(AdminActivityRegisterPo adminActivityRegister) {
        return adminActivityRegisterMapper.updateByPrimaryKey(adminActivityRegister);
    }

    @Override
    public Integer batchInsert(List<AdminActivityRegisterPo> list) {
        return adminActivityRegisterMapper.batchInsert(list);
    }

    @Override
    public Integer batchUpdate(List<AdminActivityRegisterPo> list) {
        return adminActivityRegisterMapper.batchUpdate(list);
    }

    /**
     * 存在即更新
     *
     * @param adminActivityRegister
     * @return
     */
    @Override
    public Integer upsert(AdminActivityRegisterPo adminActivityRegister) {
        return adminActivityRegisterMapper.upsert(adminActivityRegister);
    }

    /**
     * 存在即更新，可选择具体属性
     *
     * @param adminActivityRegister
     * @return
     */
    @Override
    public Integer upsertSelective(AdminActivityRegisterPo adminActivityRegister) {
        return adminActivityRegisterMapper.upsertSelective(adminActivityRegister);
    }

    @Override
    public List<AdminActivityRegisterPo> query(AdminActivityRegisterPo adminActivityRegister) {
        return adminActivityRegisterMapper.query(adminActivityRegister);
    }

    @Override
    public Long queryTotal() {
        return adminActivityRegisterMapper.queryTotal();
    }

    @Override
    public Integer deleteBatch(List<Integer> list) {
        return adminActivityRegisterMapper.deleteBatch(list);
    }

    /*<AUTOGEN--END>*/

}
