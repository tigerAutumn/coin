package com.qkwl.common.dto.mq;

import net.openhft.chronicle.values.MaxUtf8Length;

import java.math.BigDecimal;

 public interface IMQEntrust  {
     String getFentrustid();

     void setFentrustid(@MaxUtf8Length(20) String fentrustid);

     long getFuid() ;

     void setFuid(long fuid);

     int getFsource() ;

     void setFsource(int fsource);

     int getFstatus() ;

     void setFstatus(int fstatus);

     int getFtradeid() ;

     void setFtradeid(int ftradeid);

     int getFtype() ;

     void setFtype(int ftype) ;

     String getSize() ;

     void setSize(@MaxUtf8Length(32) String size) ;

     String getLeftcount() ;

     void setLeftcount(@MaxUtf8Length(32) String leftcount);

     String getPrize() ;
     void setPrize(@MaxUtf8Length(32) String prize);

     String getFunds();
     void setFunds(@MaxUtf8Length(32) String funds);

     String getLeftfunds();
     void setLeftfunds(@MaxUtf8Length(32) String leftfunds);

     int getMatchtype();
     void setMatchtype(int matchtype);

     long getCreatedate() ;

     void setCreatedate(long createdate);

     boolean isFreefee() ;

     void setFreefee(boolean freefee) ;

     int getApiopenrate() ;

     void setApiopenrate(int apiopenrate) ;


     void setApirate(@MaxUtf8Length(32) String apirate);
     String getApirate();
     int getRecover();
     void setRecover(int recover);
     int getLeverorder();
     void setLeverorder(int leverorder);


}
