package com.hotcoin.activity.controller;

import com.github.pagehelper.Page;
import com.hotcoin.activity.model.Result;
import com.hotcoin.activity.model.po.AdminActivityItemsPo;
import com.hotcoin.activity.service.AdminActivityItemsService;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author hf
 * @date 2019-06-10 09:20:40
 * 空投活动列表
 */
@RestController
@RequestMapping("/activityItems")
public class AdminActivityItemsController {
    @Autowired
    private AdminActivityItemsService adminActivityItemsService;

    /**
     * 分页查询数据
     */
    @RequestMapping("/select_paged")
    public Result<Page<AdminActivityItemsPo>> selectPaged() {
        Result<Page<AdminActivityItemsPo>> pageResult = new Result<>();
        Page<AdminActivityItemsPo> page = adminActivityItemsService.selectPaged();
        pageResult.setData(page);
        return pageResult;
    }

    /**
     * 通过id查询
     *
     * @return
     */
    @RequestMapping("/select_by_id")
    public Result<AdminActivityItemsPo> selectByPrimaryKey(Integer id) {
        Result<AdminActivityItemsPo> result = new Result<>();
        AdminActivityItemsPo po = adminActivityItemsService.selectByPrimaryKey(id);
        result.setData(po);
        return result;
    }

    /**
     * 通过ID删除
     *
     * @return
     */
    @RequestMapping("/delete_by_id")
    public Result<Integer> deleteByPrimaryKey(Integer id) {
        Result<Integer> result = new Result<>();
        Integer num = adminActivityItemsService.deleteByPrimaryKey(id);
        result.setData(num);
        return result;
    }

    /**
     * 新增数据
     *
     * @return
     */
    @RequestMapping("/save_adminActivityItems")
    public Result<Integer> insert(AdminActivityItemsPo adminActivityItems) {
        Result<Integer> result = new Result<>();
        Integer num = adminActivityItemsService.insertSelective(adminActivityItems);
        result.setData(num);
        return result;
    }

    /**
     * 修改数据
     *
     * @return
     */
    @RequestMapping("/update_adminActivityItems")
    public Result<Integer> updateByPrimaryKeySelective(AdminActivityItemsPo adminActivityItems) {
        Result<Integer> result = new Result<>();
        Integer num = adminActivityItemsService.updateByPrimaryKeySelective(adminActivityItems);
        result.setData(num);
        return result;
    }


    /**
     * 查询已激活的空投活动列表
     *
     * @return
     */
    @RequestMapping("/query_list")
    public Result<List<AdminActivityItemsPo>> queryByCondition() {
        Result<List<AdminActivityItemsPo>> result = new Result<>();
        AdminActivityItemsPo adminActivityItems = new AdminActivityItemsPo();
        adminActivityItems.setStatus(1);
        List<AdminActivityItemsPo> list = adminActivityItemsService.query(adminActivityItems);
        result.setData(list);
        return result;
    }

}
