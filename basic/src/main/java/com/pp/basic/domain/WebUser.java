/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import java.util.Date;
import com.pp.common.core.AbstractEntity;


/**
 * 数据实体
 * 
 * @author
 */
public final class WebUser extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 
    private String userName;

    // 
    private String userPassword;

    // 
    private Date userLastLoginTime;

    // 开始查询条件
    private transient Date userLastLoginTimeBegin;

    // 截止查询条件
    private transient Date userLastLoginTimeEnd;

    /**
     * 设置
     * 
     * @param userName 
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * 设置
     * 
     * @param userPassword 
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    /**
     * 获取
     */
    public String getUserPassword() {
        return this.userPassword;
    }

    /**
     * 设置
     * 
     * @param userLastLoginTime 
     */
    public void setUserLastLoginTime(Date userLastLoginTime) {
        this.userLastLoginTime = userLastLoginTime;
    }

    /**
     * 获取
     */
    public Date getUserLastLoginTime() {
        return this.userLastLoginTime;
    }

    /**
     * 设置开始查询条件
     * 
     * @param userLastLoginTimeBegin 开始查询条件
     */
    public void setUserLastLoginTimeBegin(Date userLastLoginTimeBegin) {
        this.userLastLoginTimeBegin = userLastLoginTimeBegin;
    }

    /**
     * 获取开始查询条件
     */
    public Date getUserLastLoginTimeBegin() {
        return this.userLastLoginTimeBegin;
    }

    /**
     * 设置截止查询条件
     * 
     * @param userLastLoginTimeEnd 截止查询条件
     */
    public void setUserLastLoginTimeEnd(Date userLastLoginTimeEnd) {
        this.userLastLoginTimeEnd = userLastLoginTimeEnd;
    }

    /**
     * 获取截止查询条件
     */
    public Date getUserLastLoginTimeEnd() {
        return this.userLastLoginTimeEnd;
    }

}