package com.hotcoin.activity.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hotcoin.activity.model.Result;
import com.hotcoin.activity.model.bo.AdminActivityRechargeBo;
import com.hotcoin.activity.model.po.AdminActivityRechargePo;
import com.hotcoin.activity.service.AdminActivityRechargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


/**
 * @author hf
 * @date 2019-06-10 09:20:42
 * 充值空投控制器
 */
@RestController
@RequestMapping("/activityRecharge")
public class AdminActivityRechargeController {
    @Autowired
    private AdminActivityRechargeService adminActivityRechargeService;

    /**
     * 分页查询数据
     */
    @RequestMapping("/select_paged")
    public Result<Page<AdminActivityRechargePo>> selectPaged(@RequestParam(required = false, defaultValue = "0") int currentPage,
                                                             @RequestParam(required = false, defaultValue = "5") int pageSize) {
        Result<Page<AdminActivityRechargePo>> result = new Result<>();
        PageHelper.startPage(currentPage, pageSize);
        Page<AdminActivityRechargePo> page = adminActivityRechargeService.selectPaged();
        result.savePage(currentPage, pageSize, page.getTotal());
        result.setData(page);
        return result;
    }

    /**
     * 通过id查询
     *
     * @return
     */
    @RequestMapping("/select_by_id")
    public Result<AdminActivityRechargePo> selectByPrimaryKey(@RequestParam Integer id) {
        Result<AdminActivityRechargePo> result = new Result<>();
        AdminActivityRechargePo po = adminActivityRechargeService.selectByPrimaryKey(id);
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
        Integer num = adminActivityRechargeService.deleteByPrimaryKey(id);
        result.setData(num);
        return result;
    }

    /**
     * 新增数据
     *
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Result<Integer> insert(@RequestBody AdminActivityRechargeBo ab) {
        List<AdminActivityRechargePo> queryData = adminActivityRechargeService.query(convertQueryData(ab));
        if (queryData != null && !queryData.isEmpty()) {
            return Result.error("data is exist!");
        }
        Result<Integer> result = new Result<>();
        AdminActivityRechargePo arp = new AdminActivityRechargePo(ab, new Date());
        Integer num = adminActivityRechargeService.insertSelective(arp);
        result.setData(num);
        return result;
    }

    /**
     * 修改数据
     *
     * @return
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Result<Integer> updateByPrimaryKeySelective(@RequestBody AdminActivityRechargeBo ab) {
        Result<Integer> result = new Result<>();
        if (ab.getId() == null) {
            return Result.error("id is required!");
        }
        AdminActivityRechargePo arp = new AdminActivityRechargePo(ab);
        Integer num = adminActivityRechargeService.updateByPrimaryKeySelective(arp);
        result.setData(num);
        return result;
    }


    /**
     * 查询列表
     *
     * @return
     */
    @RequestMapping("/query_list")
    public Result<List<AdminActivityRechargePo>> queryByCondition(@RequestParam AdminActivityRechargePo adminActivityRecharge) {
        Result<List<AdminActivityRechargePo>> result = new Result<>();
        List<AdminActivityRechargePo> list = adminActivityRechargeService.query(adminActivityRecharge);
        result.setData(list);
        return result;
    }

    public AdminActivityRechargePo convertQueryData(AdminActivityRechargeBo ab) {
        AdminActivityRechargePo arp = new AdminActivityRechargePo();
        arp.setAirDropCoin(ab.getRechargeCoin());
        arp.setAirDropRule(ab.getAirDropRule());
        return arp;
    }
}
