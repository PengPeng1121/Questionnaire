/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 学生选课信息表数据实体
 * 
 * @author
 */
public final class StudentLesson extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 学号
    private String studentCode;

    // 姓名
    private String studentName;

    // 课程代码
    private String lessonCode;

    // 课程名称
    private String lessonName;

    //学期
    private String term;

    /**
     * 设置学号
     * 
     * @param studentCode 学号
     */
    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    /**
     * 获取学号
     */
    public String getStudentCode() {
        return this.studentCode;
    }

    /**
     * 设置姓名
     * 
     * @param studentName 姓名
     */
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    /**
     * 获取姓名
     */
    public String getStudentName() {
        return this.studentName;
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