/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 选项组表数据实体
 * 
 * @author
 */
public final class AnswerGroup extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 选项组编号
    private String groupCode;

    // 选项组名称
    private String groupName;

    // 答案
    private String answer;

    // 答案值
    private String answerValue;

    // 答案得分
    private Integer answerScore;

    /**
     * 设置选项组编号
     * 
     * @param groupCode 选项组编号
     */
    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    /**
     * 获取选项组编号
     */
    public String getGroupCode() {
        return this.groupCode;
    }

    /**
     * 设置选项组名称
     * 
     * @param groupName 选项组名称
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * 获取选项组名称
     */
    public String getGroupName() {
        return this.groupName;
    }

    /**
     * 设置答案
     * 
     * @param answer 答案
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * 获取答案
     */
    public String getAnswer() {
        return this.answer;
    }

    /**
     * 设置答案值
     * 
     * @param answerValue 答案值
     */
    public void setAnswerValue(String answerValue) {
        this.answerValue = answerValue;
    }

    /**
     * 获取答案值
     */
    public String getAnswerValue() {
        return this.answerValue;
    }

    /**
     * 设置答案得分
     * 
     * @param answerScore 答案得分
     */
    public void setAnswerScore(Integer answerScore) {
        this.answerScore = answerScore;
    }

    /**
     * 获取答案得分
     */
    public Integer getAnswerScore() {
        return this.answerScore;
    }

}