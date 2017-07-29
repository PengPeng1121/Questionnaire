package com.pp.common.util;

import java.util.UUID;

public abstract class UuidUtils {

	/**
	 * 获取一个UUID字符串
	 * @return UUID字符串
	 */
	public static String getUuid() {
		String uuid = UUID.randomUUID().toString();
		return uuid.replace("-", "");
	}
	
}
