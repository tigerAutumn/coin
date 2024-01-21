package com.hotcoin.activity.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hotcoin.activity.model.Result;
import com.hotcoin.activity.model.po.AirdropActivityDetailV2Po;
import com.hotcoin.activity.service.AirdropActivityDetailV2Service;
import com.hotcoin.activity.tasks.UserTradeActivityTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author hf
 * @date 2019-06-13 02:29:35
 */
@RestController
@RequestMapping("/activityDetail")
@Slf4j
public class AirdropActivityDetailV2Controller {
    @Autowired
    private AirdropActivityDetailV2Service airdropActivityDetailV2Service;

    @Autowired
    private UserTradeActivityTask userTradeActivityTask;

    /**
     * 分页查询数据
     */
    @RequestMapping("/test")
    public Result test() {
        Result pageResult = new Result<>();
        userTradeActivityTask.runTradeActivityTask();
        return pageResult;
    }

    /**
     * 分页查询数据
     */
    @RequestMapping("/select_paged")
    public Result<Page<AirdropActivityDetailV2Po>> selectPaged(@RequestParam(required = false, defaultValue = "0") int currentPage,
                                                               @RequestParam(required = false, defaultValue = "5") int pageSize) {
        Result pageResult = new Result<>();
        AirdropActivityDetailV2Po   activityDetailV2Po = new AirdropActivityDetailV2Po();
        activityDetailV2Po.setActivityType(2);
        activityDetailV2Po.setActivityCoin("GAVC");
        activityDetailV2Po.setTypeDetail("legalMoney");
        activityDetailV2Po.setRule(300.0);
        List<AirdropActivityDetailV2Po> page = airdropActivityDetailV2Service.selectPaged(activityDetailV2Po);
        pageResult.setData(page);
        return pageResult;
    }

    /**
     * 通过id查询
     *
     * @return
     */
    @RequestMapping("/select_by_id")
    public Result<AirdropActivityDetailV2Po> selectByPrimaryKey(Integer id) {
        Result<AirdropActivityDetailV2Po> result = new Result<>();
        AirdropActivityDetailV2Po po = airdropActivityDetailV2Service.selectByPrimaryKey(id);
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
        Integer num = airdropActivityDetailV2Service.deleteByPrimaryKey(id);
        result.setData(num);
        return result;
    }


    /**
     * 新增数据
     *
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Result<Integer> insert(@RequestBody AirdropActivityDetailV2Po airdropActivityDetailV2) {
        Result<Integer> result = new Result<>();
        Integer num = airdropActivityDetailV2Service.insertSelective(airdropActivityDetailV2);
        result.setData(num);
        return result;
    }


    /**
     * 修改数据
     *
     * @return
     */
    @RequestMapping("/update")
    public Result<Integer> updateByPrimaryKeySelective(AirdropActivityDetailV2Po airdropActivityDetailV2) {
        Result<Integer> result = new Result<>();
        Integer num = airdropActivityDetailV2Service.updateByPrimaryKeySelective(airdropActivityDetailV2);
        result.setData(num);
        return result;
    }


    /**
     * 查询列表
     *
     * @return
     */
    @RequestMapping("/query_list")
    public Result<List<AirdropActivityDetailV2Po>> queryByCondition(AirdropActivityDetailV2Po airdropActivityDetailV2) {
        Result<List<AirdropActivityDetailV2Po>> result = new Result<>();
        List<AirdropActivityDetailV2Po> list = airdropActivityDetailV2Service.query(airdropActivityDetailV2);
        result.setData(list);
        return result;
    }

}
