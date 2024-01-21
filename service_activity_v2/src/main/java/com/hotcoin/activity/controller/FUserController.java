package com.hotcoin.activity.controller;

import com.hotcoin.activity.model.Result;
import com.hotcoin.activity.model.po.FUserPo;
import com.hotcoin.activity.service.FUserService;
import com.hotcoin.activity.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ProjectName: service_activity_v2
 * @Package: com.hotcoin.activity.controller
 * @ClassName: FUserController
 * @Author: hf
 * @Description:
 * @Date: 2019/6/12 13:41
 * @Version: 1.0
 */
@RestController
public class FUserController {
    @Autowired
    private FUserService fUserService;

    @RequestMapping("/select_paged")
    public Result selectPaged() {
        String startTime = "2018-03-11 12:30:40";
        String endTime = "2019-06-11 12:30:40";
        Result pageResult = new Result<>();
        List<FUserPo> fUserPoList = fUserService.getUserListByParam(DateUtil.strToDate(startTime), DateUtil.strToDate(endTime));
        pageResult.setData(fUserPoList);
        return pageResult;
    }
}
