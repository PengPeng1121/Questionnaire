/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 教师信息表数据实体
 * 
 * @author
 */
public final class Teacher extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 教师编号
    private String teacherCode;

    // 教师姓名
    private String teacherName;

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

}