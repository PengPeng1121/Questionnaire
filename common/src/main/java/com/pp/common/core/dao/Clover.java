package com.pp.common.core.dao;

import java.io.Serializable;

/**
 * Clover分片信息
 * 
 * @author
 *
 */
public final class Clover implements Serializable {
	
	/**
	 * 序列化
	 */
	private static final long serialVersionUID = 1L;

	// 服务器总数
	private final int serverCount;

	// 服务器序号
	private final int serverId;
	
	/**
	 * 供JSF反射使用的构造函数
	 */
	public Clover(){
		this.serverCount = 0;
		this.serverId = 0;
	}

	/**
	 * 业务代码中使用的构造函数
	 * @param serverCount 服务器总数
	 * @param serverId 服务器序号
	 */
	public Clover(int serverCount, int serverId) {
		if (serverCount < 1) {
			throw new IllegalArgumentException("参数serverCount不能小于1");
		}
		if (serverId < 0) {
			throw new IllegalArgumentException("参数serverId不能小于0");
		}
		this.serverCount = serverCount;
		this.serverId = serverId;
	}

	public int getServerCount() {
		return serverCount;
	}

	public int getServerId() {
		return serverId;
	}

}
