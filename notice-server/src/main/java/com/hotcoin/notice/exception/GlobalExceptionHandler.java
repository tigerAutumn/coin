package com.hotcoin.notice.exception;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hotcoin.notice.Enum.ErrorCodeEnum;
import com.hotcoin.notice.dto.RespData;

/**
 * 
 * @author huangjinfeng
 */
@ControllerAdvice
public class GlobalExceptionHandler {

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
	@ExceptionHandler(NoticeException.class)
	@ResponseBody
	public RespData<Void> handleBussinessException(HttpServletRequest req, HttpServletResponse resp, Exception e) {
		NoticeException bizException = (NoticeException) e;
		ErrorCodeEnum errorCodeEnum = bizException.getErrorCodeEnum();
		return RespData.error(errorCodeEnum, bizException.getArgs());
	}

	/**
	 * 处理其他异常
	 */
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public RespData<Void> handleException(HttpServletRequest req, HttpServletResponse resp, Exception e) {
		return RespData.error(ErrorCodeEnum.DEFAULT, e.getMessage());
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
