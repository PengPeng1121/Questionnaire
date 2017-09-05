/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 文卷和课程关系调查信息表数据实体
 * 
 * @author
 */
public final class QuestionnaireLesson extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 问卷编号
    private String questionnaireCode;

    // 问卷名称
    private String questionnaireName;

    // 课程代码
    private String lessonCode;

    // 课程名称
    private String lessonName;

    //学期
    private String term;

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
     * 设置课程代码
     * 
     * @param lessonCode 课程代码
     */
    public void setLessonCode(String lessonCode) {
        this.lessonCode = lessonCode;
    }

    /**
     * 获取课程代码
     */
    public String getLessonCode() {
        return this.lessonCode;
    }

    /**
     * 设置课程名称
     * 
     * @param lessonName 课程名称
     */
    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    /**
     * 获取课程名称
     */
    public String getLessonName() {
        return this.lessonName;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}