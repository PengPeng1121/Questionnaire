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
public class GetCurrentAccountInterceptor implements HandlerInterceptor {

	Logger log = LoggerFactory.getLogger(GetCurrentAccountInterceptor.class.getName());

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		request.setCharacterEncoding("utf-8");
		HttpSession session = request.getSession();
		String userCode = "";
		Account account =null;
		try{
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
			//利用session 判断登录
			//从cookie中获取当前登录的用户id
			// session 获取登录的用户信息
			SystemUser systemUser = (SystemUser)session.getAttribute(SystemCommon.COOKIE_NAME+"_"+userCode);
			if (systemUser == null) {
				log.error("没有登陆");
			}else {
				account = new Account();
				//根据systemUser赋值
				account.setUserCode(systemUser.getUserCode());
				account.setUserName(systemUser.getUserName());
				account.setRole(systemUser.getUserAuthority());
			}
		}catch (Exception e){
			log.error("获取登录用户信息时出错，msg:"+e.getMessage(),e);
			return false;
		}
		AccountContext.setAccount(account);
		return true;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}
}
