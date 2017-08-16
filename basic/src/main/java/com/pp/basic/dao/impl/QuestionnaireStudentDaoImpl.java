/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.dao.impl;

import com.google.gson.Gson;
import com.pp.basic.domain.vo.QuestionnaireInfoVo;
import com.pp.basic.domain.vo.QuestionnaireStudentExportVo;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger log = LoggerFactory.getLogger(QuestionnaireStudentDaoImpl.class.getName());

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

    @Override
    public List<QuestionnaireInfoVo> showStudentQuestionnaire(HashMap<String ,Object> parameterMap, Integer rowOffset, Integer rowLimit) {
        // 设置分页参数
        if (rowOffset != null && rowLimit != null) {
            if (rowOffset < 0) {
                throw new IllegalArgumentException("参数异常：参数rowOffset不能小于0");
            }
            if (rowLimit < 1) {
                throw new IllegalArgumentException("参数异常：参数rowLimit不能小于1");
            }

            RowBounds rowBounds = new RowBounds(rowOffset, rowLimit);
            parameterMap.put(KEY_PAGE, rowBounds);
        } else {
            //最大1000条
            // 当没有设置分页时，硬编码限制返回超过最大值的结果集，用于触发异常
            RowBounds rowBounds = new RowBounds(0, MAX_RESULT + 1);
            parameterMap.put(KEY_PAGE, rowBounds);
        }
        // 获取按条件查询的语句
        String statement = this.getStatement("showStudentQuestionnaire");
        // 执行按条件查询的语句
        List<QuestionnaireInfoVo> resultList = this.getSqlSession().selectList(statement, parameterMap);
        // 判断结果集是否过大
        if (resultList != null && resultList.size() > MAX_RESULT) {
            String param = new Gson().toJson(parameterMap);
            log.warn("查询结果集过大：当前数量" + resultList.size() + "，建议设置合理的分页排序参数[SQL:" + statement + ";PARAM:" + param + "]");
        }
        // 返回查询结果
        return resultList;
    }

    @Override
    public Long showStudentQuestionnaireCount(HashMap<String ,Object> parameterMap) {
        // 获取统计语句
        String statement = this.getStatement("countByConditionUnion");
        // 执行统计语句
        Long count = this.getSqlSession().selectOne(statement, parameterMap);
        // 返回结果
        if (count != null) {
            return count.longValue();
        }
        return 0L;
    }
}