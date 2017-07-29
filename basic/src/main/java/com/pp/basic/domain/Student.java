/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 学生信息表数据实体
 * 
 * @author
 */
public final class Student extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 学号
    private String studentCode;

    // 姓名
    private String studentName;

    // 学生年级
    private String studentGrade;

    // 学生班级
    private String studentClass;

    // 学生是否毕业（0:否;1:是）
    private Integer isStudentGraduate;

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
     * 设置学生年级
     * 
     * @param studentGrade 学生年级
     */
    public void setStudentGrade(String studentGrade) {
        this.studentGrade = studentGrade;
    }

    /**
     * 获取学生年级
     */
    public String getStudentGrade() {
        return this.studentGrade;
    }

    /**
     * 设置学生班级
     * 
     * @param studentClass 学生班级
     */
    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    /**
     * 获取学生班级
     */
    public String getStudentClass() {
        return this.studentClass;
    }

    /**
     * 设置学生是否毕业（0:否;1:是）
     * 
     * @param isStudentGraduate 学生是否毕业（0:否;1:是）
     */
    public void setIsStudentGraduate(Integer isStudentGraduate) {
        this.isStudentGraduate = isStudentGraduate;
    }

    /**
     * 获取学生是否毕业（0:否;1:是）
     */
    public Integer getIsStudentGraduate() {
        return this.isStudentGraduate;
    }

}