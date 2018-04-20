/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.service;

import com.pp.basic.domain.QuestionnaireQuestion;
import com.pp.basic.domain.QuestionnaireQuestionTemplate;
import com.pp.basic.domain.QuestionnaireTemplate;
import com.pp.common.core.service.IService;

import java.util.List;

/**
 * 问卷模板表Service接口
 * 
 * @author
 */
public interface QuestionnaireTemplateService extends IService<QuestionnaireTemplate> {

    public void saveTemplate(QuestionnaireTemplate questionnaireTemplate,List<QuestionnaireQuestionTemplate> questionTemplates,String user);

}