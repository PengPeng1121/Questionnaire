/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 权限资源表数据实体
 * 
 * @author
 */
public final class AuthorityResource extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 资源代码
    private Long resourceCode;

    // 权限代码
    private Long authorityCode;

    /**
     * 设置资源代码
     * 
     * @param resourceCode 资源代码
     */
    public void setResourceCode(Long resourceCode) {
        this.resourceCode = resourceCode;
    }

    /**
     * 获取资源代码
     */
    public Long getResourceCode() {
        return this.resourceCode;
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