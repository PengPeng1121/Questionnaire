package com.pp.web.interceptor;

import com.pp.basic.domain.SystemUser;
import com.pp.basic.service.SystemUserService;
import com.pp.web.account.Account;
import com.pp.web.account.AccountContext;
import com.pp.web.common.SystemCommon;
import org.apache.commons.lang.StringUtils;
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

	Logger log = LoggerFactory.getLogger(LoginHandlerInterceptor.class.getName());

	@Autowired
	SystemUserService systemUserService;

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


		HttpSession session = request.getSession();
		SystemUser systemUser = new SystemUser();

		String userCode = request.getParameter("userCode");
		String password = request.getParameter("password");

		try {
			if(!StringUtils.isEmpty(userCode)){
				systemUser.setUserCode(userCode);
			}else {
				throw new IllegalAccessException("用户名不能为空");
			}
			if(!StringUtils.isEmpty(password)){
				systemUser.setUserPassword(password);
			}else {
				throw new IllegalAccessException("密码不能为空");
			}
			//判断用户是否存在 及权限
			if(this.systemUserService.exists(systemUser)){
				systemUser = this.systemUserService.selectOne(systemUser);
				Account account = new Account();
				account.setUserCode(systemUser.getUserCode());
				account.setRole(systemUser.getUserAuthority());
				account.setUserName(systemUser.getUserName());
				//保存在上下文
				AccountContext.setAccount(account);
				//保存session 值为 systemUser   session 为 questionnaire_user + "_" +userCode
				session.setAttribute(SystemCommon.COOKIE_NAME+"_"+userCode,systemUser);
				Cookie[] cookies = request.getCookies();
				//首先删除cookie
				if(cookies!=null){
					for (Cookie cookie :cookies) {
						if (cookie.getName().equals(SystemCommon.COOKIE_NAME)) {
							cookie.setValue(null);
							cookie.setPath("/");
							cookie.setMaxAge(0);// 立即销毁cookie
							response.addCookie(cookie);
							break;
						}
					}
				}
				//重写cookie  cookie 值为用户id 用来获取session
				Cookie cookie = new Cookie(SystemCommon.COOKIE_NAME, userCode);
				cookie.setMaxAge(SystemCommon.COOKIE_EXPIRE_TIME);// 设置为2h
				cookie.setPath("/");
				response.addCookie(cookie);
			}else {
				throw new IllegalArgumentException("没有找到该用户！");
			}
		}catch (Exception e){
			log.error("用户登录出错,用户："+userCode +" ，msg:"+e.getMessage(),e);
			return false;
		}
		return true;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}
}
