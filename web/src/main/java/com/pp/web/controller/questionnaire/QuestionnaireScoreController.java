/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.QuestionnaireQuestion;
import com.pp.basic.domain.QuestionnaireQuestionAnswer;
import com.pp.basic.domain.QuestionnaireScore;
import com.pp.basic.service.QuestionnaireQuestionAnswerService;
import com.pp.basic.service.QuestionnaireQuestionService;
import com.pp.basic.service.QuestionnaireScoreService;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import com.pp.web.account.Account;
import com.pp.web.controller.BaseController;
import com.pp.web.controller.until.AccountUtils;
import com.pp.web.controller.until.PoiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师问卷得分表Controller
 *
 * @author
 */
@Controller
@RequestMapping("/web/questionnairescore")
public class QuestionnaireScoreController extends BaseController {

    Logger log = LoggerFactory.getLogger(QuestionnaireScoreController.class.getName());

    @Autowired
    QuestionnaireScoreService questionnaireScoreService;

    @Autowired
    QuestionnaireQuestionService questionService;

    @Autowired
    QuestionnaireQuestionAnswerService questionAnswerService;
    /**
     * 分页查询
     */
    @RequestMapping(value = "/pageQuery", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String,Object> pageQuery(QuestionnaireScore questionnaireScoreQuery,
                                            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                            @RequestParam(value = "dir", required = false, defaultValue = "asc") String sortOrder,
                                            @RequestParam(value = "sd") String sortName) {
        // 设置合理的参数
        if (size < 1) {
            size = 20;
        } else if (size > 100) {
            size = 100;
        }
        // 开始页码
        int pageIndex = page - 1;
        // 排序
        Sort sort = null;
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = Sort.desc(sortName);
        } else {
            sort = Sort.asc(sortName);
        }
        // 创建分页对象
        Page<QuestionnaireScore> scorePage = new Page<QuestionnaireScore>(pageIndex, size, sort);
        // 执行查询
        scorePage = this.questionnaireScoreService.selectPage(questionnaireScoreQuery, scorePage);
        // 返回查询结果
        HashMap<String, Object> map = new HashMap<String, Object>();
        HashMap<String, Object> returnMap = new HashMap<String, Object>();
        if (scorePage != null) {
            map.put("count", scorePage.getTotalElements());
            map.put("page", scorePage.getPageIndex() + 1);
            map.put("data", scorePage.getContent());
            map.put("limit", scorePage.getPageSize());
            returnMap.put("data", map);
            returnMap.put("status", 200);
        } else {
            returnMap.put("data", map);
            returnMap.put("msg", "没有查询到数据");
            returnMap.put("status", 300);
        }
        return returnMap;
    }

    /**
     * 计算得分
     */
    @RequestMapping(value = "/calculate ", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> calculate(String questionnaireCodes) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("status",300);
        if(StringUtils.isEmpty(questionnaireCodes)){
            map.put("msg", "没有查询到数据");
            return map;
        }
        Account account = AccountUtils.getCurrentAccount();
        try{
            String[] questionnaireCodeArray = questionnaireCodes.split(",");
            if(questionnaireCodeArray!=null&& questionnaireCodeArray.length!=0){
                for (int i=0;i<questionnaireCodeArray.length;i++) {
                    String  questionnaireCode =questionnaireCodeArray[i];
                    if(StringUtils.isEmpty(questionnaireCode)){
                        continue;
                    }
                    Integer totalScore = 0;
                    QuestionnaireQuestion questionQuery = new QuestionnaireQuestion();
                    questionQuery.setQuestionnaireCode(questionnaireCode);
                    questionQuery.setQuestionTypeCode(QuestionnaireQuestion.QUESTION_TYPE_CODE_CHOICE);
                    List<QuestionnaireQuestion> questions =  questionService.selectList(questionQuery);
                    if(!CollectionUtils.isEmpty(questions)){
                        for (QuestionnaireQuestion question:questions) {
                            Integer questionScore = 0;
                            QuestionnaireQuestionAnswer answerQuery = new QuestionnaireQuestionAnswer();
                            answerQuery.setQuestionnaireCode(questionnaireCode);
                            answerQuery.setQuestionCode(question.getQuestionCode());
                            List<QuestionnaireQuestionAnswer> answerList = questionAnswerService.selectList(answerQuery);
                            if(!CollectionUtils.isEmpty(answerList)){
                                for (QuestionnaireQuestionAnswer answer:answerList) {
                                    questionScore+=answer.getAnswerScore();
                                }
                                totalScore = totalScore + questionScore/answerList.size()*question.getQuestionScore();
                            }else {
                                totalScore = 100000;
                            }
                        }
                        totalScore = totalScore/questions.size();
                        //写入数据保存
                        QuestionnaireScore questionnaireScoreQuery = new QuestionnaireScore();
                        questionnaireScoreQuery.setQuestionnaireCode(questionnaireCode);
                        QuestionnaireScore questionnaireScoreUpdate = questionnaireScoreService.selectOne(questionnaireScoreQuery);
                        questionnaireScoreUpdate.setScore(totalScore);
                        questionnaireScoreService.update(questionnaireScoreUpdate,account.getUserCode());
                    }else {
                        throw new IllegalArgumentException("问卷问题为空，不能计算得分！");
                    }
                }
            }
            map.put("status",200);
        }catch (Exception e){
            log.error("计算问卷得分失败：msg"+e.getMessage(),e);
            map.put("msg",e.getMessage());
        }
        return map;
    }

    /**
     * 导出问卷得分
     */
    @RequestMapping(value = "/export", method = {RequestMethod.POST ,RequestMethod.GET})
    public void export(HttpServletResponse response,QuestionnaireScore questionnaireScoreQuery) {
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            String title = "导入问卷得分_" + f.format(date);
            String name = title;
            String fileName = new String((name).getBytes(), PoiUtils.Excel_EnCode);

            //类型设置
            response.setContentType("application/binary;charset=ISO8859_1");
            // 名称和格式
            response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");

            List<QuestionnaireScore> questionnaireScoreList =this.questionnaireScoreService.selectList(questionnaireScoreQuery);
            String[] columnName = {"问卷名称", "课程名称", "教师姓名", "学期", "得分"};
            ServletOutputStream outputStream = response.getOutputStream();
            Object[][] data ;
            if(CollectionUtils.isEmpty(questionnaireScoreList)){
                data = null;
            }else {
                data = new Object[questionnaireScoreList.size()][5];
                for (int i = 0; i < questionnaireScoreList.size(); i++) {
                    data[i][0] = questionnaireScoreList.get(i).getQuestionnaireName() != null ? questionnaireScoreList.get(i).getQuestionnaireName() : "";
                    data[i][1] = questionnaireScoreList.get(i).getLessonName() != null ? questionnaireScoreList.get(i).getLessonName() : "";
                    data[i][2] = questionnaireScoreList.get(i).getTeacherName() != null ? questionnaireScoreList.get(i).getTeacherName() : "";
                    data[i][3] = questionnaireScoreList.get(i).getTerm() != null ? questionnaireScoreList.get(i).getTerm() : "";
                    data[i][4] = questionnaireScoreList.get(i).getScore() != null ? questionnaireScoreList.get(i).getScore() : 0;
                }
            }
            PoiUtils.export(name, title, columnName, data,outputStream);
        } catch (Exception e) {
            throw new IllegalArgumentException("导入课程模板导出失败!" + e.getMessage());
        }
    }
}