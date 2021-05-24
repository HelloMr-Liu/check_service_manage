package org.king2.check_service_manage.exception;

import org.king2.check_service_manage.constant.SystemResultCodeEnum;
import org.king2.check_service_manage.entity.SystemResultVo;
import org.king2.check_service_manage.utils.ApplicationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;


/**
 * 描述：全局异常接收器
 * @author 刘梓江
 * @date   2020/6/9 13:55
 */
@RestControllerAdvice
public class GlobalExceptionHandle {

	private static final Logger logger= LoggerFactory.getLogger(GlobalExceptionHandle.class);

	/**
	 * 处理全局异常
	 * @param exception	异常对象
	 */
	@ExceptionHandler(Exception.class)
	public Object errorHandler(Exception exception) {
		pageExceptionLog(exception);
		return SystemResultVo.build(SystemResultCodeEnum.ERROR.STATE,SystemResultCodeEnum.ERROR.MESS,null);
	}

	/**
	 * 处理数据校验异常
	 * @param exception	数据校验异常对象
	 * @return
	 */
	@ExceptionHandler(BindException.class)
	public Object dataException(BindException exception) {
		pageExceptionLog(exception);
		// 返回数据校验错误信息
		return SystemResultVo.build(SystemResultCodeEnum.CHECK_ERROR.STATE, SystemResultCodeEnum.CHECK_ERROR.MESS, exception.getFieldError().getDefaultMessage());
	}

	/**
	 * 处理请求单个参数不满足校验规则的异常信息
	 * @param exception	数据校验异常对象
	 * @return
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public Object constraintViolationExceptionHandler(ConstraintViolationException exception) {
		pageExceptionLog(exception);
		// 执行校验，获得校验结果
		Set<ConstraintViolation<?>> validResult = exception.getConstraintViolations();
		// 返回错误信息
		return SystemResultVo.build(SystemResultCodeEnum.CHECK_ERROR.STATE, SystemResultCodeEnum.CHECK_ERROR.MESS, validResult.iterator().next().getMessage());
	}

	/**
	 * 处理加了@RequestBody数据校验异常
	 * @param exception	数据校验异常对象
	 * @return
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Object MethodArgumentNotValidException(MethodArgumentNotValidException exception) {
		pageExceptionLog(exception);
		// 返回数据校验错误信息
		return SystemResultVo.build(SystemResultCodeEnum.CHECK_ERROR.STATE, SystemResultCodeEnum.CHECK_ERROR.MESS, exception.getBindingResult().getFieldError().getDefaultMessage());
	}

	/**
	 * 记录异常日志信息
	 * @param e
	 */
	private void pageExceptionLog(Throwable e){
		// 打印异常信息
		String exceptionMessage = ApplicationUtil.getExceptionMessage(e);
		logger.warn("异常信息："+exceptionMessage);
	}

}
