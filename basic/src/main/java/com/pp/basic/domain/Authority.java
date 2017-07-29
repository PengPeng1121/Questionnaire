/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;
/**
 * 权限表数据实体
 * 
 * @author
 */
public final class Authority extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 权限代码
    private Long authorityCode;

    // 权限名称
    private String authorityName;

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

    /**
     * 设置权限名称
     * 
     * @param authorityName 权限名称
     */
    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    /**
     * 获取权限名称
     */
    public String getAuthorityName() {
        return this.authorityName;
    }

}