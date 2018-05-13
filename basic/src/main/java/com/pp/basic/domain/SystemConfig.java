/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import java.util.Date;

import com.pp.common.annotation.Transient;
import com.pp.common.core.AbstractEntity;

/**
 * 系统配置数据实体
 * 
 * @author
 */
public final class SystemConfig extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 问卷编号
    private String questionnaireCode;

    // 问卷名称
    private String questionnaireName;

    // 提醒时间
    private Date remindTime;

    //截止时间
    @Transient
    private Date endTime;

    // 提醒时间开始查询条件
    private transient Date remindTimeBegin;

    // 提醒时间截止查询条件
    private transient Date remindTimeEnd;


    /**
     * 设置问卷编号
     * 
     * @param questionnaireCode 问卷编号
     */
    public void setQuestionnaireCode(String questionnaireCode) {
        this.questionnaireCode = questionnaireCode;
    }

    /**
     * 获取问卷编号
     */
    public String getQuestionnaireCode() {
        return this.questionnaireCode;
    }

    /**
     * 设置问卷名称
     * 
     * @param questionnaireName 问卷名称
     */
    public void setQuestionnaireName(String questionnaireName) {
        this.questionnaireName = questionnaireName;
    }

    /**
     * 获取问卷名称
     */
    public String getQuestionnaireName() {
        return this.questionnaireName;
    }

    /**
     * 设置提醒时间
     * 
     * @param remindTime 提醒时间
     */
    public void setRemindTime(Date remindTime) {
        this.remindTime = remindTime;
    }

    /**
     * 获取提醒时间
     */
    public Date getRemindTime() {
        return this.remindTime;
    }

    /**
     * 设置提醒时间开始查询条件
     * 
     * @param remindTimeBegin 提醒时间开始查询条件
     */
    public void setRemindTimeBegin(Date remindTimeBegin) {
        this.remindTimeBegin = remindTimeBegin;
    }

    /**
     * 获取提醒时间开始查询条件
     */
    public Date getRemindTimeBegin() {
        return this.remindTimeBegin;
    }

    /**
     * 设置提醒时间截止查询条件
     * 
     * @param remindTimeEnd 提醒时间截止查询条件
     */
    public void setRemindTimeEnd(Date remindTimeEnd) {
        this.remindTimeEnd = remindTimeEnd;
    }

    /**
     * 获取提醒时间截止查询条件
     */
    public Date getRemindTimeEnd() {
        return this.remindTimeEnd;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}