/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 用户角色关系表数据实体
 * 
 * @author
 */
public final class UserRole extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 用户id
    private Long userId;

    // 角色代码
    private String roleCode;

    /**
     * 设置用户id
     * 
     * @param userId 用户id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取用户id
     */
    public Long getUserId() {
        return this.userId;
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