package com.qkwl.service.admin.bc.dao;

import com.qkwl.common.dto.riskmanagement.SystemRiskManagementDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ProjectName: service_admin
 * @Package: com.qkwl.service.admin.bc.dao
 * @ClassName: RiskManagementMapper
 * @Author: hf
 * @Description:
 * @Date: 2019/8/19 10:24
 * @Version: 1.0
 */
@Mapper
public interface RiskManagementMapper {
    /*<AUTOGEN--BEGIN>*/

    List<SystemRiskManagementDto> selectPaged();

    SystemRiskManagementDto selectByPrimaryKey(Integer id);

    Integer deleteByPrimaryKey(Integer id);

    Integer insert(SystemRiskManagementDto systemRiskManagement);

    Integer insertSelective(SystemRiskManagementDto systemRiskManagement);

    Integer insertSelectiveIgnore(SystemRiskManagementDto systemRiskManagement);

    Integer updateByPrimaryKeySelective(SystemRiskManagementDto systemRiskManagement);

    Integer updateByPrimaryKey(SystemRiskManagementDto systemRiskManagement);

    Integer batchInsert(List<SystemRiskManagementDto> list);

    Integer batchUpdate(List<SystemRiskManagementDto> list);

    /**
     * 存在即更新
     *
     * @param systemRiskManagement
     * @return
     */
    Integer upsert(SystemRiskManagementDto systemRiskManagement);

    /**
     * 存在即更新，可选择具体属性
     *
     * @param systemRiskManagement
     * @return
     */
    Integer upsertSelective(SystemRiskManagementDto systemRiskManagement);

    List<SystemRiskManagementDto> query(SystemRiskManagementDto systemRiskManagement);

    Long queryTotal();

    Integer deleteBatch(List<Integer> list);

    /*<AUTOGEN--END>*/
}
