/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 角色权限表数据实体
 * 
 * @author
 */
public final class RoleAuthority extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 角色代码
    private Long roleCode;

    // 权限代码
    private Long authorityCode;

    /**
     * 设置角色代码
     * 
     * @param roleCode 角色代码
     */
    public void setRoleCode(Long roleCode) {
        this.roleCode = roleCode;
    }

    /**
     * 获取角色代码
     */
    public Long getRoleCode() {
        return this.roleCode;
    }

    /**
     * 设置权限代码
     * 
     * @param authorityCode 权限代码
     */
    public void setAuthorityCode(Long authorityCode) {
        this.authorityCode = authorityCode;
    }

    /**
     * 获取权限代码
     */
    public Long getAuthorityCode() {
        return this.authorityCode;
    }

}