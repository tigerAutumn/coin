package com.qkwl.admin.layui.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.qkwl.common.dto.activity_v2.bo.AdminActivityRegisterBo;
import com.qkwl.common.dto.activity_v2.po.AdminActivityRegisterPo;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.rpc.admin.activity_v2.AdminActivityRegisterService;
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
 * @date 2019-06-10 09:20:43
 */
@Controller
@Slf4j
public class AdminActivityRegisterController {
    @Autowired
    private AdminActivityRegisterService adminActivityRegisterService;
    @Autowired
    private RedisHelper redisHelper;

    /**
     * 通过id查询
     *
     * @return
     */
    @RequestMapping("/admin/select/register/byId")
    public ReturnResult selectByPrimaryKey(Integer id) {
        AdminActivityRegisterPo po = adminActivityRegisterService.selectByPrimaryKey(id);
        return ReturnResult.SUCCESS(po);
    }

    /**
     * 通过ID删除
     *
     * @return
     */
    @RequestMapping("/admin/deleteRegisterActivity")
    @ResponseBody
    public ReturnResult deleteByPrimaryKey(@RequestParam(value = "id") Integer id) {
        if (id == null) {
            return ReturnResult.FAILUER("id is required!");
        }
        Integer num = adminActivityRegisterService.deleteByPrimaryKey(id);
        if (num != null && num > 0) {
            return ReturnResult.SUCCESS("删除注册活动成功！");
        } else {
            return ReturnResult.FAILUER("删除注册活动失败！");
        }
    }

    /**
     * 新增数据
     *
     * @return
     */
    @RequestMapping("admin/registerActivity/save")
    @ResponseBody
    public ReturnResult insert(AdminActivityRegisterBo ab) {
//        List<AdminActivityRegisterPo> queryData = adminActivityRegisterService.query(convertQueryData(ab));
//        if (queryData != null && !queryData.isEmpty()) {
//            return ReturnResult.FAILUER("data is exist!");
//        }
        AdminActivityRegisterPo po = new AdminActivityRegisterPo(ab, new Date());
        Integer num = adminActivityRegisterService.insertSelective(po);
        log.error("get num is->{}", num);
        if (num != null && num > 0) {
            return ReturnResult.SUCCESS("新增注册活动成功");
        } else {
            return ReturnResult.FAILUER("新增注册活动失败");
        }
    }

    /**
     * 修改数据
     *
     * @return
     */
    @RequestMapping("admin/registerActivity/update")
    @ResponseBody
    public ReturnResult updateByPrimaryKeySelective(AdminActivityRegisterBo ar) {
        log.error("update data is->{}", JSON.toJSONString(ar));
        if (ar.getId() == null) {
            return ReturnResult.FAILUER("id is required!");
        }
        try { AdminActivityRegisterPo po = new AdminActivityRegisterPo(ar);
            Integer num = adminActivityRegisterService.updateByPrimaryKeySelective(po);
            log.error("update register data result->{}", num);
            if (num != null && num > 0) {
                return ReturnResult.SUCCESS("修改注册活动成功");
            } else {
                return ReturnResult.FAILUER("修改注册活动失败");
            }
        } catch (Exception e) {
            log.error("update data fail ->{}", e);
            return ReturnResult.FAILUER("修改注册活动失败");
        }

    }

    /**
     * 禁用活动
     *
     * @return
     */
    @RequestMapping("/admin/closeRegisterActivity")
    @ResponseBody
    public ReturnResult closeRegisterActivityByPrimaryKey(@RequestParam(value = "id") Integer id) {
        if (id == null) {
            return ReturnResult.FAILUER("id is required!");
        }
        AdminActivityRegisterPo po = new AdminActivityRegisterPo();
        po.setStatus(-1);
        po.setId(id);
        Integer num = adminActivityRegisterService.updateByPrimaryKeySelective(po);
        if (num != null && num > 0) {
            return ReturnResult.SUCCESS("禁用注册活动成功");
        } else {
            return ReturnResult.FAILUER("禁用注册活动失败");
        }
    }

    /**
     * 开启活动
     *
     * @return
     */
    @RequestMapping("/admin/openRegisterActivity")
    @ResponseBody
    public ReturnResult openRegisterActivityByPrimaryKey(@RequestParam(value = "id") Integer id) {
        log.error("open activity id->{}", id);
        if (id == null) {
            return ReturnResult.FAILUER("id is required!");
        }
        try {
            AdminActivityRegisterPo po = new AdminActivityRegisterPo();
            po.setStatus(1);
            po.setId(id);
            Integer num = adminActivityRegisterService.updateByPrimaryKeySelective(po);
            log.error("open register update success,num is ->{}", num);
            if (num != null && num > 0) {
                return ReturnResult.SUCCESS("启用注册活动成功");
            } else {
                return ReturnResult.FAILUER("启用注册活动失败");
            }
        } catch (Exception e) {
            log.error("open register fail ->{}", e);
            return ReturnResult.FAILUER("启用注册活动失败！");
        }
    }

    /**
     * 查询列表
     *
     * @return
     */
    @RequestMapping("/query_list/registerActivity")
    public ReturnResult queryByCondition(AdminActivityRegisterPo adminActivityRegister) {
        List<AdminActivityRegisterPo> list = adminActivityRegisterService.query(adminActivityRegister);
        return ReturnResult.SUCCESS(list);
    }

    public AdminActivityRegisterPo convertQueryData(AdminActivityRegisterBo ab) {
        AdminActivityRegisterPo arp = new AdminActivityRegisterPo();
        arp.setAirDropCoin(ab.getAirDropCoin());
        return arp;
    }
    /* ---------------------------视图----------------------------------------*/

    /**
     * 分页查询数据(视图)
     */
    @RequestMapping("admin/registerActivityList")
    public ModelAndView selectPaged(@RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
                                    @RequestParam(required = false, defaultValue = "40") int pageSize) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            PageInfo<AdminActivityRegisterPo> page = adminActivityRegisterService.selectPaged(currentPage, pageSize);
            modelAndView.setViewName("activity_v2/registerActivityList");
            modelAndView.addObject("currentPage", page.getPageNum());
            modelAndView.addObject("pages", page.getPages());
            modelAndView.addObject("registerActivityList", page.getList());
        } catch (Exception e) {
            log.error("get register list fail->{}", e);
        }

        return modelAndView;
    }

    /**
     * 新增注册空投活动信息(视图)
     */
    @RequestMapping("/admin/addRegisterActivity")
    public ModelAndView addRegisterAirdrop() throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("activity_v2/addRegisterActivity");
        //查询所有币种
        List<SystemCoinType> coinsList = redisHelper.getCoinTypeList();
        log.error("get all coinType info ->{}", JSON.toJSONString(coinsList));
        modelAndView.addObject("coinsList", coinsList);
        return modelAndView;
    }

    /**
     * 读取修改的活动信息(视图)
     *
     * @return
     */
    @RequestMapping("/admin/updateRegisterActivity")
    public ModelAndView changeActivity(@RequestParam(value = "id", required = false) Integer id) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("activity_v2/updateRegisterActivity");
        AdminActivityRegisterPo activity = adminActivityRegisterService.selectByPrimaryKey(id);
        List<SystemCoinType> coinsList = redisHelper.getCoinTypeList();
        modelAndView.addObject("content", activity);
        modelAndView.addObject("coinsList", coinsList);
        return modelAndView;
    }
}
