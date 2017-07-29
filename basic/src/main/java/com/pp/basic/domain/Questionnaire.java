/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 文卷调查信息表数据实体
 * 
 * @author
 */
public final class Questionnaire extends AbstractEntity {

    public static final String CODE_INIT="0";

    public static final String ALREADY_WITH_LESSON_CODE="1";

    public static final String ALREADY_WITH_STUDENT_CODE="2";

    public static final String NAME_INIT="初始";

    public static final String ALREADY_WITH_LESSON_NAME="已关联课程";

    public static final String ALREADY_WITH_STUDENT_NAME="已推送学生";

    // 序列化
    private static final long serialVersionUID = 1L;

    // 问卷编号
    private String questionnaireCode;

    // 问卷名称
    private String questionnaireName;

    // 问卷状态编号（0:初始;1:已关联课程；2:已推送学生）
    private String questionnaireStatusCode;

    // 问卷状态名称（初始;已关联课程；已推送学生）
    private String questionnaireStatusName;

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
     * 设置问卷状态编号（0:初始;1:已关联课程；2:已推送学生）
     * 
     * @param questionnaireStatusCode 问卷状态编号（0:初始;1:已关联课程；2:已推送学生）
     */
    public void setQuestionnaireStatusCode(String questionnaireStatusCode) {
        this.questionnaireStatusCode = questionnaireStatusCode;
    }

    /**
     * 获取问卷状态编号（0:初始;1:已关联课程；2:已推送学生）
     */
    public String getQuestionnaireStatusCode() {
        return this.questionnaireStatusCode;
    }

    /**
     * 设置问卷状态名称（初始;已关联课程；已推送学生）
     * 
     * @param questionnaireStatusName 问卷状态名称（初始;已关联课程；已推送学生）
     */
    public void setQuestionnaireStatusName(String questionnaireStatusName) {
        this.questionnaireStatusName = questionnaireStatusName;
    }

    /**
     * 获取问卷状态名称（初始;已关联课程；已推送学生）
     */
    public String getQuestionnaireStatusName() {
        return this.questionnaireStatusName;
    }

}