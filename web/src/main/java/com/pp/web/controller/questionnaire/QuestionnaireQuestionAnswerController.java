/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pp.basic.domain.Questionnaire;
import com.pp.basic.domain.vo.Answer;
import com.pp.basic.service.QuestionnaireService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pp.web.account.Account;
import com.pp.web.controller.until.AccountUtils;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import com.pp.web.controller.BaseController;
import com.pp.basic.domain.QuestionnaireQuestionAnswer;
import com.pp.basic.service.QuestionnaireQuestionAnswerService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 文卷调查问题答案信息表Controller
 *
 * @author
 */
@Controller
@RequestMapping("/web/questionnairequestionanswer")
public class QuestionnaireQuestionAnswerController extends BaseController {

    @Autowired
    QuestionnaireQuestionAnswerService questionnaireQuestionAnswerService;

    @Autowired
    QuestionnaireService questionnaireService;

    /**
     * 显示列表页面
     */
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public String listPage() {
        return "common/core/QuestionnaireQuestionAnswer/questionnaire_question_answer_list";
    }

    /**
     * 显示新增页面
     */
    @RequestMapping(value = "/addPage", method = RequestMethod.GET)
    public String addPage() {
        return "common/core/QuestionnaireQuestionAnswer/questionnaire_question_answer_add";
    }

    /**
     * 显示修改页面
     */
    @RequestMapping(value = "/editPage", method = RequestMethod.GET)
    public String editPage(Long id, Model model) {
        //TODO 数据验证
        return "common/core/QuestionnaireQuestionAnswer/questionnaire_question_answer_edit";
    }

    /**
     * 保存数据
     */
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public boolean insert(QuestionnaireQuestionAnswer questionnaireQuestionAnswer) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        this.questionnaireQuestionAnswerService.insert(questionnaireQuestionAnswer, account.getUserCode());
        return true;
    }

    /**
     * 修改数据
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public boolean update(QuestionnaireQuestionAnswer questionnaireQuestionAnswerUpdate) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        int rows = this.questionnaireQuestionAnswerService.update(questionnaireQuestionAnswerUpdate, account.getUserCode());
        if (rows == 1) {
            return true;
        }
        return false;
    }

    /**
     * 逻辑删除数据
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public boolean delete(Long id) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        int rows = this.questionnaireQuestionAnswerService.delete(id, account.getUserCode());
        if (rows == 1) {
            return true;
        }
        return false;
    }

    /**
     * 分页查询
     */
    @RequestMapping(value = "/pageQuery", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String,Object> pageQuery(QuestionnaireQuestionAnswer questionnaireQuestionAnswerQuery, @RequestParam(value = "page", required = false, defaultValue = "1") int pageNum, @RequestParam(value = "rows", required = false, defaultValue = "20") int pageSize, @RequestParam(value = "sidx", required = false, defaultValue = "ts") String sortName, @RequestParam(value = "sord", required = false, defaultValue = "desc") String sortOrder) {
        //TODO 数据验证

        // 设置合理的参数
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize < 1) {
            pageSize = 20;
        } else if (pageSize > 100) {
            pageSize = 100;
        }
        // 开始页码
        int pageIndex = pageNum - 1;
        // 排序
        Sort sort = null;
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = Sort.desc(sortName);
        } else {
            sort = Sort.asc(sortName);
        }
        // 创建分页对象
        Page<QuestionnaireQuestionAnswer> page = new Page<QuestionnaireQuestionAnswer>(pageIndex, pageSize, sort);
        // 执行查询
        page = this.questionnaireQuestionAnswerService.selectPage(questionnaireQuestionAnswerQuery, page);
        // 返回查询结果
        HashMap<String,Object> map = new HashMap<String,Object>();
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        map.put("data",page.getContent());
        map.put("count",page.getTotalElements());
        map.put("limit",page.getPageSize());
        map.put("page",page.getPageIndex());
        returnMap.put("data",map);
        returnMap.put("status",200);
        return returnMap;
    }


    /**
     *  保存答案
     */
    @RequestMapping(value = "/saveAnswer", method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public boolean saveAnswer(String optionsJson) {
        Account account = AccountUtils.getCurrentAccount();
        ObjectMapper mapper = new ObjectMapper();
        List<Answer> answerList;
        if(StringUtils.isBlank(optionsJson)){
            throw new IllegalArgumentException("至少包含一条数据！");
        }
        try {
            answerList = mapper.readValue(optionsJson, new TypeReference<List<Answer>>() {});
        } catch (IOException e) {
            throw new IllegalArgumentException("输入数量必须是正整数");
        }
        List<QuestionnaireQuestionAnswer> questionAnswers = new ArrayList<>();
        //准备数据
        this.prepareDocData(answerList,account,questionAnswers);
        //写入数据
        this.questionnaireQuestionAnswerService.insert(questionAnswers, account.getUserCode());
        return true;
    }

    private void prepareDocData(List<Answer> answerList, Account account, List<QuestionnaireQuestionAnswer> questionAnswers){
        if(CollectionUtils.isEmpty(answerList)){
            throw new IllegalArgumentException("至少包含一条数据!");
        }
        Answer answerTemp = answerList.get(0);
        Questionnaire questionnaire =new Questionnaire();
        questionnaire.setQuestionnaireCode(answerTemp.getQuestionnaireCode());
        if (this.questionnaireService.exists(questionnaire)){
            questionnaire = this.questionnaireService.selectOne(questionnaire);
            for (Answer answer:answerList) {
                QuestionnaireQuestionAnswer questionAnswer = new QuestionnaireQuestionAnswer();
                questionAnswer.setAnswer(answer.getAnswer());
                questionAnswer.setAnswerCode(answer.getQuestionCode()+"_answer");
                questionAnswer.setQuestionCode(answer.getQuestionCode());
                questionAnswer.setQuestionName(answer.getQuestionName());
                questionAnswer.setQuestionnaireCode(answer.getQuestionCode());
                questionAnswer.setQuestionnaireName(questionnaire.getQuestionnaireName());
                questionAnswer.setStudentCode(account.getUserCode());
                questionAnswer.setStudentName(account.getUserName());

                questionAnswers.add(questionAnswer);
            }
        }else {
            throw new IllegalArgumentException("根据该问卷编码没有找到问卷，编码："+ answerTemp.getQuestionnaireCode());
        }
    }
}