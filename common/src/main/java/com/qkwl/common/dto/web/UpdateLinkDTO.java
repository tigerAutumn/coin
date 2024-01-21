package com.qkwl.common.dto.web;

import java.io.Serializable;

public class UpdateLinkDTO implements Serializable {

  private static final long serialVersionUID = 1L;
  
  // 主键ID
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
  // 链接地址
  private String furl;



  public Integer getForder() {
    return forder;
  }

  public void setForder(Integer forder) {
    this.forder = forder;
  }


  public String getFurl() {
    return furl;
  }

  public void setFurl(String furl) {
    this.furl = furl == null ? null : furl.trim();
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

  public Integer getFid() {
    return fid;
  }

  public void setFid(Integer fid) {
    this.fid = fid;
  }
}
