package com.pp.web.controller.until;

import com.pp.web.account.Account;
import com.pp.web.account.AccountContext;

/**
 * 账号工具类
 *
 * @author
 *
 */
public abstract class AccountUtils {

	/**
	 * 取得当前登录用户信息
	 *
	 * @return
	 */
	public static Account getCurrentAccount() {

		return AccountContext.getAccount();

	}

	public static void removeAccount() {
		AccountContext.removeAccount();
	}
}
