package com.qkwl.common.dto.otc;

import java.io.Serializable;
import java.util.Date;

import com.qkwl.common.dto.Enum.otc.OtcPaymentStatusEnum;
import com.qkwl.common.dto.Enum.otc.OtcPaymentTypeEnum;

public class ListPaymentResp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;
	
	//支付名称
	private String chineseName;
	
	//支付图标
	private String picture;
	
	//状态
	private Integer status;
	private String statusString;
	
	//类型
	private Integer type;
	private String typeString;
	
	//英文简称
	private String englishName;
	
	//创建时间
	private Date createTime;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getTypeString() {
		return OtcPaymentTypeEnum.getValueByCode(type);
	}

	public String getStatusString() {
		return OtcPaymentStatusEnum.getValueByCode(status);
	}

	
}
