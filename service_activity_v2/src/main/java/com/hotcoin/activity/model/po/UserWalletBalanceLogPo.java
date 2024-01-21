package com.hotcoin.activity.model.po;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author hf
 * @date 2019-07-01 06:43:58
 * @since jdk1.8
 */
public class UserWalletBalanceLogPo implements Serializable {
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

    /**
     *
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     */
    public void setUid(Integer uid) {
        this.uid = uid;
    }

    /**
     *
     */
    public Integer getUid() {
        return uid;
    }

    /**
     *
     */
    public void setCoinId(Integer coinId) {
        this.coinId = coinId;
    }

    /**
     *
     */
    public Integer getCoinId() {
        return coinId;
    }

    /**
     *
     */
    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    /**
     *
     */
    public String getFieldId() {
        return fieldId;
    }

    /**
     *
     */
    public void setChange(BigDecimal change) {
        this.change = change;
    }

    /**
     *
     */
    public BigDecimal getChange() {
        return change;
    }

    /**
     *
     */
    public void setSrcId(Integer srcId) {
        this.srcId = srcId;
    }

    /**
     *
     */
    public Integer getSrcId() {
        return srcId;
    }

    /**
     * 010 --  &Oacute;&Atilde;&raquo;&sect;&sup3;&auml;&plusmn;&Ograve; 011 --&Oacute;&Atilde;&raquo;&sect;&Igrave;&aacute;&plusmn;&Ograve;&pound;&raquo;  -- 020 &Iuml;&Acirc;&micro;&yen;&pound;&raquo;  -- &acute;&eacute;&ordm;&Iuml;&pound;&raquo; 3 -- &sup1;&Uuml;&Agrave;&iacute;&Ocirc;&plusmn;&Ecirc;&Ouml;&sup1;&curren;&Oacute;&agrave;&para;&icirc;&micro;&divide;&Otilde;&ucirc;; 4 --C2C  5 -- &middot;&micro;&Oacute;&para;
     */
    public void setSrcType(Integer srcType) {
        this.srcType = srcType;
    }

    /**
     * 010 --  &Oacute;&Atilde;&raquo;&sect;&sup3;&auml;&plusmn;&Ograve; 011 --&Oacute;&Atilde;&raquo;&sect;&Igrave;&aacute;&plusmn;&Ograve;&pound;&raquo;  -- 020 &Iuml;&Acirc;&micro;&yen;&pound;&raquo;  -- &acute;&eacute;&ordm;&Iuml;&pound;&raquo; 3 -- &sup1;&Uuml;&Agrave;&iacute;&Ocirc;&plusmn;&Ecirc;&Ouml;&sup1;&curren;&Oacute;&agrave;&para;&icirc;&micro;&divide;&Otilde;&ucirc;; 4 --C2C  5 -- &middot;&micro;&Oacute;&para;
     */
    public Integer getSrcType() {
        return srcType;
    }

    /**
     * IN -- &Egrave;&euml;&middot;&frac12;&Iuml;&ograve;  OUT -- &sup3;&ouml;&middot;&frac12;&Iuml;&ograve;
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * IN -- &Egrave;&euml;&middot;&frac12;&Iuml;&ograve;  OUT -- &sup3;&ouml;&middot;&frac12;&Iuml;&ograve;
     */
    public String getDirection() {
        return direction;
    }

    /**
     *
     */
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    /**
     *
     */
    public Date getCreatetime() {
        return createtime;
    }

    /**
     *
     */
    public void setCreatedatestamp(Long createdatestamp) {
        this.createdatestamp = createdatestamp;
    }

    /**
     *
     */
    public Long getCreatedatestamp() {
        return createdatestamp;
    }

    /**
     *
     */
    public void setOldvalue(BigDecimal oldvalue) {
        this.oldvalue = oldvalue;
    }

    /**
     *
     */
    public BigDecimal getOldvalue() {
        return oldvalue;
    }

    public UserWalletBalanceLogPo() {
    }
}
