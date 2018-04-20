/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.domain;

import com.pp.common.core.AbstractEntity;

/**
 * 问卷模板表数据实体
 * 
 * @author
 */
public final class QuestionnaireTemplate extends AbstractEntity {

    // 序列化
    private static final long serialVersionUID = 1L;

    // 模板编号
    private String templateCode;

    // 模板名称
    private String templateName;

    /**
     * 设置模板编号
     * 
     * @param templateCode 模板编号
     */
    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    /**
     * 获取模板编号
     */
    public String getTemplateCode() {
        return this.templateCode;
    }

    /**
     * 设置模板名称
     * 
     * @param templateName 模板名称
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * 获取模板名称
     */
    public String getTemplateName() {
        return this.templateName;
    }

}