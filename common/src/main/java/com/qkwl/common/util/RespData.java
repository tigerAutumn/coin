package com.qkwl.common.util;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.qkwl.common.dto.Enum.ErrorCodeEnum;

/**
 * 
 * @author huangjinfeng
 */
@JsonInclude(Include.NON_NULL)
public class RespData<T> {

	private T data;

	// 相关的错误信息
	private String msg = "";

	private int pageNum; // option int 第几页数据

	// 成功（200），失败则由相关失败码
	private int code = 200;

	private long totalCount; // option int 数据总条数

	private int totalPages; // option int 数据总页数

	private long time = new Date().getTime();

	private RespData() {

	}

	public RespData<T> setPagination(int pageNum, int pageSize, long totalCount) {
		// 总记录数
		this.setTotalCount(totalCount);

		int totalPages;
		if (pageSize <= 0) {
			totalPages = 1;
		} else {
			totalPages = (int)(totalCount - 1) / pageSize + 1;
		}

		// 总页数，每页记录数用请求消息里的定义
		this.setTotalPages(totalPages);

		// 当前数据是第几页，如果请求的页码小余重新计算的总记录数，则页码不变；否则页码就是总页数
		this.setPageNum(pageNum <= totalPages ? pageNum : totalPages);
		return this;
	}
	
	

	public T getData() {
		return data;
	}
	

	public void setData(T data) {
		this.data = data;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String errMsg) {
		this.msg = errMsg;
	}



	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}



	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public static <T> RespData<T> ok(T t) {
		RespData<T> resp = new RespData<>();
		resp.setData(t);
		return resp;
	}
	
	public static RespData<Void> ok() {
		RespData<Void> resp = new RespData<>();
		// 异常信息国际化
		String msg = I18NUtils.getString("200");
		if (StringUtils.isBlank(msg)) {
			msg = "成功";
		}
		resp.setCode(200);
		resp.setMsg(msg);
		return resp;
	}
	
	public static <R>RespData<List<R>> ok(PageData<R> pageData) {
		RespData<List<R>> resp = new RespData<>();
		
		String msg = I18NUtils.getString("200");
		if (StringUtils.isBlank(msg)) {
			msg = "成功";
		}
		resp.setCode(200);
		resp.setMsg(msg);
		resp.setData(pageData.getList());
		resp.setPagination(pageData.getPageNum(), pageData.getPageSize(), pageData.getTotalRecord());
		return resp;
	}

	/**
	 * 设置响应失败标志.
	 * 
	 * @param errorCodeEnum
	 * @param args          错误信息参数
	 * @return
	 */
	public static RespData<Void> error(ErrorCodeEnum errorCodeEnum, Object... args) {
		if (errorCodeEnum == null) {
			errorCodeEnum = ErrorCodeEnum.DEFAULT;
		}
		
		RespData<Void> resp = new RespData<>();
		// 异常信息国际化
		String errorMsg = I18NUtils.getString(String.valueOf(errorCodeEnum.getCode()), args);
		if (StringUtils.isBlank(errorMsg)) {
			errorMsg = MessageFormat.format(errorCodeEnum.getDesc(), args);
		}
		resp.setCode(errorCodeEnum.getCode());
		resp.setMsg(errorMsg);
		return resp;
	}
}
