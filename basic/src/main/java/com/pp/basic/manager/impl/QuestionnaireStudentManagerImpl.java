/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.manager.impl;

import com.pp.basic.dao.QuestionnaireStudentDao;
import com.pp.basic.domain.vo.QuestionnaireInfoVo;
import com.pp.basic.domain.vo.QuestionnaireStudentExportVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pp.common.core.manager.AbstractGenericManager;
import com.pp.basic.domain.QuestionnaireStudent;
import com.pp.basic.manager.QuestionnaireStudentManager;

import java.util.HashMap;
import java.util.List;

/**
 * 学生问卷调查关系表Manager接口实现
 * 
 * @author
 */
@Service
public class QuestionnaireStudentManagerImpl extends AbstractGenericManager<QuestionnaireStudent> implements QuestionnaireStudentManager {

    @Autowired
    QuestionnaireStudentDao questionnaireStudentDao;

    @Override
    public List<QuestionnaireStudentExportVo> exportStudentUnDoInfo(String questionnaireCode) {
        return questionnaireStudentDao.exportStudentUnDoInfo(questionnaireCode);
    }

    @Override
    public List<QuestionnaireInfoVo> showStudentQuestionnaire(HashMap<String ,Object> map, Integer rowOffset, Integer rowLimit) {
        return questionnaireStudentDao.showStudentQuestionnaire(map, rowOffset, rowLimit);
    }

    @Override
    public Long showStudentQuestionnaireCount(HashMap<String ,Object> map) {
        return questionnaireStudentDao.showStudentQuestionnaireCount(map);
    }
}