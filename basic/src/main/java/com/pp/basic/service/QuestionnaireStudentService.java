/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.service;

import com.pp.basic.domain.QuestionnaireStudent;
import com.pp.basic.domain.vo.QuestionnaireStudentExportVo;
import com.pp.common.core.service.IService;

import java.util.List;

/**
 * 学生问卷调查关系表Service接口
 * 
 * @author
 */
public interface QuestionnaireStudentService extends IService<QuestionnaireStudent> {

    //导出
    List<QuestionnaireStudentExportVo> exportStudentUnDoInfo(String questionnaireCode);
}