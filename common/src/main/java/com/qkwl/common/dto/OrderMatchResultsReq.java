/**
 * 
 */
package com.qkwl.common.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangjinfeng
 */
public class OrderMatchResultsReq  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NotEmpty
	private String AccessKeyId;
	@NotEmpty
	private  String symbol;
	@NotEmpty
	@Pattern(regexp="0|1",message="0或1")
	private  String types;
	@Pattern(regexp="\\d{4}(\\-)\\d{1,2}\\1\\d{1,2}",message="startDate格式错误")
	private  String startDate;
	@Pattern(regexp="\\d{4}(\\-)\\d{1,2}\\1\\d{1,2}",message="endDate格式错误")
	private  String endDate;
	@ApiModelProperty(value = "查询起始ID",required = false)
	private  String from;
	@Pattern(regexp="prev|next",message="可选prev,next")
	private  String direct="next";
	@Pattern(regexp="^([1-9]|[1-9]\\d|100)$",message="[1,100]")
	private  String size="100";
	public String getAccessKeyId() {
		return AccessKeyId;
	}
	public void setAccessKeyId(String accessKeyId) {
		AccessKeyId = accessKeyId;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getTypes() {
		return types;
	}
	public void setTypes(String types) {
		this.types = types;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getDirect() {
		return direct;
	}
	public void setDirect(String direct) {
		this.direct = direct;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
}
