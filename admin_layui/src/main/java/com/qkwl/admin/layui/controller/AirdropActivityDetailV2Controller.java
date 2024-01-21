package com.qkwl.admin.layui.controller;

import com.alibaba.fastjson.JSON;
import com.qkwl.common.dto.activity_v2.bo.AirdropActivityDetailV2Bo;
import com.qkwl.common.dto.activity_v2.po.AirdropActivityDetailV2Po;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.rpc.admin.activity_v2.AirdropActivityDetailV2Service;
import com.qkwl.common.util.ReturnResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;


/**
 * @author hf
 * @date 2019-06-13 02:29:35
 */
@RestController
@Slf4j
public class AirdropActivityDetailV2Controller {
    @Autowired
    private AirdropActivityDetailV2Service airdropActivityDetailV2Service;

    /**
     * 通过id查询
     *
     * @return
     */
    @RequestMapping("/admin/select/detail/byId")
    public ReturnResult selectByPrimaryKey(Integer id) {
        AirdropActivityDetailV2Po po = airdropActivityDetailV2Service.selectByPrimaryKey(id);
        return ReturnResult.SUCCESS(po);
    }


    /**
     * 通过ID删除
     *
     * @return
     */
    @RequestMapping("/delete_by_id")
    public ReturnResult deleteByPrimaryKey(Integer id) {
        Integer num = airdropActivityDetailV2Service.deleteByPrimaryKey(id);
        return ReturnResult.SUCCESS(num);
    }


    /**
     * 新增数据
     *
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ReturnResult insert(@RequestBody AirdropActivityDetailV2Po airdropActivityDetailV2) {
        Integer num = airdropActivityDetailV2Service.insertSelective(airdropActivityDetailV2);
        return ReturnResult.SUCCESS(num);
    }


    /**
     * 修改数据
     *
     * @return
     */
    @RequestMapping("/update")
    public ReturnResult updateByPrimaryKeySelective(AirdropActivityDetailV2Po airdropActivityDetailV2) {
        Integer num = airdropActivityDetailV2Service.updateByPrimaryKeySelective(airdropActivityDetailV2);
        return ReturnResult.SUCCESS(num);
    }


    /**
     * 查询列表
     *
     * @return
     */
    @RequestMapping("/query_list/activityDetail")
    public ReturnResult queryByCondition(AirdropActivityDetailV2Po airdropActivityDetailV2) {
        List<AirdropActivityDetailV2Po> list = airdropActivityDetailV2Service.query(airdropActivityDetailV2);
        return ReturnResult.SUCCESS(list);
    }

    /* --------------------------视图---------------------------*/

    /**
     * 分页查询数据
     */
    @RequestMapping("/admin/activityDetailList")
    public ModelAndView selectPaged(@RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
                                    @RequestParam(required = false, defaultValue = "40") int pageSize,
                                    @RequestParam(value = "type", required = false, defaultValue = "1") Integer type,
                                    @RequestParam(value = "airDropTime", required = false, defaultValue = "0") long airDropTime,
                                    @RequestParam(value = "airDropType", defaultValue = "") String airDropType,
                                    @RequestParam(value = "userId", defaultValue = "-1") Integer userId,
                                    @RequestParam(value = "activityCoin", defaultValue = "") String activityCoin,
                                    @RequestParam(value = "airDropRule", required = false, defaultValue = "") String airDropRule) {
        log.info("get activityDetailList data,airDropTime:[{}],currentPage is [{}],pageSize is[{}],type is[{}],airDropType is:[{}] activityCoin is->[{}],airDropRule is->[{}]",
                airDropTime, currentPage, pageSize, type, airDropType, activityCoin, airDropRule);

        AirdropActivityDetailV2Bo activityDetailV2Bo = new AirdropActivityDetailV2Bo(type, activityCoin);
        activityDetailV2Bo.setAirDropTime(new Date(airDropTime));
        activityDetailV2Bo.setCurrentPage(currentPage);
        activityDetailV2Bo.setPageSize(pageSize);
        activityDetailV2Bo.setOffSet((currentPage - 1) * pageSize);
        if (!type.equals(1)) {
            activityDetailV2Bo.setTypeDetail(airDropType);
            activityDetailV2Bo.setRule(Double.valueOf(airDropRule));
        }
        if (userId >= 0) {
            activityDetailV2Bo.setUserId(userId);
        }
        Pagination<AirdropActivityDetailV2Po>  page = airdropActivityDetailV2Service.selectPaged(activityDetailV2Bo);
        log.info("get activityDetailList query param :[{}],result data is->[{}]", JSON.toJSONString(activityDetailV2Bo), JSON.toJSONString(page));
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("activity_v2/activityDetailList");
        modelAndView.addObject("currentPage", currentPage);
        modelAndView.addObject("pages", pageSize);
        modelAndView.addObject("activityDetailList", page.getData());
        return modelAndView;
    }

}
