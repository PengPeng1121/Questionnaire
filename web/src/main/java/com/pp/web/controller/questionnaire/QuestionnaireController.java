/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.Questionnaire;
import com.pp.basic.domain.QuestionnaireQuestion;
import com.pp.basic.domain.SystemUser;
import com.pp.basic.service.QuestionnaireQuestionService;
import com.pp.basic.service.QuestionnaireService;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import com.pp.web.account.Account;
import com.pp.web.controller.BaseController;
import com.pp.web.controller.until.AccountUtils;
import com.pp.web.controller.until.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;

/**
 * 文卷调查信息表Controller
 *
 * @author
 */
@Controller
@RequestMapping("/web/questionnaire")
public class QuestionnaireController extends BaseController {

    @Autowired
    QuestionnaireService questionnaireService;

    @Autowired
    QuestionnaireQuestionService questionnaireQuestionService;

//    /**
//     * 保存数据
//     */
//    @RequestMapping(value = "/insert", method = RequestMethod.POST)
//    @ResponseBody
//    public boolean insert(String questionnaireName,String lessonCode){
//        //TODO 数据验证
//        Account account = AccountUtils.getCurrentAccount();
//        Questionnaire questionnaire = new Questionnaire();
//        try {
//            questionnaire.setQuestionnaireName(questionnaireName);
//            questionnaire.setQuestionnaireCode(UUID.randomUUID().toString());
//            questionnaire.setQuestionnaireStatusCode(Questionnaire.CODE_INIT);
//            questionnaire.setQuestionnaireStatusName(Questionnaire.NAME_INIT);
//            this.questionnaireService.insert(questionnaire, account.getUserCode());
//            if(!account.getRole().equals(SystemUser.AUTHOR_ADMIN)) {
//                throw new RuntimeException("为管理员操作，当前用户没有管理员权限");
//            }
//            questionnaire.setQuestionnaireCode(questionnaire.getQuestionnaireCode());
//            if(!this.questionnaireService.exists(questionnaire)){
//                throw new RuntimeException("该问卷编码找不到对应问卷，请确认");
//            }
//            questionnaire= this.questionnaireService.selectOne(questionnaire);
//            Lesson lesson = new Lesson();
//            lesson.setLessonCode(lessonCode);
//            if(!this.lessonService.exists(lesson)){
//                throw new RuntimeException("该课程编码找不到对应课程，请确认");
//            }
//            lesson = this.lessonService.selectOne(lesson);
//            QuestionnaireLesson questionnaireLesson = new QuestionnaireLesson();
//            questionnaireLesson.setLessonCode(lesson.getLessonCode());
//            questionnaireLesson.setLessonName(lesson.getLessonName());
//            questionnaireLesson.setQuestionnaireCode(questionnaire.getQuestionnaireCode());
//            questionnaireLesson.setQuestionnaireName(questionnaire.getQuestionnaireName());
//            this.questionnaireLessonService.insert(questionnaireLesson,account.getUserName());
//            questionnaire.setQuestionnaireStatusCode(Questionnaire.ALREADY_WITH_LESSON_CODE);
//            questionnaire.setQuestionnaireName(Questionnaire.ALREADY_WITH_LESSON_NAME);
//            this.questionnaireService.update(questionnaire,account.getUserName());
//        }catch (Exception e){
//            return false;
//        }
//        return true;
//    }

    /**
     * 修改数据
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> update(String questionnaireCode,String questionnaireName,String endTime) {
        Account account = AccountUtils.getCurrentAccount();
        HashMap<String,Object> returnMap =new HashMap<>();
        returnMap.put("status",300);
        try{
            if(!account.getRole().equals(SystemUser.AUTHOR_ADMIN)) {
                returnMap.put("msg","为管理员操作，当前用户没有管理员权限");
                return returnMap;
            }
            Questionnaire questionnaire = new Questionnaire();
            questionnaire.setQuestionnaireCode(questionnaireCode);
            if(this.questionnaireService.exists(questionnaire)){
                questionnaire= this.questionnaireService.selectOne(questionnaire);
                if(!StringUtils.isEmpty(questionnaireName)){
                    questionnaire.setQuestionnaireName(questionnaireName);
                }
                if(!StringUtils.isEmpty(endTime)){
                    questionnaire.setQuestionnaireEndTime(DateUtils.timeStamp2Date(endTime));
                }
                int rows = this.questionnaireService.update(questionnaire, account.getUserCode());
                if (rows == 1) {
                    returnMap.put("status",200);
                }else {
                    returnMap.put("msg","没有查询到问卷："+questionnaireName);
                }
            }else{
                returnMap.put("msg","没有查询到问卷："+questionnaireName);
            }
        }catch (Exception e){
            returnMap.put("msg",e.getMessage());
        }
        return returnMap;
    }

    /**
     * 逻辑删除数据
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> delete(Long id) {
        Account account = AccountUtils.getCurrentAccount();
        HashMap<String,Object> returnMap =new HashMap<>();
        returnMap.put("status",300);
        returnMap.put("msg","删除失败");
        try{
            if(!account.getRole().equals(SystemUser.AUTHOR_ADMIN)) {
                returnMap.put("msg","为管理员操作，当前用户没有管理员权限");
                return returnMap;
            }
            int rows = this.questionnaireService.delete(id, account.getUserCode());
            if (rows == 1) {
                returnMap.put("status",200);
            }
        }catch (Exception e){
            returnMap.put("msg",e.getMessage());
            returnMap.put("status",300);
        }
        return returnMap;
    }

    /**
     * 分页查询
     */
    @RequestMapping(value = "/listQuery", method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public HashMap<String,Object> listQuery(String questionnaireCode){
        Questionnaire questionnaire = new Questionnaire();
        HashMap<String,Object> map =new HashMap<>();
        //查询问卷
        questionnaire.setQuestionnaireCode(questionnaireCode);
        questionnaire = this.questionnaireService.selectOne(questionnaire);
        map.put("questionnaire",questionnaire);
        //查询问卷问题
        QuestionnaireQuestion questionnaireQuestion = new QuestionnaireQuestion();
        questionnaireQuestion.setQuestionnaireCode(questionnaireCode);
        List<QuestionnaireQuestion> questionnaireQuestions = this.questionnaireQuestionService.selectList(questionnaireQuestion);
        map.put("questionnaireQuestions",questionnaireQuestions);
        // 返回查询结果
        return map;
    }

    /**
     * 分页查询
     */
    @RequestMapping(value = "/pageQuery", method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public HashMap<String,Object> pageQuery(Questionnaire questionnaireQuery,
                                            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                            @RequestParam(value = "dir", required = false, defaultValue = "asc") String sortOrder,
                                            @RequestParam(value = "sd") String sortName) {
        //TODO 数据验证

        // 设置合理的参数
        if (size < 1) {
            size = 10;
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
        Page<Questionnaire> questionnairePage = new Page<Questionnaire>(pageIndex, size, sort);
        // 执行查询
        questionnairePage = this.questionnaireService.selectPage(questionnaireQuery, questionnairePage);
        // 返回查询结果
        HashMap<String,Object> map = new HashMap<String,Object>();
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        if (questionnairePage!=null){
            map.put("data",questionnairePage.getContent());
            map.put("count",questionnairePage.getTotalElements());
            map.put("limit",questionnairePage.getPageSize());
            map.put("page",questionnairePage.getPageIndex()+1);
            returnMap.put("data",map);
            returnMap.put("status",200);
        }else {
            returnMap.put("data",map);
            returnMap.put("msg","没有查询到数据");
            returnMap.put("status",300);
        }
        return returnMap;
    }
}