/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain.vo;

import com.pp.common.core.AbstractEntity;

/**
 * 学生回答问题
 * 
 * @author
 */
public final class Answer extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 问卷编号
    private String questionnaireCode;

    // 问题编号
    private String questionCode;

    // 问题
    private String questionName;

    // 答案
    private String answer;


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
     * 设置问题编号
     * 
     * @param questionCode 问题编号
     */
    public void setQuestionCode(String questionCode) {
        this.questionCode = questionCode;
    }

    /**
     * 获取问题编号
     */
    public String getQuestionCode() {
        return this.questionCode;
    }

    /**
     * 设置问题
     * 
     * @param questionName 问题
     */
    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    /**
     * 获取问题
     */
    public String getQuestionName() {
        return this.questionName;
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

}