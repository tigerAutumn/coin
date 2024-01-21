package com.hotcoin.activity.model.resp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author hf
 * @date 2019-07-01 06:43:56
 * @since jdk1.8
 */
@Setter
@Getter
public class UserCoinWalletResp implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private Integer id;
    /**
     *
     */
    private Integer uid;
    /**
     *
     */
    private Integer coinId;
    /**
     *
     */
    private BigDecimal total;
    /**
     *
     */
    private BigDecimal frozen;
    /**
     *
     */
    private BigDecimal borrow;
    /**
     *
     */
    private BigDecimal ico;
    /**
     *
     */
    private Date gmtCreate;
    /**
     *
     */
    private Date gmtModified;
    /**
     *
     */
    private Long version;
    /**
     * 创新区充值冻结值
     */
    private BigDecimal depositFrozen;
    /**
     * 创新区充值累计值
     */
    private BigDecimal depositFrozenTotal;
}
