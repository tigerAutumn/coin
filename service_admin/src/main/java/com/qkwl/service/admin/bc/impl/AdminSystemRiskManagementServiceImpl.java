package com.qkwl.service.admin.bc.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.riskmanagement.SystemRiskManagementDto;
import com.qkwl.common.rpc.admin.IAdminSystemRiskManagementService;
import com.qkwl.service.admin.bc.dao.RiskManagementMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @ProjectName: service_admin
 * @Package: com.qkwl.service.admin.bc.impl
 * @ClassName: AdminSystemRiskManagementServiceImpl
 * @Author: hf
 * @Description:
 * @Date: 2019/8/19 10:15
 * @Version: 1.0
 */
@Slf4j
@Service("adminSystemRiskManagementService")
public class AdminSystemRiskManagementServiceImpl implements IAdminSystemRiskManagementService {
    @Autowired
    private RiskManagementMapper riskManagementMapper;

    @Override
    public PageInfo<SystemRiskManagementDto> selectPaged(SystemRiskManagementDto dto, int currentPage, int pageSize) {
        PageInfo<SystemRiskManagementDto> pageInfo = new PageInfo<>();
        PageHelper.startPage(currentPage, pageSize);
        List<SystemRiskManagementDto> list = riskManagementMapper.query(dto);
        if (CollectionUtils.isEmpty(list)) {
            return pageInfo;
        }
        pageInfo.setList(list);
        return pageInfo;
    }

    @Override
    public SystemRiskManagementDto selectByPrimaryKey(Integer id) {
        return riskManagementMapper.selectByPrimaryKey(id);
    }

    @Override
    public Integer deleteByPrimaryKey(Integer id) {
        return riskManagementMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Integer insert(SystemRiskManagementDto systemRiskManagement) {
        return riskManagementMapper.insert(systemRiskManagement);
    }

    @Override
    public Integer insertSelective(SystemRiskManagementDto systemRiskManagement) {
        return riskManagementMapper.insertSelective(systemRiskManagement);
    }

    @Override
    public Integer insertSelectiveIgnore(SystemRiskManagementDto systemRiskManagement) {
        return riskManagementMapper.insertSelectiveIgnore(systemRiskManagement);
    }

    @Override
    public Integer updateByPrimaryKeySelective(SystemRiskManagementDto systemRiskManagement) {
        return riskManagementMapper.updateByPrimaryKeySelective(systemRiskManagement);
    }

    @Override
    public Integer updateByPrimaryKey(SystemRiskManagementDto systemRiskManagement) {
        try {
            return riskManagementMapper.updateByPrimaryKey(systemRiskManagement);
        } catch (Exception e) {
            log.error("update riskManagement Log fail ->{}", e);
            return null;
        }
    }

    @Override
    public Integer batchInsert(List<SystemRiskManagementDto> list) {
        return riskManagementMapper.batchInsert(list);
    }

    @Override
    public Integer batchUpdate(List<SystemRiskManagementDto> list) {
        return riskManagementMapper.batchUpdate(list);
    }

    /**
     * 存在即更新
     *
     * @param systemRiskManagement
     * @return
     */
    @Override
    public Integer upsert(SystemRiskManagementDto systemRiskManagement) {
        return riskManagementMapper.upsert(systemRiskManagement);
    }

    /**
     * 存在即更新，可选择具体属性
     *
     * @param systemRiskManagement
     * @return
     */
    @Override
    public Integer upsertSelective(SystemRiskManagementDto systemRiskManagement) {
        return riskManagementMapper.upsertSelective(systemRiskManagement);
    }

    @Override
    public List<SystemRiskManagementDto> query(SystemRiskManagementDto systemRiskManagement) {
        return riskManagementMapper.query(systemRiskManagement);
    }

    @Override
    public Long queryTotal() {
        return riskManagementMapper.queryTotal();
    }

    @Override
    public Integer deleteBatch(List<Integer> list) {
        return riskManagementMapper.deleteBatch(list);
    }

    /*<AUTOGEN--END>*/
}
