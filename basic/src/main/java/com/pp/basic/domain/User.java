/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 用户表数据实体
 * 
 * @author
 */
public final class User extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 用户名
    private String userName;

    // 密码
    private String passWord;

    /**
     * 设置用户名
     * 
     * @param userName 用户名
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取用户名
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * 设置密码
     * 
     * @param passWord 密码
     */
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    /**
     * 获取密码
     */
    public String getPassWord() {
        return this.passWord;
    }

}