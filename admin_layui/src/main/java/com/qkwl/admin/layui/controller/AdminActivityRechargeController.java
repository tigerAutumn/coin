package com.qkwl.admin.layui.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.activity_v2.bo.AdminActivityRechargeBo;
import com.qkwl.common.dto.activity_v2.po.AdminActivityRechargePo;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.admin.activity_v2.AdminActivityRechargeService;
import com.qkwl.common.util.ReturnResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;


/**
 * @author hf
 * @date 2019-06-10 09:20:42
 * 充值空投控制器
 */
@Controller
@Slf4j
public class AdminActivityRechargeController {
    @Autowired
    private AdminActivityRechargeService adminActivityRechargeService;
    @Autowired
    private RedisHelper redisHelper;

    /**
     * 通过id查询
     *
     * @return
     */
    @RequestMapping("/select/recharge/byId")
    public ReturnResult selectByPrimaryKey(@RequestParam Integer id) {
        AdminActivityRechargePo po = adminActivityRechargeService.selectByPrimaryKey(id);
        return ReturnResult.SUCCESS(po);
    }

    /**
     * 通过ID删除
     *
     * @return
     */
    @RequestMapping("/admin/deleteRechargeActivity")
    @ResponseBody
    public ReturnResult deleteByPrimaryKey(@RequestParam(value = "id") Integer id) {
        if (id == null) {
            return ReturnResult.FAILUER("id is required!");
        }
        Integer num = adminActivityRechargeService.deleteByPrimaryKey(id);
        if (num != null && num > 0) {
            return ReturnResult.SUCCESS("删除充值活动成功");
        } else {
            return ReturnResult.FAILUER("删除充值活动失败");
        }
    }

    /**
     * 新增数据
     *
     * @return
     */
    @RequestMapping(value = "admin/rechargeActivity/save")
    @ResponseBody
    public ReturnResult insert(AdminActivityRechargeBo ab) {
        log.error("get need save data->{}", JSON.toJSONString(ab));
//        List<AdminActivityRechargePo> queryData = adminActivityRechargeService.query(convertQueryData(ab));
//        if (queryData != null && !queryData.isEmpty()) {
//            return ReturnResult.FAILUER("data is exist!");
//        }
        AdminActivityRechargePo arp = new AdminActivityRechargePo(ab, new Date());
        Integer num = adminActivityRechargeService.insertSelective(arp);
        if (num != null && num > 0) {
            return ReturnResult.SUCCESS("新增充值活动成功");
        } else {
            return ReturnResult.FAILUER("新增充值活动失败");
        }
    }

    /**
     * 修改数据
     *
     * @return
     */
    @RequestMapping("admin/rechargeActivity/update")
    @ResponseBody
    public ReturnResult updateByPrimaryKeySelective(AdminActivityRechargeBo ab) {
        if (ab.getId() == null) {
            return ReturnResult.FAILUER("id is required!");
        }
        AdminActivityRechargePo arp = new AdminActivityRechargePo(ab);
        Integer num = adminActivityRechargeService.updateByPrimaryKeySelective(arp);
        if (num != null && num > 0) {
            return ReturnResult.SUCCESS("修改充值活动成功");
        } else {
            return ReturnResult.FAILUER("修改充值活动失败");
        }
    }

    /**
     * 禁用活动
     *
     * @return
     */
    @RequestMapping("/admin/closeRechargeActivity")
    @ResponseBody
    public ReturnResult closeRechargeActivityByPrimaryKey(@RequestParam(value = "id") Integer id) {
        if (id == null) {
            return ReturnResult.FAILUER("id is required!");
        }
        AdminActivityRechargePo po = new AdminActivityRechargePo();
        po.setStatus(-1);
        po.setId(id);
        Integer num = adminActivityRechargeService.updateByPrimaryKeySelective(po);
        if (num != null && num > 0) {
            return ReturnResult.SUCCESS("禁用充值活动成功");
        } else {
            return ReturnResult.FAILUER("禁用充值活动失败");
        }
    }

    /**
     * 开启活动
     *
     * @return
     */
    @RequestMapping("/admin/openRechargeActivity")
    @ResponseBody
    public ReturnResult openRechargeActivityByPrimaryKey(@RequestParam(value = "id") Integer id) {
        if (id == null) {
            return ReturnResult.FAILUER("id is required!");
        }
        AdminActivityRechargePo po = new AdminActivityRechargePo();
        po.setStatus(1);
        po.setId(id);
        Integer num = adminActivityRechargeService.updateByPrimaryKeySelective(po);
        if (num != null && num > 0) {
            return ReturnResult.SUCCESS("开启充值活动成功");
        } else {
            return ReturnResult.FAILUER("开启充值活动失败");
        }
    }

    /**
     * 查询列表
     *
     * @return
     */
    @RequestMapping("/query_list/recharge")
    public ReturnResult queryByCondition(@RequestParam AdminActivityRechargePo adminActivityRecharge) {
        List<AdminActivityRechargePo> list = adminActivityRechargeService.query(adminActivityRecharge);
        return ReturnResult.SUCCESS(list);
    }

    public AdminActivityRechargePo convertQueryData(AdminActivityRechargeBo ab) {
        AdminActivityRechargePo arp = new AdminActivityRechargePo();
        arp.setAirDropCoin(ab.getRechargeCoin());
        arp.setAirDropRule(ab.getAirDropRule());
        return arp;
    }
    /* ---------------视图-------------------*/

    /**
     * 分页查询数据(视图)
     */
    @RequestMapping("admin/rechargeActivityList")
    public ModelAndView selectPaged(@RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
                                    @RequestParam(required = false, defaultValue = "40") int pageSize) {
        PageInfo<AdminActivityRechargePo> page = adminActivityRechargeService.selectPaged(currentPage, pageSize);
        log.error("get pageInfo data,currentPage is {},pageSize is{},->{}", currentPage, pageSize, JSON.toJSONString(page));
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("activity_v2/rechargeActivityList");
        modelAndView.addObject("currentPage", page.getPageNum());
        modelAndView.addObject("pages", page.getPages());
        modelAndView.addObject("rechargeActivityList", page.getList());
        return modelAndView;
    }

    /**
     * 新增充值空投活动信息(视图)
     */
    @RequestMapping("/admin/addRechargeActivity")
    public ModelAndView addRechargeAirdrop() throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("activity_v2/addRechargeActivity");
        List<SystemCoinType> coinsList = redisHelper.getCoinTypeList();
        modelAndView.addObject("coinsList", coinsList);
        return modelAndView;
    }

    /**
     * 读取修改的活动信息(视图)
     *
     * @return
     */
    @RequestMapping("/admin/updateRechargeActivity")
    public ModelAndView changeActivity(@RequestParam(value = "id", required = false) Integer id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("activity_v2/updateRechargeActivity");
        AdminActivityRechargePo activity = adminActivityRechargeService.selectByPrimaryKey(id);
        List<SystemCoinType> coinsList = redisHelper.getCoinTypeList();
        modelAndView.addObject("coinsList", coinsList);
        modelAndView.addObject("content", activity);
        return modelAndView;
    }

}
