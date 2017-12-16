/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.service.impl;

import com.pp.basic.domain.vo.QuestionnaireInfoVo;
import com.pp.basic.domain.vo.QuestionnaireStudentExportVo;
import com.pp.basic.manager.QuestionnaireStudentManager;
import com.pp.common.core.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pp.common.core.service.AbstractGenericService;
import com.pp.basic.domain.QuestionnaireStudent;
import com.pp.basic.service.QuestionnaireStudentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public Page<QuestionnaireInfoVo> showStudentQuestionnaire(HashMap<String ,Object> map, Page<QuestionnaireInfoVo> page) {
        if (page == null) {
            throw new IllegalArgumentException("参数page不能为null");
        }

        int pageIndex = page.getPageIndex();
        int pageSize = page.getPageSize();

        int rowOffset = pageIndex  * pageSize;
        int rowLimit = (pageIndex+1) * pageSize;

        long count = questionnaireStudentManager.showStudentQuestionnaireCount(map);

        List<QuestionnaireInfoVo> resultList = questionnaireStudentManager.showStudentQuestionnaire(map, rowOffset, rowLimit);

        return new Page<QuestionnaireInfoVo>(resultList, count, pageIndex, pageSize);
    }


}