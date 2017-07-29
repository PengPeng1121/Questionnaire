/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 学生问卷调查关系表数据实体
 * 
 * @author
 */
public final class QuestionnaireStudent extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    public static final String PROCESS_CODE_UNDO="0";

    public static final String PROCESS_CODE_DOING="1";

    public static final String PROCESS_CODE_DONE="2";

    public static final String PROCESS_NAME_UNDO="未答";

    public static final String PROCESS_NAME_DOING="进行中";

    public static final String PROCESS_NAME_DONE="答完";

    // 问卷编号
    private String questionnaireCode;

    // 问卷名称
    private String questionnaireName;

    // 问卷进度 0：未答；1：进行中：2：答完
    private String questionnaireProcessStatusCode;

    // 问卷进度 未答；进行中：答完
    private String questionnaireProcessStatusName;

    // 学号(回答者编号)
    private String studentCode;

    // 姓名(回答者姓名)
    private String studentName;

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
     * 设置问卷进度 0：未答；1：进行中：2：答完
     * 
     * @param questionnaireProcessStatusCode 问卷进度 0：未答；1：进行中：2：答完
     */
    public void setQuestionnaireProcessStatusCode(String questionnaireProcessStatusCode) {
        this.questionnaireProcessStatusCode = questionnaireProcessStatusCode;
    }

    /**
     * 获取问卷进度 0：未答；1：进行中：2：答完
     */
    public String getQuestionnaireProcessStatusCode() {
        return this.questionnaireProcessStatusCode;
    }

    /**
     * 设置问卷进度 未答；进行中：答完
     * 
     * @param questionnaireProcessStatusName 问卷进度 未答；进行中：答完
     */
    public void setQuestionnaireProcessStatusName(String questionnaireProcessStatusName) {
        this.questionnaireProcessStatusName = questionnaireProcessStatusName;
    }

    /**
     * 获取问卷进度 未答；进行中：答完
     */
    public String getQuestionnaireProcessStatusName() {
        return this.questionnaireProcessStatusName;
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

}