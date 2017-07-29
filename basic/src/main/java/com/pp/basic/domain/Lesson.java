/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 课程信息表数据实体
 * 
 * @author
 */
public final class Lesson extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 课程代码
    private String lessonCode;

    // 课程名称
    private String lessonName;

    // 课程类型代码 0：实践课；1：理论课
    private String lessonTypeCode;

    // 课程类型名称 实践课 ；理论课
    private String lessonTypeName;

    // 授课教师编码
    private String lessonTeacherCode;

    // 授课教师名称
    private String lessonTeacherName;

    //学期
    private String term;
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

    /**
     * 设置课程类型代码 0：实践课；1：理论课
     * 
     * @param lessonTypeCode 课程类型代码 0：实践课；1：理论课
     */
    public void setLessonTypeCode(String lessonTypeCode) {
        this.lessonTypeCode = lessonTypeCode;
    }

    /**
     * 获取课程类型代码 0：实践课；1：理论课
     */
    public String getLessonTypeCode() {
        return this.lessonTypeCode;
    }

    /**
     * 设置课程类型名称 实践课 ；理论课
     * 
     * @param lessonTypeName 课程类型名称 实践课 ；理论课
     */
    public void setLessonTypeName(String lessonTypeName) {
        this.lessonTypeName = lessonTypeName;
    }

    /**
     * 获取课程类型名称 实践课 ；理论课
     */
    public String getLessonTypeName() {
        return this.lessonTypeName;
    }

    /**
     * 设置授课教师编码
     * 
     * @param lessonTeacherCode 授课教师编码
     */
    public void setLessonTeacherCode(String lessonTeacherCode) {
        this.lessonTeacherCode = lessonTeacherCode;
    }

    /**
     * 获取授课教师编码
     */
    public String getLessonTeacherCode() {
        return this.lessonTeacherCode;
    }

    /**
     * 设置授课教师名称
     * 
     * @param lessonTeacherName 授课教师名称
     */
    public void setLessonTeacherName(String lessonTeacherName) {
        this.lessonTeacherName = lessonTeacherName;
    }

    /**
     * 获取授课教师名称
     */
    public String getLessonTeacherName() {
        return this.lessonTeacherName;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}