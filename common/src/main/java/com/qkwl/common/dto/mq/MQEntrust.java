package com.qkwl.common.dto.mq;



import java.io.Serializable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
public class MQEntrust implements Serializable,IMQEntrust {
    public String fentrustid;
    public long fuid;
    public int fsource;
    public int fstatus;
    public int ftradeid;
    private int ftype;
    public String size;
    public String leftcount;
    public String prize;
    public long createdate;
    public boolean freefee; // false --不免手续费  true--免手续费
    //apiAuth rate
    private int apiopenrate;
    private String apirate;
    private int recover;
    private int leverorder;
    private long lastupdatedate;
    private String funds;
    private String leftfunds;
    private int matchtype;

    //testaaa

}