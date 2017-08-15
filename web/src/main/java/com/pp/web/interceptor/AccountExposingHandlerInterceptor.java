package com.pp.web.interceptor;

import com.pp.basic.domain.SystemUser;
import com.pp.web.account.Account;
import com.pp.web.account.AccountContext;
import com.pp.web.common.SystemCommon;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 用户登录拦截器-判读是否登录
 */
public class AccountExposingHandlerInterceptor implements HandlerInterceptor {

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		Account account = AccountContext.getAccount();

		HttpSession session = request.getSession();
		SystemUser systemUser = (SystemUser)session.getAttribute(SystemCommon.COOKIE_NAME);
		//利用session 判断登录
		if (account== null && systemUser == null) {
			throw new IllegalArgumentException("没有登陆");
		}else if(account== null && systemUser!=null){
			Account accountNew = new Account();
			//根据systemUser赋值
			accountNew.setUserCode(systemUser.getUserCode());
			accountNew.setUserName(systemUser.getUserName());
			accountNew.setRole(systemUser.getUserAuthority());
			AccountContext.setAccount(accountNew);
		}
		return true;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}
}
