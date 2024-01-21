package com.qkwl.admin.layui.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @ProjectName: hotcoin-risk-management
 * @Package: hotcoin.model.po
 * @ClassName: CloseoutDto
 * @Author: hf
 * @Description:
 * @Date: 2019/8/21 11:28
 * @Version: 1.0
 */
@Slf4j
@Getter
@Setter
public class CloseoutDto implements Serializable {
    /** 支付流水ID（金融平台订单号）*/
    private String paymentFlowId;

    /** 业务订单号 */
    private String bizOrderId;

    /** 支付者账号 */
    private String payAccountNo;

    /** 支付币种ID */
    private String payCoinId;

    /** 支付币种名稱 */
    private String payCoinName;

    /** 支付币种数量*/
    private String payCoinAmt;

    /**
     * 来源系统编号 801风控系统
     */
    private Integer sysCode;

    /**收款人账号*/
    private String rcvAccountNo;

    /** 收款人币种ID */
    private String recvCoinId;
}
