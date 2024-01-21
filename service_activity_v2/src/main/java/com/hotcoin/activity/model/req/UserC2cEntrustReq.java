package com.hotcoin.activity.model.req;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author hf
 * @date 2019-06-28 05:57:15
 * @since jdk1.8
 */
@Setter
@Getter
public class UserC2cEntrustReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ??&mu;￥id
     */
    private Integer id;
    /**
     * &plusmn;&ograve;??ID
     */
    private Integer coinId;
    /**
     * &oacute;??&sect;&deg;&oacute;?&uml;&ograve;?DD?&uml;id
     */
    private Integer bankId;
    /**
     * &eacute;&igrave;?&sect;id
     */
    private Integer businessId;
    /**
     * &oacute;??&sect;id
     */
    private Integer userId;
    /**
     * &prime;&prime;?&uml;&ecirc;&plusmn;??
     */
    private Date createTime;
    /**
     * &ecirc;y&aacute;?
     */
    private BigDecimal amount;
    /**
     * 单价
     */
    private BigDecimal price;
    /**
     * ?e??
     */
    private BigDecimal money;
    /**
     * &agrave;&agrave;D&iacute;￡o￡&uml;1￡?3??&mu; 2￡?&igrave;&aacute;??￡?
     */
    private Integer type;
    /**
     * &times;&prime;&igrave;?￡o￡&uml;￡?
     */
    private Integer status;
    /**
     * ??&mu;￥&plusmn;?&times;￠
     */
    private String remark;
    /**
     * &ograve;?DD??3?
     */
    private String bank;
    /**
     * &ograve;?DD???&sect;
     */
    private String bankAccount;
    /**
     * &ograve;?DD?a?&sect;DD
     */
    private String bankAddress;
    /**
     * &ograve;?DD?&uml;o?
     */
    private String bankCode;
    /**
     * &ecirc;??&uacute;o?
     */
    private String phone;
    /**
     * ?&uuml;D?&ecirc;&plusmn;??
     */
    private Date updateTime;
    /**
     * 2&ugrave;&times;&divide;&egrave;?
     */
    private Integer adminId;
    /**
     * 用户c2c订单表
     */
    private Integer version;
    /**
     * ?&mu;&iacute;3&agrave;&prime;?&prime;
     */
    private Integer platform;
    /**
     * ??&mu;￥&plusmn;&agrave;o?
     */
    private String orderNumber;
}
