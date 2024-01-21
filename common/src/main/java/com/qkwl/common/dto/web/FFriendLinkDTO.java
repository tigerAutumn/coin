package com.qkwl.common.dto.web;

import java.io.Serializable;
import java.util.Date;
import com.qkwl.common.dto.Enum.LinkTypeEnum;

public class FFriendLinkDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Integer fid;

  private String zhCNName;
  private String zhTWName;
  private String enUSName;
  private String koKRName;

  private String zhTWFdescription;
  private String zhCNFdescription;
  private String enUSFdescription;
  private String koKRFdescription;
  
  private String  zhCNFurl;
  private String  zhTWFurl;
  private String  enUSFurl;
  private String  koKRFurl;
  

  // 排序
  private Integer forder;
  // 创建时间
  private Date fcreatetime;
  // 类型
  private Integer ftype;
  // 类型
  private String ftype_s;
  // 版本
  private Integer version;

  public FFriendLinkDTO() {
    getFtype_s();
  }

  public Integer getFid() {
    return fid;
  }

  public void setFid(Integer fid) {
    this.fid = fid;
  }



  public Integer getForder() {
    return forder;
  }

  public void setForder(Integer forder) {
    this.forder = forder;
  }

  public Date getFcreatetime() {
    return fcreatetime;
  }

  public void setFcreatetime(Date fcreatetime) {
    this.fcreatetime = fcreatetime;
  }


  public Integer getFtype() {
    return ftype;
  }

  public void setFtype(Integer ftype) {
    this.ftype = ftype;
  }

  public String getFtype_s() {
    if (this.getFtype() == null) {
      return "";
    }
    ftype_s = LinkTypeEnum.getEnumString(this.getFtype());
    return ftype_s;
  }

  public void setFtype_s(String ftype_s) {
    this.ftype_s = ftype_s;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public String getZhCNName() {
    return zhCNName;
  }

  public void setZhCNName(String zhCNName) {
    this.zhCNName = zhCNName;
  }

  public String getEnUSName() {
    return enUSName;
  }

  public void setEnUSName(String enUSName) {
    this.enUSName = enUSName;
  }

  public String getZhCNFdescription() {
    return zhCNFdescription;
  }

  public void setZhCNFdescription(String zhCNFdescription) {
    this.zhCNFdescription = zhCNFdescription;
  }

  public String getEnUSFdescription() {
    return enUSFdescription;
  }

  public void setEnUSFdescription(String enUSFdescription) {
    this.enUSFdescription = enUSFdescription;
  }

  public String getKoKRName() {
    return koKRName;
  }

  public void setKoKRName(String koKRName) {
    this.koKRName = koKRName;
  }

  public String getKoKRFdescription() {
    return koKRFdescription;
  }

  public void setKoKRFdescription(String koKRFdescription) {
    this.koKRFdescription = koKRFdescription;
  }

  public String getZhTWName() {
    return zhTWName;
  }

  public void setZhTWName(String zhTWName) {
    this.zhTWName = zhTWName;
  }

  public String getZhTWFdescription() {
    return zhTWFdescription;
  }

  public void setZhTWFdescription(String zhTWFdescription) {
    this.zhTWFdescription = zhTWFdescription;
  }

  public String getZhCNFurl() {
    return zhCNFurl;
  }

  public void setZhCNFurl(String zhCNFurl) {
    this.zhCNFurl = zhCNFurl;
  }

  public String getZhTWFurl() {
    return zhTWFurl;
  }

  public void setZhTWFurl(String zhTWFurl) {
    this.zhTWFurl = zhTWFurl;
  }

  public String getEnUSFurl() {
    return enUSFurl;
  }

  public void setEnUSFurl(String enUSFurl) {
    this.enUSFurl = enUSFurl;
  }

  public String getKoKRFurl() {
    return koKRFurl;
  }

  public void setKoKRFurl(String koKRFurl) {
    this.koKRFurl = koKRFurl;
  }
}
