package com.pp.web.controller.advice;

import com.pp.web.account.Account;
import com.pp.web.controller.until.AjaxUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@ControllerAdvice
public class GlobalDefaultExceptionHandler {

	// 日志
	Logger logger = LoggerFactory.getLogger(getClass());

	// 默认出错页面--重新登录
	public static final String DEFAULT_ERROR_VIEW = "login";

	@ExceptionHandler(value = Exception.class)
	public ModelAndView defaultErrorHandler(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {

		// 如果通过注解指定状态，则抛出，框架进行处理
		if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
			throw e;
		}

		// 更改某些异常的提示
		String errorMessage = e.getMessage();

		// 是否AJAX提交
		boolean isAjax = AjaxUtils.isAjaxSumbit(request);
		if (isAjax) {
			// 如果是ajax提交，则返回异常，供js处理
			// 状态码设置为服务器拒绝
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("text/xml;charset=UTF-8");
			PrintWriter writer = response.getWriter();
			writer.write(errorMessage);
			writer.flush();
			return null;
		} else {
			// 如果不是ajax提交，则显示默认出错页面
			ModelAndView mav = new ModelAndView();
			mav.addObject("exception", e);
			mav.addObject("url", request.getRequestURL());
			mav.setViewName(DEFAULT_ERROR_VIEW);
			return mav;
		}
	}
}
