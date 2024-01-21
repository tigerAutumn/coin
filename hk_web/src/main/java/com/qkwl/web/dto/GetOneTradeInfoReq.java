/**
 * 
 */
package com.qkwl.web.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangjinfeng
 */


@ApiModel(value = "GetOneTradeInfoReq", description = "根据sellName和buyName获取一条交易对信息参数")
public class GetOneTradeInfoReq implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "交易区id",example = "1")
	private Integer type=1;
	
	@ApiModelProperty(value = "sellShortName",example ="BTC" )
	private String sellShortName="BTC";
	
	@ApiModelProperty(value = "buyShortName",example ="USDT" )
	private String buyShortName="USDT";

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getSellShortName() {
		return sellShortName;
	}

	public void setSellShortName(String sellShortName) {
		this.sellShortName = sellShortName;
	}

	public String getBuyShortName() {
		return buyShortName;
	}

	public void setBuyShortName(String buyShortName) {
		this.buyShortName = buyShortName;
	}

}
