/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 问卷问题模板表数据实体
 * 
 * @author
 */
public final class QuestionnaireQuestionTemplate extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 模板编号
    private String templateCode;

    // 模板名称
    private String templateName;

    // 问题编号
    private String questionCode;

    // 问题描述
    private String questionName;

    // 问题类型代码 0：选择题；1：简答题
    private String questionTypeCode;

    // 问题类型名称 选择题 ；简答题
    private String questionTypeName;

    // 是否必答题 0 ：否 ；1是
    private Integer isMustAnswer;

    // 问题选项组
    private String answerGroup;

    /**
     * 设置模板编号
     * 
     * @param templateCode 模板编号
     */
    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    /**
     * 获取模板编号
     */
    public String getTemplateCode() {
        return this.templateCode;
    }

    /**
     * 设置模板名称
     * 
     * @param templateName 模板名称
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * 获取模板名称
     */
    public String getTemplateName() {
        return this.templateName;
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
     * 设置问题描述
     * 
     * @param questionName 问题描述
     */
    public void setQuestionName(String questionName) {
        this.questionName = questionName;
    }

    /**
     * 获取问题描述
     */
    public String getQuestionName() {
        return this.questionName;
    }

    /**
     * 设置问题类型代码 0：选择题；1：简答题
     * 
     * @param questionTypeCode 问题类型代码 0：选择题；1：简答题
     */
    public void setQuestionTypeCode(String questionTypeCode) {
        this.questionTypeCode = questionTypeCode;
    }

    /**
     * 获取问题类型代码 0：选择题；1：简答题
     */
    public String getQuestionTypeCode() {
        return this.questionTypeCode;
    }

    /**
     * 设置问题类型名称 选择题 ；简答题
     * 
     * @param questionTypeName 问题类型名称 选择题 ；简答题
     */
    public void setQuestionTypeName(String questionTypeName) {
        this.questionTypeName = questionTypeName;
    }

    /**
     * 获取问题类型名称 选择题 ；简答题
     */
    public String getQuestionTypeName() {
        return this.questionTypeName;
    }

    /**
     * 设置是否必答题 0 ：否 ；1是
     * 
     * @param isMustAnswer 是否必答题 0 ：否 ；1是
     */
    public void setIsMustAnswer(Integer isMustAnswer) {
        this.isMustAnswer = isMustAnswer;
    }

    /**
     * 获取是否必答题 0 ：否 ；1是
     */
    public Integer getIsMustAnswer() {
        return this.isMustAnswer;
    }

    /**
     * 设置问题选项组
     * 
     * @param answerGroup 问题选项组
     */
    public void setAnswerGroup(String answerGroup) {
        this.answerGroup = answerGroup;
    }

    /**
     * 获取问题选项组
     */
    public String getAnswerGroup() {
        return this.answerGroup;
    }

}