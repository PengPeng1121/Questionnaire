package com.pp.web.account;
/**
 * 应用上下文
 */
public final class AccountContext {

	private final static ThreadLocal<Account> ACCOUNT_HOLDER = new ThreadLocal<Account>();
	
	public static void setAccount(Account account) {
		ACCOUNT_HOLDER.set(account);
    }
	
	public static Account getAccount(){
		return ACCOUNT_HOLDER.get();
	}

	public static void removeAccount(){
		ACCOUNT_HOLDER.remove();
	}
}
