package com.pp.basic.domain.vo;

/**
 * Created by asus on 2017/5/22.
 */
public class InitLessonFail {
    // 课程编码
    private String lessonCode;

    // 失败原因
    private String failReason;

    public String getLessonCode() {
        return lessonCode;
    }

    public void setLessonCode(String lessonCode) {
        this.lessonCode = lessonCode;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }
}
