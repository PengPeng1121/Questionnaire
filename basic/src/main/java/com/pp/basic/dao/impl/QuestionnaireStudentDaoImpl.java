/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.dao.impl;

import com.pp.basic.domain.vo.QuestionnaireStudentExportVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import com.pp.basic.dao.QuestionnaireStudentDao;
import com.pp.basic.domain.QuestionnaireStudent;
import com.pp.common.core.dao.AbstractDemoDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生问卷调查关系表DAO接口实现
 * 
 * @author
 */
@Repository
public class QuestionnaireStudentDaoImpl extends AbstractDemoDao<QuestionnaireStudent> implements QuestionnaireStudentDao {

    @Override
    public List<QuestionnaireStudentExportVo> exportStudentUnDoInfo(String questionnaireCode) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("questionnaireCode",questionnaireCode);
        String statement = this.getStatement("exportStudentUnDoInfo");
        // 执行统计语句
        List<QuestionnaireStudentExportVo> studentExportVos = this.getSqlSession().selectList(statement, parameterMap);
        if (CollectionUtils.isNotEmpty(studentExportVos)){
            return studentExportVos;
        }
        return null;
    }
}