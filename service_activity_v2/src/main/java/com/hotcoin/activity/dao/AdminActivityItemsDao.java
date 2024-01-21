package com.hotcoin.activity.dao;

import com.github.pagehelper.Page;
import com.hotcoin.activity.model.po.AdminActivityItemsPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 的dao层
 *
 * @author hf
 * @date 2019-06-10 09:20:40
 */
@Mapper
public interface AdminActivityItemsDao {

    /*<AUTOGEN--BEGIN>*/

    Page<AdminActivityItemsPo> selectPaged();

    AdminActivityItemsPo selectByPrimaryKey(Integer id);

    Integer deleteByPrimaryKey(Integer id);

    Integer insert(AdminActivityItemsPo adminActivityItems);

    Integer insertSelective(AdminActivityItemsPo adminActivityItems);

    Integer insertSelectiveIgnore(AdminActivityItemsPo adminActivityItems);

    Integer updateByPrimaryKeySelective(AdminActivityItemsPo adminActivityItems);

    Integer updateByPrimaryKey(AdminActivityItemsPo adminActivityItems);

    Integer batchInsert(List<AdminActivityItemsPo> list);

    Integer batchUpdate(List<AdminActivityItemsPo> list);

    /**
     * 存在即更新
     *
     * @param adminActivityItems
     * @return
     */
    Integer upsert(AdminActivityItemsPo adminActivityItems);

    /**
     * 存在即更新，可选择具体属性
     *
     * @param adminActivityItems
     * @return
     */
    Integer upsertSelective(AdminActivityItemsPo adminActivityItems);

    List<AdminActivityItemsPo> query(AdminActivityItemsPo adminActivityItems);

    Long queryTotal();

    Integer deleteBatch(List<Integer> list);

    /*<AUTOGEN--END>*/

}