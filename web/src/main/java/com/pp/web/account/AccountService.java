package com.pp.web.account;

/**
 * 账号服务
 * 
 * @author
 *
 */
public interface AccountService {

	/**
	 * 获取用户账号信息
	 * 
	 * @param userId
	 *            用户编码
	 * @return 用户账号信息
	 */
	Account getAccount(String userId);

}
