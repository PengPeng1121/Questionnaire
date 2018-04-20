/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.service;

import com.pp.basic.domain.*;
import com.pp.common.core.service.IService;

import java.util.List;

/**
 * 文卷调查问题信息表Service接口
 *
 * @author
 */
public interface QuestionnaireQuestionService extends IService<QuestionnaireQuestion> {

    //保存 控制事物
    public void save(Questionnaire questionnaire, QuestionnaireScore questionnaireScore,
                     QuestionnaireLesson questionnaireLesson, List<QuestionnaireQuestion> questionnaireQuestionList,
                     List<QuestionnaireStudent> questionnaireStudentList, String userCode);
}