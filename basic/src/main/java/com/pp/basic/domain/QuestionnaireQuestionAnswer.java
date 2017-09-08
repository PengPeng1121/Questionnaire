/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 文卷调查问题答案信息表数据实体
 * 
 * @author
 */
public final class QuestionnaireQuestionAnswer extends AbstractEntity {

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

    // 问题编号
    private String answerCode;

    // 答案
    private String answer;

    // 答案值
    private String answerValue;

    // 学号(回答者编号)
    private String studentCode;

    // 姓名(回答者姓名)
    private String studentName;

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
     * 设置问题编号
     * 
     * @param answerCode 问题编号
     */
    public void setAnswerCode(String answerCode) {
        this.answerCode = answerCode;
    }

    /**
     * 获取问题编号
     */
    public String getAnswerCode() {
        return this.answerCode;
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
     * 设置学号(回答者编号)
     * 
     * @param studentCode 学号(回答者编号)
     */
    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    /**
     * 获取学号(回答者编号)
     */
    public String getStudentCode() {
        return this.studentCode;
    }

    /**
     * 设置姓名(回答者姓名)
     * 
     * @param studentName 姓名(回答者姓名)
     */
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    /**
     * 获取姓名(回答者姓名)
     */
    public String getStudentName() {
        return this.studentName;
    }

    public String getAnswerValue() {
        return answerValue;
    }

    public void setAnswerValue(String answerValue) {
        this.answerValue = answerValue;
    }

    public String getQuestionTypeCode() {
        return questionTypeCode;
    }

    public void setQuestionTypeCode(String questionTypeCode) {
        this.questionTypeCode = questionTypeCode;
    }

    public String getQuestionTypeName() {
        return questionTypeName;
    }

    public void setQuestionTypeName(String questionTypeName) {
        this.questionTypeName = questionTypeName;
    }

    public Integer getIsMustAnswer() {
        return isMustAnswer;
    }

    public void setIsMustAnswer(Integer isMustAnswer) {
        this.isMustAnswer = isMustAnswer;
    }
}