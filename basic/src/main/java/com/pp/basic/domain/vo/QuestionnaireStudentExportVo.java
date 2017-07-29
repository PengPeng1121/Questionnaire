/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain.vo;

import com.pp.common.core.AbstractEntity;

import java.util.Date;

/**
 * 学生问卷调查关系表数据实体
 * 
 * @author
 */
public final class QuestionnaireStudentExportVo {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 问卷编号
    private String questionnaireCode;

    // 问卷名称
    private String questionnaireName;

    // 学号(回答者编号)
    private String studentCode;

    // 姓名(回答者姓名)
    private String studentName;

    private String lessonName;

    // 创建时间
    private Date createTime;

    // 提醒时间
    private Date remindTime;
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

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(Date remindTime) {
        this.remindTime = remindTime;
    }
}