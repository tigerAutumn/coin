package com.hotcoin.notice.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @author huangjinfeng
 *
 */
//@Service
//@Aspect
public class LogAspceJAdvice {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(LogAspceJAdvice.class);
	@Pointcut("execution(public * com.hotcoin.notice.service.impl..*.*(..))")
	public void aspectjMethod() {
	};

	@Before(value = "aspectjMethod()")
	public void beforeAdvice(JoinPoint joinPoint) {
		logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>> {}.{}  start",joinPoint.getSignature().getDeclaringTypeName() , joinPoint.getSignature().getName() );
		Object[] args = joinPoint.getArgs();
		for (int i = 0; i < args.length; i++) {
			logger.info("参数{}:{}" ,i+1,JSON.toJSONString(args[i]));
		}
	}

	@After(value = "aspectjMethod()")
	public void afterAdvice(JoinPoint joinPoint) {
		logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<< {}.{} end ",joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
	}

	@AfterReturning(value = "aspectjMethod()", returning = "retVal")
	public void afterReturningAdvice(JoinPoint joinPoint, Object retVal) {
		logger.info("返回结果:{}",JSON.toJSONString(retVal));
	}

	@AfterThrowing(value = "aspectjMethod()", throwing = "ex")
	public void afterThrowingAdvice(JoinPoint joinPoint, Exception ex) {
		logger.error("返回结果异常");
	}
}