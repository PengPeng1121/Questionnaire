package com.pp.web.account;

import java.io.Serializable;
import java.util.List;

/**
 * 登录用户
 */
public final class Account implements Serializable{

	
	/**
	 * 序列化
	 */
	private static final long serialVersionUID = 1L;
	
	// 用户编码
	private String userCode;
	// 用户名
	private String userName;
	//角色
	private String role;
	//未做问卷个数
	private Integer unDoQuestionnaireNum;
	//提醒问卷个数
	private Integer remindDoQuestionnaireNum;
	// 资源
	private List<ResourceGroup> resourceList;
	// 权限编码
	private List<String> permissionCodeList;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Integer getUnDoQuestionnaireNum() {
		return unDoQuestionnaireNum;
	}

	public void setUnDoQuestionnaireNum(Integer unDoQuestionnaireNum) {
		this.unDoQuestionnaireNum = unDoQuestionnaireNum;
	}

	public Integer getRemindDoQuestionnaireNum() {
		return remindDoQuestionnaireNum;
	}

	public void setRemindDoQuestionnaireNum(Integer remindDoQuestionnaireNum) {
		this.remindDoQuestionnaireNum = remindDoQuestionnaireNum;
	}

	public List<ResourceGroup> getResourceList() {
		return resourceList;
	}

	public void setResourceList(List<ResourceGroup> resourceList) {
		this.resourceList = resourceList;
	}

	public List<String> getPermissionCodeList() {
		return permissionCodeList;
	}

	public void setPermissionCodeList(List<String> permissionCodeList) {
		this.permissionCodeList = permissionCodeList;
	}
}
