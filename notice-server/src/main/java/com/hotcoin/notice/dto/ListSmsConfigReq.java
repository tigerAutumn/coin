/**
 * 
 */
package com.hotcoin.notice.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;

/**
 * @author huangjinfeng
 */
@ApiModel(value = "ListSmsConfigReq", description = "列出sms配置参数")
public class ListSmsConfigReq extends PageDTO implements Serializable {

	private static final long serialVersionUID = 1L;

}
