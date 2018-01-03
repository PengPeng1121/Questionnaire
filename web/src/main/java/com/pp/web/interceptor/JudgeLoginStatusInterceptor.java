package com.pp.web.interceptor;

import com.pp.basic.domain.SystemUser;
import com.pp.web.account.Account;
import com.pp.web.account.AccountContext;
import com.pp.web.common.SystemCommon;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 用户登录拦截器-判读是否登录
 */
public class JudgeLoginStatusInterceptor implements HandlerInterceptor {

	Logger log = LoggerFactory.getLogger(JudgeLoginStatusInterceptor.class.getName());

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		HttpSession session = request.getSession();
		Account	account= null ;
		try{
			String userCode = "";
			Cookie[] cookies = request.getCookies();
			if (cookies ==null || cookies.length ==0){
				log.error("请重新登录");
				return true;
			}
			for(Cookie cookie : cookies){
				if(cookie.getName().equals(SystemCommon.COOKIE_NAME)){
					userCode = cookie.getValue();
					break;
				}
			}
			if(StringUtils.isEmpty(userCode)){
				log.error("没有登陆");
				return true;
			}
			log.info("用户:"+userCode+"在判断用户登录状态！！");
			SystemUser systemUser = (SystemUser) session.getAttribute(SystemCommon.COOKIE_NAME+"_"+userCode);
			account = new Account();
			account.setUserCode(systemUser.getUserCode());
			account.setRole(systemUser.getUserAuthority());
			account.setUserName(systemUser.getUserName());
		}catch (Exception e){
			log.error("判断用户登录状态时出错，msg:"+e.getMessage(),e);
			return true;
		}
		AccountContext.setAccount(account);
		return true;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}
}
