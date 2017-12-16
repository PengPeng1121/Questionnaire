/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.QuestionnaireLesson;
import com.pp.basic.service.QuestionnaireLessonService;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import com.pp.web.account.Account;
import com.pp.web.controller.BaseController;
import com.pp.web.controller.until.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

/**
 * 文卷和课程关系调查信息表Controller
 * 
 * @author
 */
@Controller
@RequestMapping("/web/questionnairelesson")
public class QuestionnaireLessonController extends BaseController {

    @Autowired
    QuestionnaireLessonService questionnaireLessonService;

    /**
     * 保存数据
     */
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public boolean insert(QuestionnaireLesson questionnaireLesson) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        this.questionnaireLessonService.insert(questionnaireLesson, account.getUserCode());
        return true;
    }

    /**
     * 修改数据
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public boolean update(QuestionnaireLesson questionnaireLessonUpdate) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        int rows = this.questionnaireLessonService.update(questionnaireLessonUpdate, account.getUserCode());
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
        int rows = this.questionnaireLessonService.delete(id, account.getUserCode());
        if (rows == 1) {
            	return true;
        }
        return false;
    }

    /**
     * 分页查询
     */
    @RequestMapping(value = "/pageQuery", method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public HashMap<String,Object> pageQuery(QuestionnaireLesson questionnaireLessonQuery,
                                            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                            @RequestParam(value = "dir", required = false, defaultValue = "asc") String sortOrder,
                                            @RequestParam(value = "sd") String sortName) {
        //TODO 数据验证
        
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
        Page<QuestionnaireLesson> questionnaireLessonPage = new Page<QuestionnaireLesson>(pageIndex, size, sort);
        // 执行查询
        questionnaireLessonPage = this.questionnaireLessonService.selectPage(questionnaireLessonQuery, questionnaireLessonPage);
        // 返回查询结果
        HashMap<String,Object> map = new HashMap<String,Object>();
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        if (questionnaireLessonPage!=null){
            map.put("data",questionnaireLessonPage.getContent());
            map.put("count",questionnaireLessonPage.getTotalElements());
            map.put("limit",questionnaireLessonPage.getPageSize());
            map.put("page",questionnaireLessonPage.getPageIndex()+1);
            returnMap.put("data",map);
            returnMap.put("status",200);
        }else {
            returnMap.put("data",map);
            returnMap.put("msg","没有查询到数据");
            returnMap.put("status",300);
        }
        return returnMap;
    }

//    @RequestMapping(value = "/relateLesson", method ={RequestMethod.POST,RequestMethod.GET})
//    @ResponseBody
//    public Boolean relateLesson(String questionnaireCode,String lessonCode) {
//        Account account = AccountUtils.getCurrentAccount();
//        if(!account.getRole().equals(SystemUser.AUTHOR_ADMIN)) {
//            throw new RuntimeException("为管理员操作，当前用户没有管理员权限");
//        }
//        Questionnaire questionnaire = new Questionnaire();
//        questionnaire.setQuestionnaireCode(questionnaireCode);
//        if(!this.questionnaireService.exists(questionnaire)){
//            throw new RuntimeException("该问卷编码找不到对应问卷，请确认");
//        }
//        questionnaire= this.questionnaireService.selectOne(questionnaire);
//        Lesson lesson = new Lesson();
//        lesson.setLessonCode(lessonCode);
//        if(!this.lessonService.exists(lesson)){
//            throw new RuntimeException("该课程编码找不到对应课程，请确认");
//        }
//        lesson = this.lessonService.selectOne(lesson);
//        QuestionnaireLesson questionnaireLesson = new QuestionnaireLesson();
//        questionnaireLesson.setLessonCode(lesson.getLessonCode());
//        questionnaireLesson.setLessonName(lesson.getLessonName());
//        questionnaireLesson.setQuestionnaireCode(questionnaire.getQuestionnaireCode());
//        questionnaireLesson.setQuestionnaireName(questionnaire.getQuestionnaireName());
//        this.questionnaireLessonService.insert(questionnaireLesson,account.getUserName());
//        questionnaire.setQuestionnaireStatusCode(Questionnaire.ALREADY_WITH_LESSON_CODE);
//        questionnaire.setQuestionnaireName(Questionnaire.ALREADY_WITH_LESSON_NAME);
//        this.questionnaireService.update(questionnaire,account.getUserName());
//        return true;
//    }
}