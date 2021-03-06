package com.pp.web.controller.advice;

import com.pp.web.controller.until.AjaxUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@ControllerAdvice
public class GlobalDefaultExceptionHandler {

	@ExceptionHandler(value = Exception.class)
	public HashMap<String,Object> defaultErrorHandler(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
		HashMap<String,Object> resultMap = new HashMap<String,Object>();
		// 如果通过注解指定状态，则抛出，框架进行处理
		if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) {
			resultMap.put("status",300);
			resultMap.put("msg",e.getMessage());
			return resultMap;
		}

		// 是否AJAX提交
		boolean isAjax = AjaxUtils.isAjaxSumbit(request);
		if (isAjax) {
			resultMap.put("status",300);
			resultMap.put("msg",e.getMessage());
			return resultMap;
		}
		resultMap.put("status",404);
		resultMap.put("msg",e.getMessage());
		return resultMap;
	}
}
