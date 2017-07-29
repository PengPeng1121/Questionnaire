package com.pp.basic.domain.vo;

/**
 * Created by asus on 2017/5/22.
 */
public class InitStudentLessonFail {
    // 学号
    private String studentCode;

    // 学号
    private String lessonCode;

    // 失败原因
    private String failReason;

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public String getLessonCode() {
        return lessonCode;
    }

    public void setLessonCode(String lessonCode) {
        this.lessonCode = lessonCode;
    }
}
