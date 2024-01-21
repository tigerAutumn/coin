package com.qkwl.admin.layui.exception;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qkwl.common.dto.Enum.ErrorCodeEnum;
import com.qkwl.common.exceptions.BizException;
import com.qkwl.common.util.RespData;

/**
 * 
 * @author huangjinfeng
 */
@ControllerAdvice
public class GlobalExceptionHandler {
	
	private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/**
	 * 处理参数绑定异常
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public RespData<Void> handleMethodArgumentNotValidException(HttpServletRequest req, HttpServletResponse resp, Exception e) {
		MethodArgumentNotValidException be = (MethodArgumentNotValidException) e;
		return handleBindException(be.getBindingResult());
	}
	
	/**
	 * 处理参数绑定异常
	 */
	@ExceptionHandler(BindException.class )
	@ResponseBody
	public RespData<Void> handleBindException(HttpServletRequest req, HttpServletResponse resp, Exception e) {
		BindException be = (BindException) e;
		return handleBindException(be.getBindingResult());
	}

	/**
	 * 处理业务层异常
	 */
	@ExceptionHandler(BizException.class)
	@ResponseBody
	public RespData<Void> handleBussinessException(HttpServletRequest req, HttpServletResponse resp, Exception e) {
		BizException bizException = (BizException) e;
		StackTraceElement stackTraceElement = e.getStackTrace()[0];
		logger.error("【业务异常】类名:{},方法名:{},行数:{}",stackTraceElement.getClassName(),stackTraceElement.getMethodName(),stackTraceElement.getLineNumber());
		ErrorCodeEnum errorCodeEnum = bizException.getErrorCodeEnum();
		return RespData.error(errorCodeEnum, bizException.getArgs());
	}

	/**
	 * 处理其他异常
	 */
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public RespData<Void> handleException(HttpServletRequest req, HttpServletResponse resp, Exception e) {
		logger.error(null,e);
		return RespData.error(ErrorCodeEnum.DEFAULT);
	}

	public RespData<Void> handleBindException(BindingResult bindingResult) {
		List<FieldError> errorList = bindingResult.getFieldErrors();
		List<String> errors = new ArrayList<>(errorList.size());
		for (FieldError error : errorList) {
			errors.add(String.format("%s_%s:%s", error.getObjectName(), error.getField(), error.getDefaultMessage()));
		}
		return RespData.error(ErrorCodeEnum.PARAM_ERROR, String.join("|", errors));
	}

}
