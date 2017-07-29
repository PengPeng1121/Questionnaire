package com.pp.web.interceptor;

import com.pp.basic.domain.SystemUser;
import com.pp.basic.service.SystemUserService;
import com.pp.web.account.Account;
import com.pp.web.account.AccountContext;
import com.pp.web.common.SystemCommon;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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
		if(!StringUtils.isEmpty(userCode)){
			systemUser.setUserCode(userCode);
		}else {
			throw new IllegalAccessException("用户名不能为空");
		}
		if(!StringUtils.isEmpty(userCode)){
			systemUser.setUserPassword(password);
		}else {
			throw new IllegalAccessException("密码不能为空");
		}

		systemUser.setUserCode(userCode);
		systemUser.setUserPassword(password);

		//判断用户是否存在 及权限
		if(this.systemUserService.exists(systemUser)){
			systemUser = this.systemUserService.selectOne(systemUser);
			Account account = new Account();
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
