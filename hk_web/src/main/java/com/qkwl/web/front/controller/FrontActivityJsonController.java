package com.qkwl.web.front.controller;


import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.qkwl.common.dto.capital.FRewardCodeDTO;
import com.qkwl.common.dto.coin.SystemCoinType;
import com.qkwl.common.dto.common.Pagination;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.framework.redis.RedisHelper;
import com.qkwl.common.result.Result;
import com.qkwl.common.rpc.capital.IUserRewardCodeService;
import com.qkwl.common.rpc.user.IUserService;
import com.qkwl.common.util.HttpRequestUtils;
import com.qkwl.common.util.I18NUtils;
import com.qkwl.common.util.ReturnResult;
import com.qkwl.web.front.controller.base.JsonBaseController;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class FrontActivityJsonController extends JsonBaseController {

    private static Logger logger = LoggerFactory.getLogger(FrontActivityJsonController.class);

    @Autowired
    private IUserRewardCodeService rewardCodeService;
    @Autowired
    private IUserService userService;
    @Autowired
    private RedisHelper redisHelper;

    /**
     * 使用激活码
     *
     * @param code
     * @return
     * @throws Exception
     */
    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/activity/exchange",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult codeExchange(@RequestParam(required = true, defaultValue = "") String code) throws Exception {
        // 传送消息
        if (code == "" || code.length() != 16) { 
            return ReturnResult.FAILUER(I18NUtils.getString("com.activity.error.10005")); 
        }
        FUser fUser = super.getCurrentUserInfoByToken();
        fUser = userService.selectUserById(fUser.getFid());
        String ip = HttpRequestUtils.getIPAddress();
        Result flag = null;
            flag = rewardCodeService.UseRewardCode(fUser.getFid(), code, ip);
            if (flag.getSuccess()) {
                return ReturnResult.SUCCESS(flag.getMsg());
            } else {
                return ReturnResult.FAILUER(flag.getMsg());
            }
    }

    @ResponseBody
    @ApiOperation("")
	@RequestMapping(value="/activity/index_json",method = {RequestMethod.GET,RequestMethod.POST})
    public ReturnResult activityGo(@RequestParam(required = false, defaultValue = "1") Integer currentPage) throws Exception {
        int pagesize = 5;
        FUser fuser = super.getCurrentUserInfoByToken();
        fuser = userService.selectUserById(fuser.getFid());

        Pagination<FRewardCodeDTO> page = new Pagination<FRewardCodeDTO>(currentPage, pagesize, "/activity/index_json?"); 
        FRewardCodeDTO code = new FRewardCodeDTO();
        code.setFuid(fuser.getFid());
        code.setFstate(true);
        page = rewardCodeService.listRewardeCode(page, code);
        System.out.println(page);
        Collection<FRewardCodeDTO> list = page.getData();
        if (list != null) {
            for (FRewardCodeDTO fRewardCode : list) {
                SystemCoinType coinType = redisHelper.getCoinType(fRewardCode.getFtype());
                fRewardCode.setFtype_s(coinType.getShortName());
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pagin", page.getPagin()); 
        jsonObject.put("frewardcodes", list); 
        return ReturnResult.SUCCESS(jsonObject);
    }
}
