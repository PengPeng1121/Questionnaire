/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.service.impl;

import com.pp.basic.domain.*;
import com.pp.basic.manager.QuestionnaireManager;
import com.pp.basic.manager.QuestionnaireQuestionManager;
import com.pp.basic.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pp.common.core.service.AbstractGenericService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 文卷调查问题信息表Service接口实现
 * 
 * @author
 */
@Service
public class QuestionnaireQuestionServiceImpl extends AbstractGenericService<QuestionnaireQuestion> implements QuestionnaireQuestionService {

    @Autowired
    QuestionnaireService questionnaireService;

    @Autowired
    QuestionnaireQuestionManager questionnaireQuestionManager;

    @Autowired
    QuestionnaireLessonService questionnaireLessonService;

    @Autowired
    QuestionnaireStudentService questionnaireStudentService;

    @Autowired
    QuestionnaireScoreService questionnaireScoreService;


    @Override
    @Transactional
    public void save(Questionnaire questionnaire, QuestionnaireScore questionnaireScore,
                     QuestionnaireLesson questionnaireLesson, List<QuestionnaireQuestion> questionnaireQuestionList,
                     List<QuestionnaireStudent> questionnaireStudentList, String userCode) {

        this.questionnaireService.insert(questionnaire,userCode);
        for(QuestionnaireQuestion questionnaireQuestion:questionnaireQuestionList){
            //写数据库
            questionnaireQuestion.setCreateTime(new Date());
            questionnaireQuestion.setUpdateTime(new Date());
            questionnaireQuestion.setCreateUser(userCode);
            questionnaireQuestion.setUpdateUser(userCode);
        }
        this.questionnaireQuestionManager.insert(questionnaireQuestionList);
        this.questionnaireScoreService.insert(questionnaireScore,userCode);
        this.questionnaireLessonService.insert(questionnaireLesson,userCode);
        this.questionnaireStudentService.insert(questionnaireStudentList,userCode);
    }
}