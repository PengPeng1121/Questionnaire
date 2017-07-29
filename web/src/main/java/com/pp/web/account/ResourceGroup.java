package com.pp.web.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 资源：一级菜单
 * 
 * @author
 *
 */
public class ResourceGroup implements Serializable {

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
	// 菜单图标
	private String icon;
	// 二级菜单
	private List<ResourceItem> menus = new ArrayList<>();

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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public List<ResourceItem> getMenus() {
		return menus;
	}

	public void setMenus(List<ResourceItem> menus) {
		this.menus = menus;
	}

	public void addMenu(ResourceItem menuItem) {
		this.menus.add(menuItem);
	}
}
