/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pp.basic.domain.*;
import com.pp.basic.domain.vo.Answer;
import com.pp.basic.service.QuestionnaireQuestionAnswerService;
import com.pp.basic.service.QuestionnaireQuestionService;
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
import com.sun.org.apache.bcel.internal.generic.SWITCH;
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
import java.io.OutputStream;
import java.net.URLEncoder;
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

    @Autowired
    QuestionnaireQuestionService questionnaireQuestionService;

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
                if(!StringUtils.isEmpty(answer.getAnswer())){
                    questionAnswer.setAnswerValue(ChoiceQuestionEnum_A.getName(answer.getAnswer()));
                }
            }else if (group.toLowerCase().equals("b")) {
                if(!StringUtils.isEmpty(answer.getAnswer())) {
                    questionAnswer.setAnswerValue(ChoiceQuestionEnum_B.getName(answer.getAnswer()));
                }
            }else {
                //简答题
                questionAnswer.setAnswerValue(answer.getAnswer());
            }
            if(answer.getIsMustAnswer()!= QuestionnaireQuestionAnswer.IS_MUST_ANSWER || answer.getIsMustAnswer()!=QuestionnaireQuestionAnswer.IS_NOT_MUST_ANSWER ){
                throw new  IllegalArgumentException("是否必答只能为0或者1");
            }else {
                questionAnswer.setIsMustAnswer(answer.getIsMustAnswer());
            }
            if(answer.getQuestionTypeCode().equals(QuestionnaireQuestionAnswer.QUESTION_TYPE_CODE_CHOICE)){
                questionAnswer.setQuestionTypeCode(answer.getQuestionTypeCode());
                questionAnswer.setQuestionTypeName(QuestionnaireQuestionAnswer.QUESTION_TYPE_NAME_CHOICE);
            }else  if(answer.getQuestionTypeCode().equals(QuestionnaireQuestionAnswer.QUESTION_TYPE_CODE_DESC )){
                questionAnswer.setQuestionTypeCode(answer.getQuestionTypeCode());
                questionAnswer.setQuestionTypeName(QuestionnaireQuestionAnswer.QUESTION_TYPE_NAME_DESC);
            }else {
                throw new  IllegalArgumentException("题型只能为选择或者简答题");
            }
            questionAnswer.setAnswerCode(answer.getQuestionCode()+"_answer");
            questionAnswer.setQuestionCode(answer.getQuestionCode());
            questionAnswer.setQuestionName(answer.getQuestionName());
            questionAnswer.setQuestionnaireCode(answer.getQuestionnaireCode());
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
    public void exportAnswers(HttpServletResponse response,HttpServletRequest request, String questionnaireCode) {
        Account account = AccountUtils.getCurrentAccount();
        if(!account.getRole().equals(SystemUser.AUTHOR_ADMIN)) {
            throw new IllegalArgumentException("为管理员操作，当前用户没有管理员权限!");
        }
        StringBuilder sb = new StringBuilder();
        OutputStream fOut = null;
        //1 .首先查询出问卷
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireCode(questionnaireCode);
        questionnaire = this.questionnaireService.selectOne(questionnaire);
        try {
            if(questionnaire==null){
                throw new IllegalArgumentException("没有这个问卷!");
            }
            //写入头部
            writeHead(sb,questionnaire);
            //2 .查询出问卷的所有选择题
            QuestionnaireQuestion questionnaireQuestion = new QuestionnaireQuestion();
            questionnaireQuestion.setQuestionnaireCode(questionnaireCode);
            questionnaireQuestion.setQuestionTypeCode(QuestionnaireQuestion.QUESTION_TYPE_CODE_CHOICE);
            List<QuestionnaireQuestion> choiceList = this.questionnaireQuestionService.selectList(questionnaireQuestion);
            if(!CollectionUtils.isEmpty(choiceList)){
                for (QuestionnaireQuestion choice:choiceList) {
                    QuestionnaireQuestionAnswer questionAnswer = new QuestionnaireQuestionAnswer();
                    questionAnswer.setQuestionCode(choice.getQuestionCode());
                    questionAnswer.setQuestionnaireCode(questionnaireCode);
                    List<QuestionnaireQuestionAnswer> choiceAnswerList = this.questionnaireQuestionAnswerService.selectList(questionAnswer);
                    Integer a=0;
                    Integer b=0;
                    Integer c=0;
                    Integer d=0;
                    Integer e=0;
                    if(!CollectionUtils.isEmpty(choiceAnswerList)){
                        for (QuestionnaireQuestionAnswer choiceAnswer:choiceAnswerList) {
                            switch(choiceAnswer.getAnswer().toLowerCase()){
                                case "a": a=a+1;
                                    break;
                                case "b": b=b+1;
                                    break;
                                case "c": c=c+1;
                                    break;
                                case "d": d=d+1;
                                    break;
                                case "e": e=e+1;
                                    break;
                                default:
                                    break;
                            }
                            //写入选择
                            writeChoice(sb,choiceAnswer.getQuestionName(),a,b,c,d,e,choiceAnswer.getIsMustAnswer());
                        }
                    }
                }
            }else {
                sb.append("<tr>");
                sb.append("<td>题目名称：</td>");
                sb.append("<td></td>");
                sb.append("<td>选A人数：</td>");
                sb.append("<td></td>");
                sb.append("<td>选B人数:</td>");
                sb.append("<td></td>");
                sb.append("<td>选C人数:</td>");
                sb.append("<td></td>");
                sb.append("<td>选D人数:</td>");
                sb.append("<td></td>");
                sb.append("<td>选E人数:</td>");
                sb.append("<td></td>");
                sb.append("<td>是否必答:</td>");
                sb.append("<td></td>");
                sb.append("</tr>");
            }
            sb.append("<tr><td colspan=\"14\"  style='text-align: center;  font-size: 18pt;'>简答题具体情况</td></tr>");
            //3 .查询出问卷的所有学生
            QuestionnaireStudent questionnaireStudent = new QuestionnaireStudent();
            questionnaireStudent.setQuestionnaireCode(questionnaireCode);
            questionnaireStudent.setQuestionnaireProcessStatusCode(QuestionnaireStudent.PROCESS_CODE_DONE);
            List<QuestionnaireStudent> doneStudentList = this.questionnaireStudentService.selectList(questionnaireStudent);
            if(!CollectionUtils.isEmpty(doneStudentList)){
                for (QuestionnaireStudent doneStudent:doneStudentList) {
                    QuestionnaireQuestionAnswer questionAnswer = new QuestionnaireQuestionAnswer();
                    questionAnswer.setStudentCode(doneStudent.getStudentCode());
                    questionAnswer.setQuestionnaireCode(questionnaireCode);
                    questionAnswer.setQuestionTypeCode(QuestionnaireQuestion.QUESTION_TYPE_CODE_DESC);
                    List<QuestionnaireQuestionAnswer> descAnswerList = this.questionnaireQuestionAnswerService.selectList(questionAnswer);
                    if(!CollectionUtils.isEmpty(descAnswerList)){
                        //写入简答
                        writeDesc(sb,descAnswerList);
                    }
                }
            }
            sb.append("</tbody>");
            sb.append("</table>");


            SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            String title = "导出"+ questionnaire.getQuestionnaireName() +"问卷回答情况_"+ f.format(date);

            final String userAgent = request.getHeader("USER-AGENT");
            String finalFileName = null;
            if(StringUtils.contains(userAgent, "MSIE")){//IE浏览器
                finalFileName = URLEncoder.encode(title, "UTF-8");
            }else if(StringUtils.contains(userAgent, "Mozilla")){//google,火狐浏览器
                finalFileName = new String(title.getBytes(), "ISO8859-1");
            }else{
                finalFileName = URLEncoder.encode(title, "UTF-8");//其他浏览器
            }
            response.setHeader("Content-Disposition","attachment; filename=" + finalFileName + ".xls");
            //将数据插入的execl表格中
            fOut = response.getOutputStream();
            if(fOut!=null)
                fOut.write(sb.toString().getBytes());
        } catch (Exception e) {
            log.error("导出"+questionnaire.getQuestionnaireName()+"回答详情出错！"+e.getMessage(),e);
            throw new RuntimeException("导出"+questionnaire.getQuestionnaireName()+"回答详情出错！");
        } finally{
            //关闭输出文件流
            try{
                if(fOut!=null){
                    fOut.flush();
                    fOut.close();
                }
            }catch (IOException e){
                log.error("colse io is error",e);
            }
        }
    }

    //将问卷选择题答题情况写入
    private void writeDesc(StringBuilder sb,List<QuestionnaireQuestionAnswer> descAnswerList){
        for (QuestionnaireQuestionAnswer descAnswer:descAnswerList) {
            sb.append("<tr>");
            sb.append("<td>题目名称:</td>");
            sb.append("<td colspan=\"5\">"+descAnswer.getQuestionName()+"</td>");
            sb.append("<td>答案:</td>");
            sb.append("<td colspan=\"5\">"+descAnswer.getAnswerValue()+"</td>");
            sb.append("<td>是否必答:</td>");
            String isMustAnswerStr = "";
            if(descAnswer.getIsMustAnswer() == 0){
                isMustAnswerStr ="否";
            }else {
                isMustAnswerStr ="是";
            }
            sb.append("<td>"+isMustAnswerStr+"</td>");
            sb.append("</tr>");
        }
    }

    //将问卷选择题答题情况写入
    private void writeChoice(StringBuilder sb,String questionName,Integer a,Integer b,Integer c,Integer d,Integer e,Integer isMustAnswer){
        sb.append("<tr>");
        sb.append("<td>题目名称:</td>");
        sb.append("<td>"+questionName+"</td>");
        sb.append("<td>选A人数:</td>");
        sb.append("<td>"+a+"</td>");
        sb.append("<td>选B人数:</td>");
        sb.append("<td>"+b+"</td>");
        sb.append("<td>选C人数:</td>");
        sb.append("<td>"+c+"</td>");
        sb.append("<td>选D人数:</td>");
        sb.append("<td>"+d+"</td>");
        sb.append("<td>选E人数:</td>");
        sb.append("<td>"+e+"</td>");
        sb.append("<td>是否必答:</td>");
        String isMustAnswerStr = "";
        if(isMustAnswer == 0){
            isMustAnswerStr ="否";
        }else {
            isMustAnswerStr ="是";
        }
        sb.append("<td>"+isMustAnswerStr+"</td>");
        sb.append("</tr>");
    }

    //将问卷属性写入
    private void writeHead(StringBuilder sb,Questionnaire questionnaire){
        sb.append("<table cellspacing=\"0\" cellpadding=\"5\" rules=\"all\" border=\"1\">");
        sb.append("<thead>");
        sb.append("<tr><td colspan=\"14\"  style='text-align: center;  font-size: 18pt;'>"+questionnaire.getQuestionnaireName()+"回答详情</td></tr>");
        sb.append("<tr><td colspan=\"4\" style='text-align: center;'>问卷名称</td>");
        sb.append("<td colspan=\"3\" style='text-align: center;'>课程名称</td>");
        sb.append("<td colspan=\"4\" style='text-align: center;'>教师名称</td>");
        sb.append("<td colspan=\"3\" style='text-align: center;'>学期</td>");
        sb.append("</tr>");
        sb.append("<tr><td colspan=\"4\" style='text-align: center;'>"+questionnaire.getQuestionnaireName()+"</td>");
        sb.append("<td colspan=\"3\" style='text-align: center;'>"+questionnaire.getLessonName()+"</td>");
        sb.append("<td colspan=\"4\" style='text-align: center;'>"+questionnaire.getTeacherName()+"</td>");
        sb.append("<td colspan=\"3\" style='text-align: center;'>"+questionnaire.getTerm()+"</td>");
        sb.append("</tr>");
        sb.append("</thead>");
        sb.append("<tbody>");
        sb.append("<tr><td colspan=\"14\"  style='text-align: center;  font-size: 18pt;'>选择题答题情况</td></tr>");
    }

}