package com.pp.web.interceptor;

import com.pp.basic.domain.SystemUser;
import com.pp.basic.service.SystemUserService;
import com.pp.web.account.Account;
import com.pp.web.account.AccountContext;
import com.pp.web.common.SystemCommon;
import com.pp.web.controller.until.AccountUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 用户登录拦截器-判读是否登录
 */
public class LoginHandlerInterceptor implements HandlerInterceptor {

	@Autowired
	SystemUserService systemUserService;

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


		HttpSession session = request.getSession();
		SystemUser systemUser = new SystemUser();

		String userCode = request.getParameter("userCode");
		String password = request.getParameter("password");

		Account account = new Account();
		systemUser.setUserCode(userCode);
		systemUser.setUserPassword(password);

		//判断用户是否存在 及权限
		if(this.systemUserService.exists(systemUser)){
			systemUser = this.systemUserService.selectOne(systemUser);
			account.setUserCode(systemUser.getUserCode());
			account.setRole(systemUser.getUserAuthority());
			account.setUserName(systemUser.getUserName());
			//保存在上下文
			AccountContext.setAccount(account);
			//保存在页面
			session.setAttribute(SystemCommon.COOKIE_NAME,systemUser);
		}else {
			throw new IllegalArgumentException("没有找到该用户！");
		}
		return true;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}
}
