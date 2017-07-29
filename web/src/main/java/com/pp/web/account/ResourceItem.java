package com.pp.web.account;

import java.io.Serializable;

/**
 * 资源：二级菜单
 * @author
 *
 */
public class ResourceItem implements Serializable {

	/**
	 * 序列化
	 */
	private static final long serialVersionUID = 1L;
	
	// 菜单编号
	private String id;
	// 菜单编码
	private String code;
	// 菜单名称
	private String name;
	// 菜单链接
	private String url;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

}
