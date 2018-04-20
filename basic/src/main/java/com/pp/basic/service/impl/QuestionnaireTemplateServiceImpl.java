/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.service.impl;

import com.pp.basic.domain.QuestionnaireQuestionTemplate;
import com.pp.basic.manager.QuestionnaireStudentManager;
import com.pp.basic.manager.QuestionnaireTemplateManager;
import com.pp.basic.service.QuestionnaireQuestionTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pp.basic.domain.QuestionnaireTemplate;
import com.pp.basic.service.QuestionnaireTemplateService;
import com.pp.common.core.service.AbstractGenericService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 问卷模板表Service接口实现
 *
 * @author
 */
@Service
public class QuestionnaireTemplateServiceImpl extends AbstractGenericService<QuestionnaireTemplate> implements QuestionnaireTemplateService {

    @Autowired
    QuestionnaireTemplateManager questionnaireTemplateManager;

    @Autowired
    QuestionnaireQuestionTemplateService questionnaireQuestionTemplateService;

    @Override
    @Transactional
    public void saveTemplate(QuestionnaireTemplate questionnaireTemplate, List<QuestionnaireQuestionTemplate> questionTemplates,String user) {
        questionnaireTemplate.setCreateTime(new Date());
        questionnaireTemplate.setCreateUser(user);
        questionnaireTemplate.setUpdateTime(new Date());
        questionnaireTemplate.setUpdateUser(user);
        questionnaireTemplateManager.insert(questionnaireTemplate);
        questionnaireQuestionTemplateService.insert(questionTemplates,user);
    }

}