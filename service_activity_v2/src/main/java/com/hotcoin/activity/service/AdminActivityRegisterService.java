package com.hotcoin.activity.service;

import com.github.pagehelper.Page;
import com.hotcoin.activity.model.po.AdminActivityRegisterPo;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * service层
 *
 * @author hf
 * @date 2019-06-10 09:20:43
 */
public interface AdminActivityRegisterService {

    /*<AUTOGEN--BEGIN>*/

    Page<AdminActivityRegisterPo> selectPaged();

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
