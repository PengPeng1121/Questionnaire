/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 资源表数据实体
 * 
 * @author
 */
public final class Resource extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 资源代码
    private Long resourceCode;

    // 资源名称
    private String resourceName;

    // 资源路径
    private String resourceUrl;

    // 资源等级
    private String resourceLevel;

    // 父级资源码
    private String resourceParentCode;

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
     * 设置资源名称
     * 
     * @param resourceName 资源名称
     */
    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    /**
     * 获取资源名称
     */
    public String getResourceName() {
        return this.resourceName;
    }

    /**
     * 设置资源路径
     * 
     * @param resourceUrl 资源路径
     */
    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    /**
     * 获取资源路径
     */
    public String getResourceUrl() {
        return this.resourceUrl;
    }

    /**
     * 设置资源等级
     * 
     * @param resourceLevel 资源等级
     */
    public void setResourceLevel(String resourceLevel) {
        this.resourceLevel = resourceLevel;
    }

    /**
     * 获取资源等级
     */
    public String getResourceLevel() {
        return this.resourceLevel;
    }

    /**
     * 设置父级资源码
     * 
     * @param resourceParentCode 父级资源码
     */
    public void setResourceParentCode(String resourceParentCode) {
        this.resourceParentCode = resourceParentCode;
    }

    /**
     * 获取父级资源码
     */
    public String getResourceParentCode() {
        return this.resourceParentCode;
    }

}