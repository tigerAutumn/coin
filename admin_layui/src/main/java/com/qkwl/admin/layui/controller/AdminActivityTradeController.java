package com.qkwl.admin.layui.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageInfo;
import com.qkwl.admin.layui.base.WebBaseController;
import com.qkwl.admin.layui.component.CoinInfoComponent;
import com.qkwl.common.dto.activity_v2.bo.AdminActivityTradeBo;
import com.qkwl.common.dto.activity_v2.po.AdminActivityTradePo;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.admin.activity_v2.AdminActivityTradeService;
import com.qkwl.common.util.ReturnResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author hf
 * @date 2019-06-10 09:20:44
 */
@Controller
@Slf4j
public class AdminActivityTradeController extends WebBaseController {
    @Autowired
    private AdminActivityTradeService adminActivityTradeService;
    @Autowired
    private RedisHelper redisHelper;
    @Autowired
    private CoinInfoComponent coinInfoComponent;

    /**
     * 通过id查询
     *
     * @return
     */
    @RequestMapping("/select/tradeActivity/byId")
    public ReturnResult selectByPrimaryKey(Integer id) {
        AdminActivityTradePo po = adminActivityTradeService.selectByPrimaryKey(id);
        return ReturnResult.SUCCESS(po);
    }

    /**
     * 通过ID删除
     *
     * @return
     */
    @RequestMapping("/admin/deleteTradeActivity")
    @ResponseBody
    public ReturnResult deleteByPrimaryKey(@RequestParam(value = "id") Integer id) {
        if (id == null) {
            return ReturnResult.FAILUER("id is required!");
        }
        Integer num = adminActivityTradeService.deleteByPrimaryKey(id);
        if (num != null && num > 0) {
            return ReturnResult.SUCCESS("删除交易活动成功！");
        } else {
            return ReturnResult.FAILUER("删除交易活动失败！");
        }
    }

    /**
     * 新增数据
     *
     * @return
     */
    @RequestMapping("admin/tradeActivity/save")
    @ResponseBody
    public ReturnResult insert(AdminActivityTradeBo at) {
        try {
            log.error("get save trade data->{}", JSON.toJSONString(at));
            List<String> conPairsList = coinInfoComponent.getCoinPairsList();
            JSONArray jsonArray = new JSONArray(16);
            for (String coinPair : conPairsList) {
                String matchCoinPair = sessionContextUtils.getContextRequest().getParameter("coinPairs[" + coinPair + "]");
                log.error("save opt get coinPairs  ->{}, from checkbox", coinPair);
                if (!StringUtils.isEmpty(matchCoinPair)) {
                    log.error("save opt match success get coinPairs  ->{}, from checkbox", matchCoinPair);
                    jsonArray.add(coinPair);
                }
            }

            at.setLinkCoinPairs(jsonArray.toJSONString());
//            List<AdminActivityTradePo> queryData = adminActivityTradeService.query(convertQueryData(at));
//            if (queryData != null && !queryData.isEmpty()) {
//                return ReturnResult.FAILUER("数据已存在!");
//            }
            AdminActivityTradePo po = new AdminActivityTradePo(at, new Date());
            Integer num = adminActivityTradeService.insertSelective(po);

            if (num != null && num > 0) {
                return ReturnResult.SUCCESS("新增交易活动成功！");
            } else {
                return ReturnResult.FAILUER("新增交易活动失败！");
            }
        } catch (Exception e) {
            log.error("add tradeActivity fail->{}", e);
            return ReturnResult.FAILUER("新增交易活动失败！");
        }

    }

    /**
     * 修改数据
     *
     * @return
     */
    @RequestMapping("admin/tradeActivity/update")
    @ResponseBody
    public ReturnResult updateByPrimaryKeySelective(AdminActivityTradeBo at) {
        log.error("get update tradeActivity data->{}", JSON.toJSONString(at));
        if (at.getId() == null) {
            return ReturnResult.FAILUER("id is required!");
        }
        List<String> conPairsList = coinInfoComponent.getCoinPairsList();
        JSONArray jsonArray = new JSONArray(16);
        for (String coinPair : conPairsList) {
            String matchCoinPair = sessionContextUtils.getContextRequest().getParameter("coinPairs[" + coinPair + "]");
            log.error("tradeActivity update opt get coinPairs  ->{}, from checkbox", coinPair);
            if (!StringUtils.isEmpty(matchCoinPair)) {
                log.error(" update opt match success get coinPairs  ->{}, from checkbox", matchCoinPair);
                jsonArray.add(coinPair);
            }
        }
        at.setLinkCoinPairs(jsonArray.toJSONString());
        AdminActivityTradePo po = new AdminActivityTradePo(at);
        Integer num = adminActivityTradeService.updateByPrimaryKeySelective(po);
        if (num != null && num > 0) {
            return ReturnResult.SUCCESS("修改交易活动成功！");
        } else {
            return ReturnResult.FAILUER("修改交易活动失败！");
        }
    }

    /**
     * 禁用活动
     *
     * @return
     */
    @RequestMapping("/admin/closeTradeActivity")
    @ResponseBody
    public ReturnResult closeTradeActivityByPrimaryKey(@RequestParam(value = "id") Integer id) {
        if (id == null) {
            return ReturnResult.FAILUER("id is required!");
        }
        AdminActivityTradePo po = new AdminActivityTradePo();
        po.setStatus(-1);
        po.setId(id);
        Integer num = adminActivityTradeService.updateByPrimaryKeySelective(po);
        if (num != null && num > 0) {
            return ReturnResult.SUCCESS("禁用交易活动成功！");
        } else {
            return ReturnResult.FAILUER("禁用交易活动失败！");
        }
    }

    /**
     * 开启活动
     *
     * @return
     */
    @RequestMapping("/admin/openTradeActivity")
    @ResponseBody
    public ReturnResult openTradeActivityByPrimaryKey(@RequestParam(value = "id") Integer id) {
        if (id == null) {
            return ReturnResult.FAILUER("id is required!");
        }
        AdminActivityTradePo po = new AdminActivityTradePo();
        po.setStatus(1);
        po.setId(id);
        Integer num = adminActivityTradeService.updateByPrimaryKeySelective(po);
        if (num != null && num > 0) {
            return ReturnResult.SUCCESS("开启交易活动成功！");
        } else {
            return ReturnResult.FAILUER("开启交易活动失败！");
        }
    }


    /**
     * 查询列表
     *
     * @return
     */
    @RequestMapping("/query_list/tradeActivity")
    public ReturnResult queryByCondition(AdminActivityTradePo adminActivityTrade) {
        List<AdminActivityTradePo> list = adminActivityTradeService.query(adminActivityTrade);
        return ReturnResult.SUCCESS(list);
    }

    public AdminActivityTradePo convertQueryData(AdminActivityTradeBo ab) {
        AdminActivityTradePo arp = new AdminActivityTradePo();
        arp.setAirDropCoin(ab.getTradeCoin());
        arp.setAirDropRule(ab.getAirDropRule());
        return arp;
    }

    /* --------------视图------------------*/

    /**
     * 分页查询数据
     */
    @RequestMapping("/admin/tradeActivityList")
    public ModelAndView selectPaged(@RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
                                    @RequestParam(required = false, defaultValue = "40") int pageSize) {

        PageInfo<AdminActivityTradePo> page = adminActivityTradeService.selectPaged(currentPage, pageSize);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("activity_v2/tradeActivityList");
        modelAndView.addObject("currentPage", page.getPageNum());
        modelAndView.addObject("pages", page.getPages());
        modelAndView.addObject("tradeActivityList", page.getList());
        return modelAndView;
    }

    /**
     * 新增交易空投活动信息(视图)
     */
    @RequestMapping("/admin/addTradeActivity")
    public ModelAndView addTradeAirdrop() throws Exception {
        List<SystemCoinType> coinsList = redisHelper.getCoinTypeList();
        List<String> conPairsList = coinInfoComponent.getCoinPairsList();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("activity_v2/addTradeActivity");
        modelAndView.addObject("coinsList", coinsList);
        modelAndView.addObject("conPairsList", conPairsList);
        return modelAndView;
    }

    /**
     * 读取修改的活动信息(视图)
     *
     * @return
     */
    @RequestMapping("/admin/updateTradeActivity")
    public ModelAndView changeActivity(@RequestParam(value = "id", required = false) Integer id) {
        AdminActivityTradePo activity = adminActivityTradeService.selectByPrimaryKey(id);
        List<SystemCoinType> coinsList = redisHelper.getCoinTypeList();
        List<String> conPairsList = coinInfoComponent.getCoinPairsList();
        JSONArray coinPairsArr = JSON.parseArray(activity.getLinkCoinPairs());
        log.error("coinPairsArr parse result->{}", coinPairsArr);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("activity_v2/updateTradeActivity");
        modelAndView.addObject("content", activity);
        modelAndView.addObject("coinsList", coinsList);
        modelAndView.addObject("conPairsList", conPairsList);
        modelAndView.addObject("coinPairs", coinPairsArr);
        return modelAndView;
    }
}
