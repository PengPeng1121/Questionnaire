/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import java.util.Date;

import com.pp.common.annotation.Transient;
import com.pp.common.core.AbstractEntity;

/**
 * 公告管理表数据实体
 * 
 * @author
 */
public final class Notice extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 公告内容
    private String noticeContent;

    // 过期时间
    private Date expireTime;

    // 过期时间开始查询条件
    @Transient
    private transient Date expireTimeBegin;

    // 过期时间截止查询条件
    @Transient
    private transient Date expireTimeEnd;

    /**
     * 设置公告内容
     * 
     * @param noticeContent 公告内容
     */
    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

    /**
     * 获取公告内容
     */
    public String getNoticeContent() {
        return this.noticeContent;
    }

    /**
     * 设置过期时间
     * 
     * @param expireTime 过期时间
     */
    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    /**
     * 获取过期时间
     */
    public Date getExpireTime() {
        return this.expireTime;
    }

    /**
     * 设置过期时间开始查询条件
     * 
     * @param expireTimeBegin 过期时间开始查询条件
     */
    public void setExpireTimeBegin(Date expireTimeBegin) {
        this.expireTimeBegin = expireTimeBegin;
    }

    /**
     * 获取过期时间开始查询条件
     */
    public Date getExpireTimeBegin() {
        return this.expireTimeBegin;
    }

    /**
     * 设置过期时间截止查询条件
     * 
     * @param expireTimeEnd 过期时间截止查询条件
     */
    public void setExpireTimeEnd(Date expireTimeEnd) {
        this.expireTimeEnd = expireTimeEnd;
    }

    /**
     * 获取过期时间截止查询条件
     */
    public Date getExpireTimeEnd() {
        return this.expireTimeEnd;
    }

}