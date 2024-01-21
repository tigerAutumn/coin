package com.hotcoin.activity.model.resp;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author hf
 * @date 2019-07-01 06:43:58
 * @since jdk1.8
 */
@Setter
@Getter
public class UserWalletBalanceLogResp implements Serializable {
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
    private String fieldId;
    /**
     *
     */
    private BigDecimal change;
    /**
     *
     */
    private Integer srcId;
    /**
     * 010 --  &Oacute;&Atilde;&raquo;&sect;&sup3;&auml;&plusmn;&Ograve; 011 --&Oacute;&Atilde;&raquo;&sect;&Igrave;&aacute;&plusmn;&Ograve;&pound;&raquo;  -- 020 &Iuml;&Acirc;&micro;&yen;&pound;&raquo;  -- &acute;&eacute;&ordm;&Iuml;&pound;&raquo; 3 -- &sup1;&Uuml;&Agrave;&iacute;&Ocirc;&plusmn;&Ecirc;&Ouml;&sup1;&curren;&Oacute;&agrave;&para;&icirc;&micro;&divide;&Otilde;&ucirc;; 4 --C2C  5 -- &middot;&micro;&Oacute;&para;
     */
    private Integer srcType;
    /**
     * IN -- &Egrave;&euml;&middot;&frac12;&Iuml;&ograve;  OUT -- &sup3;&ouml;&middot;&frac12;&Iuml;&ograve;
     */
    private String direction;
    /**
     *
     */
    private Date createtime;
    /**
     *
     */
    private Long createdatestamp;
    /**
     *
     */
    private BigDecimal oldvalue;
}
