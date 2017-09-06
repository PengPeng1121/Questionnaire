/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pp.basic.domain.Questionnaire;
import com.pp.basic.domain.QuestionnaireQuestionAnswer;
import com.pp.basic.domain.QuestionnaireStudent;
import com.pp.basic.domain.vo.Answer;
import com.pp.basic.service.QuestionnaireQuestionAnswerService;
import com.pp.basic.service.QuestionnaireService;
import com.pp.basic.service.QuestionnaireStudentService;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import com.pp.web.account.Account;
import com.pp.web.common.ChoiceQuestionEnum_A;
import com.pp.web.common.ChoiceQuestionEnum_B;
import com.pp.web.controller.BaseController;
import com.pp.web.controller.until.AccountUtils;
import com.pp.web.controller.until.PoiUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文卷调查问题答案信息表Controller
 *
 * @author
 */
@Controller
@RequestMapping("/web/questionnairequestionanswer")
public class QuestionnaireQuestionAnswerController extends BaseController {


    Logger log = LoggerFactory.getLogger(QuestionnaireQuestionAnswerController.class.getName());

    @Autowired
    QuestionnaireQuestionAnswerService questionnaireQuestionAnswerService;

    @Autowired
    QuestionnaireService questionnaireService;

    @Autowired
    QuestionnaireStudentService questionnaireStudentService;

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
        if (page!=null){
            map.put("data",page.getContent());
            map.put("count",page.getTotalElements());
            map.put("limit",page.getPageSize());
            map.put("page",page.getPageIndex());
            returnMap.put("data",map);
            returnMap.put("status",200);
        }else {
            returnMap.put("data",map);
            returnMap.put("msg","没有查询到数据");
            returnMap.put("status",300);
        }
        return returnMap;
    }


    /**
     *  保存答案
     */
    @RequestMapping(value = "/saveAnswer", method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Map<String,Object> saveAnswer(HttpServletRequest request) {
        Account account = AccountUtils.getCurrentAccount();
        Map<String,Object> resultMap = new HashMap<String,Object>();
        resultMap.put("status",300);
        try {
            BufferedReader reader = request.getReader();
            String input = "";
            StringBuffer requestBody = new StringBuffer();
            while ((input = reader.readLine())!=null){
                requestBody.append(input);
            }
            JSONArray answerArray = null;
            String questionnaireCode ="";
            if(StringUtils.isEmpty(requestBody.toString())){
                throw new IllegalArgumentException("至少包含一条数据！");
            }else{
                JSONObject jsonObject = new  org.json.JSONObject(requestBody.toString());
                answerArray = (JSONArray) jsonObject.get("question");
                questionnaireCode = (String) jsonObject.get("questionnaireCode");
            }

            Questionnaire questionnaire = new Questionnaire();
            questionnaire.setQuestionnaireCode(questionnaireCode);
            if (!this.questionnaireService.exists(questionnaire)){
                throw new IllegalArgumentException("根据问卷编码查询不到问卷！");
            }
            questionnaire = this.questionnaireService.selectOne(questionnaire);

            ObjectMapper mapper = new ObjectMapper();
            List<Answer> answerList;
            if(StringUtils.isBlank(answerArray.toString())){
                throw new IllegalArgumentException("至少包含一条数据！");
            }
            try {
                answerList = mapper.readValue(answerArray.toString(), new TypeReference<List<Answer>>() {});
            } catch (IOException e) {
                throw new IllegalArgumentException("解析json异常");
            }
            List<QuestionnaireQuestionAnswer> questionAnswers = new ArrayList<>();
            //准备数据
            this.prepareDocData(answerList,account,questionAnswers,questionnaire);
            //写入数据
            this.questionnaireQuestionAnswerService.insert(questionAnswers, account.getUserCode());

            //更改状态
            QuestionnaireStudent questionnaireStudent = new QuestionnaireStudent();
            questionnaireStudent.setStudentCode(account.getUserCode());
            questionnaireStudent.setQuestionnaireCode(questionnaireCode);
            if(this.questionnaireStudentService.exists(questionnaireStudent)){
                questionnaireStudent = this.questionnaireStudentService.selectOne(questionnaireStudent);
                questionnaireStudent.setQuestionnaireProcessStatusCode(QuestionnaireStudent.PROCESS_CODE_DONE);
                questionnaireStudent.setQuestionnaireProcessStatusName(QuestionnaireStudent.PROCESS_NAME_DONE);
                this.questionnaireStudentService.update(questionnaireStudent, account.getUserCode());
            }
            resultMap.put("status",200);
        } catch (Exception e) {
            resultMap.put("msg",e.getMessage());
            log.error(e.getMessage(),e);
            return resultMap;
        }
        return resultMap;
    }

    private void prepareDocData(List<Answer> answerList, Account account, List<QuestionnaireQuestionAnswer> questionAnswers,Questionnaire questionnaire){
        if(CollectionUtils.isEmpty(answerList)){
            throw new IllegalArgumentException("至少包含一条数据!");
        }
        String group = questionnaire.getAnswerGroup();
        for (Answer answer:answerList) {
            QuestionnaireQuestionAnswer questionAnswer = new QuestionnaireQuestionAnswer();
            questionAnswer.setAnswer(answer.getAnswer());
            //取出答案的值
            if (group.toLowerCase().equals("a")){
                questionAnswer.setAnswerValue(ChoiceQuestionEnum_A.getName(answer.getAnswer()));
            }else if (group.toLowerCase().equals("b")) {
                questionAnswer.setAnswerValue(ChoiceQuestionEnum_B.getName(answer.getAnswer()));
            }else {
                //简答题
                questionAnswer.setAnswerValue(answer.getAnswer());
            }
            questionAnswer.setAnswerCode(answer.getQuestionCode()+"_answer");
            questionAnswer.setQuestionCode(answer.getQuestionCode());
            questionAnswer.setQuestionName(answer.getQuestionName());
            questionAnswer.setQuestionnaireCode(answer.getQuestionCode());
            questionAnswer.setQuestionnaireName(questionnaire.getQuestionnaireName());
            questionAnswer.setStudentCode(account.getUserCode());
            questionAnswer.setStudentName(account.getUserName());
            questionAnswers.add(questionAnswer);
        }
    }

    /**
     * 导出问卷答题属性
     */
    @RequestMapping(value = "/exportAnswers", method = {RequestMethod.POST ,RequestMethod.GET})
    public void exportAnswers(HttpServletResponse response) {

        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            String title = "导入课程与学生关系模板_" + f.format(date);
            String name = title;
            String fileName = new String((name).getBytes(), PoiUtils.Excel_EnCode);

            //类型设置
            response.setContentType("application/binary;charset=ISO8859_1");
            // 名称和格式
            response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");

            String[] columnName = {"课程编码", "课程名称", "学生学号","学生姓名","学期"};
            ServletOutputStream outputStream = response.getOutputStream();
            PoiUtils.export(name, title, columnName, null,outputStream);
        } catch (Exception e) {
            throw new IllegalArgumentException("导入课程模板导出失败!" + e.getMessage());
        }
    }
}