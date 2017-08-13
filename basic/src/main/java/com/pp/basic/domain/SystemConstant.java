/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 系统常量数据实体
 * 
 * @author
 */
public final class SystemConstant extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 系统常量类型  过期时间
    public static final String CONSTANT_TYPE_EXPIRES="0";

    // 系统常量
    private String constant;

    // 系统常量类型
    private String constantType;

    /**
     * 设置系统常量
     * 
     * @param constant 系统常量
     */
    public void setConstant(String constant) {
        this.constant = constant;
    }

    /**
     * 获取系统常量
     */
    public String getConstant() {
        return this.constant;
    }

    /**
     * 设置系统常量类型
     * 
     * @param constantType 系统常量类型
     */
    public void setConstantType(String constantType) {
        this.constantType = constantType;
    }

    /**
     * 获取系统常量类型
     */
    public String getConstantType() {
        return this.constantType;
    }

}