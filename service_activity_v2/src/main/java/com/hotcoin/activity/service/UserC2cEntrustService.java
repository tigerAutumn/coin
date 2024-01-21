package com.hotcoin.activity.service;

import com.github.pagehelper.PageInfo;
import com.hotcoin.activity.model.param.UserC2cEntrustDto;
import com.hotcoin.activity.model.po.UserC2cEntrustPo;
import com.hotcoin.activity.model.resp.UserC2cEntrustResp;

import java.util.List;

/**
 * service层
 *
 * @author hf
 * @date 2019-06-28 05:57:15
 * @since jdk 1.8
 */
public interface UserC2cEntrustService {

    /*<AUTOGEN--BEGIN>*/

    PageInfo<UserC2cEntrustPo> selectPaged(int currentPage, int pageSize);

    UserC2cEntrustPo selectByPrimaryKey(Integer id);

    Integer deleteByPrimaryKey(Integer id);

    Integer insert(UserC2cEntrustPo userC2cEntrust);

    Integer insertSelective(UserC2cEntrustPo userC2cEntrust);

    Integer insertSelectiveIgnore(UserC2cEntrustPo userC2cEntrust);

    Integer updateByPrimaryKeySelective(UserC2cEntrustPo userC2cEntrust);

    Integer updateByPrimaryKey(UserC2cEntrustPo userC2cEntrust);

    Integer batchInsert(List<UserC2cEntrustPo> list);

    Integer batchUpdate(List<UserC2cEntrustPo> list);

    /**
     * 存在即更新
     *
     * @param userC2cEntrust
     * @return
     */
    Integer upsert(UserC2cEntrustPo userC2cEntrust);

    /**
     * 存在即更新，可选择具体属性
     *
     * @param userC2cEntrust
     * @return
     */
    Integer upsertSelective(UserC2cEntrustPo userC2cEntrust);

    List<UserC2cEntrustPo> query(UserC2cEntrustPo userC2cEntrust);

    List<UserC2cEntrustResp> queryByDtoParam(UserC2cEntrustDto cEntrustDto);

    Long queryTotal();

    Integer deleteBatch(List<Integer> list);

    /*<AUTOGEN--END>*/

}
