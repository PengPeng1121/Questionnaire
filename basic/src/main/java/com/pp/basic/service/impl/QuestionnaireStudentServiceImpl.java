/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.service.impl;

import com.pp.basic.domain.vo.QuestionnaireStudentExportVo;
import com.pp.basic.manager.QuestionnaireStudentManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pp.common.core.service.AbstractGenericService;
import com.pp.basic.domain.QuestionnaireStudent;
import com.pp.basic.service.QuestionnaireStudentService;

import java.util.List;

/**
 * 学生问卷调查关系表Service接口实现
 * 
 * @author
 */
@Service
public class QuestionnaireStudentServiceImpl extends AbstractGenericService<QuestionnaireStudent> implements QuestionnaireStudentService {

    @Autowired
    QuestionnaireStudentManager questionnaireStudentManager;

    @Override
    public List<QuestionnaireStudentExportVo> exportStudentUnDoInfo(String questionnaireCode) {
        return questionnaireStudentManager.exportStudentUnDoInfo(questionnaireCode);
    }
}