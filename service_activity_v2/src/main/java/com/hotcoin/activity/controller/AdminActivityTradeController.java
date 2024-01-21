package com.hotcoin.activity.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hotcoin.activity.model.Result;
import com.hotcoin.activity.model.bo.AdminActivityTradeBo;
import com.hotcoin.activity.model.po.AdminActivityTradePo;
import com.hotcoin.activity.service.AdminActivityTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


/**
 * @author hf
 * @date 2019-06-10 09:20:44
 */
@RestController
@RequestMapping("/activityTrade")
public class AdminActivityTradeController {
    @Autowired
    private AdminActivityTradeService adminActivityTradeService;

    /**
     * 分页查询数据
     */
    @RequestMapping("/select_paged")
    public Result<Page<AdminActivityTradePo>> selectPaged(@RequestParam(required = false, defaultValue = "0") int currentPage,
                                                          @RequestParam(required = false, defaultValue = "5") int pageSize) {
        Result<Page<AdminActivityTradePo>> pageResult = new Result<>();
        PageHelper.startPage(currentPage, pageSize);
        Page<AdminActivityTradePo> page = adminActivityTradeService.selectPaged();
        pageResult.savePage(currentPage, pageSize, page.getTotal());
        pageResult.setData(page);
        return pageResult;
    }

    /**
     * 通过id查询
     *
     * @return
     */
    @RequestMapping("/select_by_id")
    public Result<AdminActivityTradePo> selectByPrimaryKey(Integer id) {
        Result<AdminActivityTradePo> result = new Result<>();
        AdminActivityTradePo po = adminActivityTradeService.selectByPrimaryKey(id);
        result.setData(po);
        return result;
    }

    /**
     * 通过ID删除
     *
     * @return
     */
    @RequestMapping(value = "/delete_by_id/{id}", method = RequestMethod.POST)
    public Result<Integer> deleteByPrimaryKey(@PathVariable Integer id) {
        if (id == null) {
            return Result.error("id is required!");
        }
        Result<Integer> result = new Result<>();
        Integer num = adminActivityTradeService.deleteByPrimaryKey(id);
        result.setData(num);
        return result;
    }

    /**
     * 新增数据
     *
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Result<Integer> insert(@RequestBody AdminActivityTradeBo at) {
        List<AdminActivityTradePo> queryData = adminActivityTradeService.query(convertQueryData(at));
        if (queryData != null && !queryData.isEmpty()) {
            return Result.error("data is exist!");
        }
        Result<Integer> result = new Result<>();
        AdminActivityTradePo po = new AdminActivityTradePo(at, new Date());
        Integer num = adminActivityTradeService.insertSelective(po);
        result.setData(num);
        return result;
    }

    /**
     * 修改数据
     *
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result<Integer> updateByPrimaryKeySelective(@RequestBody AdminActivityTradeBo at) {
        if (at.getId() == null) {
            return Result.error("id is required!");
        }
        Result<Integer> result = new Result<>();
        AdminActivityTradePo po = new AdminActivityTradePo(at);
        Integer num = adminActivityTradeService.updateByPrimaryKeySelective(po);
        result.setData(num);
        return result;
    }


    /**
     * 查询列表
     *
     * @return
     */
    @RequestMapping("/query_list")
    public Result<List<AdminActivityTradePo>> queryByCondition(AdminActivityTradePo adminActivityTrade) {
        Result<List<AdminActivityTradePo>> result = new Result<>();
        List<AdminActivityTradePo> list = adminActivityTradeService.query(adminActivityTrade);
        result.setData(list);
        return result;
    }

    public AdminActivityTradePo convertQueryData(AdminActivityTradeBo ab) {
        AdminActivityTradePo arp = new AdminActivityTradePo();
        arp.setAirDropCoin(ab.getTradeCoin());
        arp.setAirDropRule(ab.getAirDropRule());
        arp.setAirDropType(ab.getAirDropType());
        return arp;
    }
}
