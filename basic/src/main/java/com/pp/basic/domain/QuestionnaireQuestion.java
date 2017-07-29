/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 文卷调查问题信息表数据实体
 *
 * @author
 */
public final class QuestionnaireQuestion extends AbstractEntity {

    public static final String QUESTION_TYPE_CODE_CHOICE ="0";

    public static final String QUESTION_TYPE_CODE_DESC ="1";

    public static final String QUESTION_TYPE_NAME_CHOICE ="选择题";

    public static final String QUESTION_TYPE_NAME_DESC ="简答题";

    public static final Integer IS_MUST_ANSWER =1;

    public static final Integer IS_NOT_MUST_ANSWER =0;
    // 序列化
    private static final long serialVersionUID = 1L;

    // 问卷编号
    private String questionnaireCode;

    // 问卷名称
    private String questionnaireName;

    // 问题编号
    private String questionCode;

    // 问题
    private String questionName;

    // 问题类型代码 0：选择题；1：简答题
    private String questionTypeCode;

    // 问题类型名称 选择题 ；简答题
    private String questionTypeName;

    // 是否必答题 0 ：否 ；1是
    private Integer isMustAnswer;

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

}