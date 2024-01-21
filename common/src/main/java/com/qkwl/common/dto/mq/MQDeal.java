package com.qkwl.common.dto.mq;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

@Data
@Getter
@Setter
@ToString
public class MQDeal implements Serializable,Cloneable {
    String buyfuid;
    String type;
    String sellfuid;
    String buycoinid;
    String sellcoinid;
    String buyentrustid;
    String sellentrustid;
    BigDecimal dealamount;
    BigDecimal dealcount;
    BigDecimal sellhandlefeeAmount;
    BigDecimal buyhandlefeeCount;
    BigDecimal buyleftcount;
    BigDecimal sellleftcount;
    BigDecimal leftfunds;
    BigDecimal dealprice;
    Long buycreatetime;
    Long sellcreatetime;
    Integer systemtradetypeid;
    Integer buystatus;
    Integer sellstatus;
    Long dealtime;
    UUID uuid;
    String dealresult;
    int buyleverorder;
    int sellleverorder;
    int hashcode;
    //买单的下单类型   MatchTypeEnum
    int buymatchtype;
    @Override
    public Object clone() {
        MQDeal mqDeal = null;
        try{
            mqDeal = (MQDeal) super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return mqDeal;
    }

}
