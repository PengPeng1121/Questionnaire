/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 课程对照课程表数据实体
 * 
 * @author
 */
public final class LessonContrast extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 课程代码
    private String lessonCode;

    // 课程代码(东大)
    private String lessonCodeNeu;

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
     * 设置课程代码(东大)
     * 
     * @param lessonCodeNeu 课程代码(东大)
     */
    public void setLessonCodeNeu(String lessonCodeNeu) {
        this.lessonCodeNeu = lessonCodeNeu;
    }

    /**
     * 获取课程代码(东大)
     */
    public String getLessonCodeNeu() {
        return this.lessonCodeNeu;
    }

}