package com.qkwl.admin.layui.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.qkwl.admin.layui.component.CancelOrderComponent;
import com.qkwl.admin.layui.component.RabbitMqSendComponent;
import com.qkwl.common.dto.Enum.EntrustTypeEnum;
import com.qkwl.common.dto.Enum.UserStatusEnum;
import com.qkwl.common.dto.entrust.FEntrust;
import com.qkwl.common.dto.mq.MQEntrust;
import com.qkwl.common.dto.riskmanagement.SystemRiskManagementDto;
import com.qkwl.common.dto.user.FUser;
import com.qkwl.common.dto.wallet.UserCoinWallet;
import com.qkwl.common.rpc.admin.IAdminEntrustServer;
import com.qkwl.common.rpc.admin.IAdminSystemRiskManagementService;
import com.qkwl.common.rpc.admin.IAdminUserCapitalService;
import com.qkwl.common.rpc.admin.IAdminUserService;
import com.qkwl.common.util.ReturnResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @ProjectName: admin_layui
 * @Package: com.qkwl.admin.layui.controller
 * @ClassName: SystemRiskManagementController
 * @Author: hf
 * @Description:
 * @Date: 2019/8/19 11:44
 * @Version: 1.0
 */

@Controller
public class SystemRiskManagementController {
    @Autowired
    private IAdminSystemRiskManagementService riskManagementService;
    @Autowired
    private IAdminUserCapitalService adminUserCapitalService;
    @Autowired
    private IAdminUserService adminUserService;
    @Autowired
    private IAdminEntrustServer adminEntrustServer;
    @Autowired
    private RabbitMqSendComponent rabbitMqSendComponent;
    private static final Logger log = LoggerFactory.getLogger(SystemRiskManagementController.class);

    @Autowired
    private CancelOrderComponent cancelOrderComponent;

    /**
     * 通过ID删除
     *
     * @return
     */
    @RequestMapping("/admin/deleteRiskManagement")
    @ResponseBody
    public ReturnResult deleteByPrimaryKey(@RequestParam(value = "id") Integer id) {
        if (id == null) {
            return ReturnResult.FAILUER("id is required!");
        }
        SystemRiskManagementDto queryResult = riskManagementService.selectByPrimaryKey(id);
        if (queryResult == null) {
            return ReturnResult.FAILUER("系统错误！");
        }

        Integer num = riskManagementService.deleteByPrimaryKey(id);
        if (num != null && num > 0) {
            return ReturnResult.SUCCESS("删除风控配置成功！");
        } else {
            return ReturnResult.FAILUER("删除风控配置失败！");
        }
    }

    /**
     * 新增数据
     *
     * @return
     */
    @RequestMapping("admin/riskManagement/save")
    @ResponseBody
    public ReturnResult insert(SystemRiskManagementDto rmd) {
        try {
            Integer userId = rmd.getUserId();
            Integer coinId = setAndGetCoinId(rmd);
            UserCoinWallet userCoinWallet = adminUserCapitalService.selectUserVirtualWallet(userId, coinId);
            if (userCoinWallet == null) {
                return ReturnResult.FAILUER("用户uid不存在,请检测后再添加!");
            }
            Double walletTotal = Double.valueOf(userCoinWallet.getTotal().toPlainString());
            Double rechargeFunds = rmd.getRechargeFunds();
            if (walletTotal.compareTo(rechargeFunds) < 0) {
                return ReturnResult.FAILUER("账户可用资产不足,可用资产为:" + walletTotal);
            }
            if (rechargeFunds <= 0) {
                return ReturnResult.FAILUER("充值金额必须大于0");
            }
            FUser userInfo = adminUserService.selectById(userId);
            if (userInfo == null) {
                return ReturnResult.FAILUER("用户不存在！");
            }
            String telPhone = userInfo.getFtelephone();
            String email = userInfo.getFemail();
            if (StringUtils.isEmpty(telPhone)) {
                return ReturnResult.FAILUER("用户手机号未配置！");
            }
            if (!userInfo.getFhasrealvalidate()) {
                return ReturnResult.FAILUER("用户未实名");
            }
            rmd.setAccountRealFunds(walletTotal);
            rmd.setTelephone(telPhone);
            rmd.setEmail(StringUtils.isEmpty(email) ? "" : email);

            Integer num = riskManagementService.insertSelective(rmd);
            if (num != null && num > 0) {
                boolean b = setAndUpdateUserStatus(userInfo);
                if (b) {
                    return ReturnResult.SUCCESS("新增风控配置成功！");
                } else {
                    return ReturnResult.FAILUER("自动禁用提现,提币等功能失败,请手动禁用！");
                }
            } else {
                return ReturnResult.FAILUER("新增风控配置失败！");
            }
        } catch (Exception e) {
            log.error("add riskManagement fail->{}", e);
            return ReturnResult.FAILUER("新增风控配置失败");
        }

    }

    private boolean setAndUpdateUserStatus(FUser userInfo) {
        userInfo.setFiscoin(UserStatusEnum.FORBBIN_VALUE);
        userInfo.setFiscny(UserStatusEnum.FORBBIN_VALUE);
        userInfo.setOtcAction(false);
        return adminUserService.updateUserInfo(userInfo);
    }

    /**
     * 平仓
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/admin/riskManagement/closeout")
    public ReturnResult closeoutByUserId(Integer id) {
        log.error("into closeout ,id is->{}", id);
        SystemRiskManagementDto dto = riskManagementService.selectByPrimaryKey(id);
        if (dto == null) {
            return ReturnResult.FAILUER("用户id不存在");
        }
        log.error("query result is ->{}", JSON.toJSONString(dto));

        try {
            Integer coinId = dto.getCoinId();
            Integer userId = dto.getUserId();
            Double rechargeFunds = dto.getRechargeFunds();
            Double debitTimes = dto.getDebitTimes();
            Double debitAmount = rechargeFunds * debitTimes;
            String coinPair = dto.getRechargeCoin();
            Integer status = dto.getUserStatus();
            if (status.equals(3)) {
                return ReturnResult.SUCCESS("已平仓,不允许再次平仓!");
            }
            this.getAllOrdersAndCancel(userId, coinId);
            boolean b = rabbitMqSendComponent.sendCloseoutMQAction(userId, coinId, coinPair, debitAmount);
            if (b) {
                SystemRiskManagementDto data = new SystemRiskManagementDto(id, userId, 3);
                riskManagementService.updateByPrimaryKey(data);
                return ReturnResult.SUCCESS("平仓操作成功!");
            } else {
                log.error("send mq fail");
                return ReturnResult.FAILUER("平仓操作发送mq失败");
            }

        } catch (Exception e) {
            log.error("平仓操作发送mq失败:->{}", e);
            return ReturnResult.FAILUER(e.toString());
        }
    }

    /**
     * 转换mqEntrust字段
     *
     * @param po
     */
    private MQEntrust convert2MQEntrust(FEntrust po) {
        MQEntrust mqEntrust = new MQEntrust();
        mqEntrust.setCreatedate(po.getFcreatetime().getTime());
        mqEntrust.setFentrustid(po.getFid().toString());
        mqEntrust.setFreefee(false);
        mqEntrust.setFsource(po.getFsource());
        mqEntrust.setFstatus(po.getFstatus());
        mqEntrust.setFtradeid(po.getFtradeid());
        mqEntrust.setFuid(po.getFuid());
        mqEntrust.setLeftcount(po.getFleftcount().toString());
        BigDecimal Prize = po.getFprize().setScale(10);
        mqEntrust.setPrize(Prize.toString());
        mqEntrust.setRecover(0);
        mqEntrust.setSize(po.getFcount().toString());
        int ftype = EntrustTypeEnum.CANCEL.getCode();
        mqEntrust.setFtype(ftype);
        return mqEntrust;
    }

    /**
     * get all orders and cancel them
     *
     * @param userId
     */
    private void getAllOrdersAndCancel(Integer userId, Integer coinId) {
        try {
            log.info("getAllOrdersAndCancel userId->{}, conId is ->{}", userId, coinId);
            FEntrust fEntrust = new FEntrust();
            fEntrust.setFuid(userId);
            List<FEntrust> historyList = adminEntrustServer.query(fEntrust);
            if (CollectionUtils.isEmpty(historyList)) {
                log.error("query all order result is null");
                return;
            }
            log.info("get all order result->{},and conId is ->{}", JSON.toJSONString(historyList), coinId);
            for (FEntrust his : historyList) {
              Integer status = his.getFstatus();
              if (status.equals(1) || status.equals(2)) {
                MQEntrust mqEntrust = convert2MQEntrust(his);
                cancelOrderComponent.cancleEntrust(mqEntrust);
              }
            }
        } catch (Exception e) {
            log.error("cancel entrust fail->{} ", e);
        }
    }

    /**
     * 查询列表
     *
     * @return
     */
    @RequestMapping("/query_list/riskManagement")
    public ReturnResult queryByRiskParam(SystemRiskManagementDto riskManagementDto) {
        List<SystemRiskManagementDto> list = riskManagementService.query(riskManagementDto);
        return ReturnResult.SUCCESS(list);
    }


    /* --------------视图------------------*/

    /**
     * 分页查询数据
     */
    @RequestMapping("/admin/riskManagementList")
    public ModelAndView selectPaged(@RequestParam(value = "pageNum", defaultValue = "1") int currentPage,
                                    @RequestParam(value = "pageSize", defaultValue = "40") int pageSize,
                                    @RequestParam(required = false, defaultValue = "") String rechargeCoin,
                                    @RequestParam(required = false, defaultValue = "") Integer userId) {

        SystemRiskManagementDto dto = new SystemRiskManagementDto();
        if (userId != null) {
            dto.setUserId(userId);
        }
        if (!StringUtils.isEmpty(rechargeCoin)) {
            dto.setRechargeCoin(rechargeCoin);
        }
        PageInfo<SystemRiskManagementDto> page = riskManagementService.selectPaged(dto, currentPage, pageSize);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("riskmanagement/riskManagementList");
        modelAndView.addObject("currentPage", page.getPageNum());
        modelAndView.addObject("pages", page.getPages());
        modelAndView.addObject("riskManagementList", page.getList());
        return modelAndView;
    }

    /**
     * 新增风控配置信息(视图)
     */
    @RequestMapping("/admin/addRiskManagement")
    public ModelAndView addTradeAirdrop() throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("riskmanagement/addRiskManagement");
        return modelAndView;
    }

    /**
     * ------------------------util-------------------------------
     */
    private void setOtherInfo(SystemRiskManagementDto rmd) {
        Date date = new Date();
        rmd.setRechargeTime(date);
        rmd.setCreateTime(date);
        rmd.setUpdateStatusTime(date);
        rmd.setUserStatus(1);
        rmd.setNoticeChannel(2);
    }

    private Integer setAndGetCoinId(SystemRiskManagementDto rmd) {
        setOtherInfo(rmd);
        if (rmd.getRechargeCoin().equals("USDT")) {
            rmd.setCoinId(52);
            return 52;
        } else {
            rmd.setCoinId(9);
            return 9;
        }
    }

}
