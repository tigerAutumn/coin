/**
 * 
 */
package com.qkwl.common.dto.user;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author huangjinfeng
 */
@ApiModel(value = "GenerateQRCodeTokenResp", description = "生成qrcode token")
public class GenerateQRCodeTokenResp implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(value = "二维码token")
	private String qrCodeToken;

	public String getQrCodeToken() {
		return qrCodeToken;
	}

	public void setQrCodeToken(String qrCodeToken) {
		this.qrCodeToken = qrCodeToken;
	}
}
