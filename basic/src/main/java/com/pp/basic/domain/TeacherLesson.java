/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 教师授课信息表数据实体
 * 
 * @author
 */
public final class TeacherLesson extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 教师编号
    private String teacherCode;

    // 教师姓名
    private String teacherName;

    // 课程代码
    private String lessonCode;

    // 课程名称
    private String lessonName;

    /**
     * 设置教师编号
     * 
     * @param teacherCode 教师编号
     */
    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    /**
     * 获取教师编号
     */
    public String getTeacherCode() {
        return this.teacherCode;
    }

    /**
     * 设置教师姓名
     * 
     * @param teacherName 教师姓名
     */
    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    /**
     * 获取教师姓名
     */
    public String getTeacherName() {
        return this.teacherName;
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

}