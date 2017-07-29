package com.pp.web.controller.until;

import javax.servlet.http.HttpServletRequest;

public abstract class AjaxUtils {

	/**
	 * 判断是否ajax提交
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isAjaxSumbit(HttpServletRequest request) {
		return (request.getHeader("accept").indexOf("application/json") > -1
				|| (request.getHeader("X-Requested-With") != null
						&& request.getHeader("X-Requested-With").indexOf("XMLHttpRequest") > -1));
	}
}
