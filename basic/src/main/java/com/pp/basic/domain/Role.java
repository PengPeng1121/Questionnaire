/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 角色表数据实体
 * 
 * @author
 */
public final class Role extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 角色名称
    private String roleName;

    // 角色代码
    private String roleCode;

    /**
     * 设置角色名称
     * 
     * @param roleName 角色名称
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * 获取角色名称
     */
    public String getRoleName() {
        return this.roleName;
    }

    /**
     * 设置角色代码
     * 
     * @param roleCode 角色代码
     */
    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    /**
     * 获取角色代码
     */
    public String getRoleCode() {
        return this.roleCode;
    }

}