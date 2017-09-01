package com.pp.basic.domain.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by asus on 2017/8/16.
 */
public class QuestionnaireInfoVo implements Serializable {
    // 序列化
    private static final long serialVersionUID = 1L;

    // 课程名称
    private String lessonName;

    // 问卷编号
    private String questionnaireCode;

    // 问卷名称
    private String questionnaireName;

    // 教师姓名
    private String teacherName;

    private Date questionnaireEndTime;

    private String questionnaireProcessStatusName;

    public String getQuestionnaireProcessStatusName() {
        return questionnaireProcessStatusName;
    }

    public void setQuestionnaireProcessStatusName(String questionnaireProcessStatusName) {
        this.questionnaireProcessStatusName = questionnaireProcessStatusName;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getQuestionnaireCode() {
        return questionnaireCode;
    }

    public void setQuestionnaireCode(String questionnaireCode) {
        this.questionnaireCode = questionnaireCode;
    }

    public String getQuestionnaireName() {
        return questionnaireName;
    }

    public void setQuestionnaireName(String questionnaireName) {
        this.questionnaireName = questionnaireName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public Date getQuestionnaireEndTime() {
        return questionnaireEndTime;
    }

    public void setQuestionnaireEndTime(Date questionnaireEndTime) {
        this.questionnaireEndTime = questionnaireEndTime;
    }
}
