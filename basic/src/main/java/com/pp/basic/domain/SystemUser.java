/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 学生信息表数据实体
 * 
 * @author
 */
public final class SystemUser extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    public static final String AUTHOR_ADMIN ="1";

    public static final String AUTHOR_USER ="2";

    // 用户编号
    private String userCode;

    // 用户名
    private String userName;

    // 用户密码
    private String userPassword;

    // 用户权限 1 管理员  2 普通用户
    private String userAuthority;

    /**
     * 设置用户编号
     * 
     * @param userCode 用户编号
     */
    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    /**
     * 获取用户编号
     */
    public String getUserCode() {
        return this.userCode;
    }

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
     * 设置用户密码
     * 
     * @param userPassword 用户密码
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    /**
     * 获取用户密码
     */
    public String getUserPassword() {
        return this.userPassword;
    }

    /**
     * 设置用户权限 1 管理员  2 普通用户
     * 
     * @param userAuthority 用户权限 1 管理员  2 普通用户
     */
    public void setUserAuthority(String userAuthority) {
        this.userAuthority = userAuthority;
    }

    /**
     * 获取用户权限 1 管理员  2 普通用户
     */
    public String getUserAuthority() {
        return this.userAuthority;
    }

}