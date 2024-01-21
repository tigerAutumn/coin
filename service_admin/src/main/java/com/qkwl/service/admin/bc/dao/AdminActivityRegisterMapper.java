package com.qkwl.service.admin.bc.dao;

import com.qkwl.common.dto.activity_v2.po.AdminActivityRegisterPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 的dao层
 *
 * @author hf
 * @date 2019-06-10 09:20:43
 */
@Mapper
public interface AdminActivityRegisterMapper {

    /*<AUTOGEN--BEGIN>*/

    List<AdminActivityRegisterPo> selectPaged();

    AdminActivityRegisterPo selectByPrimaryKey(Integer id);

    Integer deleteByPrimaryKey(Integer id);

    Integer insert(AdminActivityRegisterPo adminActivityRegister);

    Integer insertSelective(AdminActivityRegisterPo adminActivityRegister);

    Integer insertSelectiveIgnore(AdminActivityRegisterPo adminActivityRegister);

    Integer updateByPrimaryKeySelective(AdminActivityRegisterPo adminActivityRegister);

    Integer updateByPrimaryKey(AdminActivityRegisterPo adminActivityRegister);

    Integer batchInsert(List<AdminActivityRegisterPo> list);

    Integer batchUpdate(List<AdminActivityRegisterPo> list);

    /**
     * 存在即更新
     *
     * @param adminActivityRegister
     * @return
     */
    Integer upsert(AdminActivityRegisterPo adminActivityRegister);

    /**
     * 存在即更新，可选择具体属性
     *
     * @param adminActivityRegister
     * @return
     */
    Integer upsertSelective(AdminActivityRegisterPo adminActivityRegister);

    List<AdminActivityRegisterPo> query(AdminActivityRegisterPo adminActivityRegister);

    Long queryTotal();

    Integer deleteBatch(List<Integer> list);

    /*<AUTOGEN--END>*/

}