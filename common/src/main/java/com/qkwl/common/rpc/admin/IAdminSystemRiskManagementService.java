package com.qkwl.common.rpc.admin;

import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.riskmanagement.SystemRiskManagementDto;

import java.util.List;

/**
 * service层
 *
 * @author hf
 * @date 2019-08-17 07:10:44
 * @since jdk 1.8
 */
public interface IAdminSystemRiskManagementService {

    /*<AUTOGEN--BEGIN>*/

    PageInfo<SystemRiskManagementDto> selectPaged(SystemRiskManagementDto dto, int currentPage, int pageSize);

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
