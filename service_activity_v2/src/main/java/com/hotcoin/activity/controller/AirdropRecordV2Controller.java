package com.hotcoin.activity.controller;

import com.github.pagehelper.Page;
import com.hotcoin.activity.model.Result;
import com.hotcoin.activity.model.po.AirdropRecordV2Po;
import com.hotcoin.activity.service.AirdropRecordV2Service;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author hf
 * @date 2019-06-10 09:20:46
 * 活动期间和空投相关的临时记录
 */
@RestController
@RequestMapping("/airDropRecord")
public class AirdropRecordV2Controller {
    @Autowired
    private AirdropRecordV2Service airdropRecordV2Service;

    /**
     * 分页查询数据
     */
    @RequestMapping("/select_paged")
    public Result<Page<AirdropRecordV2Po>> selectPaged(RowBounds rowBounds) {
        Result<Page<AirdropRecordV2Po>> pageResult = new Result<>();
        Page<AirdropRecordV2Po> page = airdropRecordV2Service.selectPaged(rowBounds);
        pageResult.setData(page);
        return pageResult;
    }

    /**
     * 通过id查询
     *
     * @return
     */
    @RequestMapping("/select_by_id")
    public Result<AirdropRecordV2Po> selectByPrimaryKey(Integer id) {
        Result<AirdropRecordV2Po> result = new Result<>();
        AirdropRecordV2Po po = airdropRecordV2Service.selectByPrimaryKey(id);
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
        Integer num = airdropRecordV2Service.deleteByPrimaryKey(id);
        result.setData(num);
        return result;
    }

    /**
     * 新增数据
     *
     * @return
     */
    @RequestMapping("/save_airdropRecordV2")
    public Result<Integer> insert(AirdropRecordV2Po airdropRecordV2) {
        Result<Integer> result = new Result<>();
        Integer num = airdropRecordV2Service.insertSelective(airdropRecordV2);
        result.setData(num);
        return result;
    }

    /**
     * 修改数据
     *
     * @return
     */
    @RequestMapping("/update_airdropRecordV2")
    public Result<Integer> updateByPrimaryKeySelective(AirdropRecordV2Po airdropRecordV2) {
        Result<Integer> result = new Result<>();
        Integer num = airdropRecordV2Service.updateByPrimaryKeySelective(airdropRecordV2);
        result.setData(num);
        return result;
    }


    /**
     * 查询列表
     *
     * @return
     */
    @RequestMapping("/query_list")
    public Result<List<AirdropRecordV2Po>> queryByCondition(AirdropRecordV2Po airdropRecordV2) {
        Result<List<AirdropRecordV2Po>> result = new Result<>();
        List<AirdropRecordV2Po> list = airdropRecordV2Service.query(airdropRecordV2);
        result.setData(list);
        return result;
    }

}
