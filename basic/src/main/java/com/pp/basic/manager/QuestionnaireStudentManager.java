/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.manager;

import com.pp.basic.domain.QuestionnaireStudent;
import com.pp.basic.domain.vo.QuestionnaireInfoVo;
import com.pp.basic.domain.vo.QuestionnaireStudentExportVo;
import com.pp.common.core.manager.IManager;

import java.util.HashMap;
import java.util.List;

/**
 * 学生问卷调查关系表Manager接口
 * 
 * @author
 */
public interface QuestionnaireStudentManager extends IManager<QuestionnaireStudent> {

    //导出
    List<QuestionnaireStudentExportVo> exportStudentUnDoInfo(String questionnaireCode);

    //查询详细
    public List<QuestionnaireInfoVo> showStudentQuestionnaire(HashMap<String ,Object> map, Integer rowOffset, Integer rowLimit);

    //查询详细个数
    public Long showStudentQuestionnaireCount(HashMap<String ,Object> map);
}