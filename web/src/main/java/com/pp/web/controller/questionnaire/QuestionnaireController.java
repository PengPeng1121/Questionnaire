/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.*;
import com.pp.basic.service.LessonService;
import com.pp.basic.service.QuestionnaireLessonService;
import com.pp.basic.service.QuestionnaireQuestionService;
import com.pp.basic.service.QuestionnaireService;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import com.pp.web.account.Account;
import com.pp.web.controller.BaseController;
import com.pp.web.controller.until.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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

    @Autowired
    QuestionnaireLessonService questionnaireLessonService;

    @Autowired
    LessonService lessonService;

    /**
     * 显示列表页面
     */
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public String listPage() {
        return "Questionnaire/questionnaire_list";
    }

    /**
     * 显示新增页面
     */
    @RequestMapping(value = "/addPage", method = RequestMethod.GET)
    public String addPage() {
        return "Questionnaire/questionnaire_add";
    }

    /**
     * 显示修改页面
     */
    @RequestMapping(value = "/editPage", method = RequestMethod.GET)
    public String editPage(Long id, Model model) {
        //TODO 数据验证
        return "Questionnaire/questionnaire_edit";
    }

    /**
     * 保存数据
     */
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public boolean insert(String questionnaireName,String lessonCode){
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        Questionnaire questionnaire = new Questionnaire();
        try {
            questionnaire.setQuestionnaireName(questionnaireName);
            questionnaire.setQuestionnaireCode(UUID.randomUUID().toString());
            questionnaire.setQuestionnaireStatusCode(Questionnaire.CODE_INIT);
            questionnaire.setQuestionnaireStatusName(Questionnaire.NAME_INIT);
            this.questionnaireService.insert(questionnaire, account.getUserCode());
            if(!account.getRole().equals(SystemUser.AUTHOR_ADMIN)) {
                throw new RuntimeException("为管理员操作，当前用户没有管理员权限");
            }
            questionnaire.setQuestionnaireCode(questionnaire.getQuestionnaireCode());
            if(!this.questionnaireService.exists(questionnaire)){
                throw new RuntimeException("该问卷编码找不到对应问卷，请确认");
            }
            questionnaire= this.questionnaireService.selectOne(questionnaire);
            Lesson lesson = new Lesson();
            lesson.setLessonCode(lessonCode);
            if(!this.lessonService.exists(lesson)){
                throw new RuntimeException("该课程编码找不到对应课程，请确认");
            }
            lesson = this.lessonService.selectOne(lesson);
            QuestionnaireLesson questionnaireLesson = new QuestionnaireLesson();
            questionnaireLesson.setLessonCode(lesson.getLessonCode());
            questionnaireLesson.setLessonName(lesson.getLessonName());
            questionnaireLesson.setQuestionnaireCode(questionnaire.getQuestionnaireCode());
            questionnaireLesson.setQuestionnaireName(questionnaire.getQuestionnaireName());
            this.questionnaireLessonService.insert(questionnaireLesson,account.getUserName());
            questionnaire.setQuestionnaireStatusCode(Questionnaire.ALREADY_WITH_LESSON_CODE);
            questionnaire.setQuestionnaireName(Questionnaire.ALREADY_WITH_LESSON_NAME);
            this.questionnaireService.update(questionnaire,account.getUserName());
        }catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * 修改数据
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public boolean update(Questionnaire questionnaireUpdate) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        int rows = this.questionnaireService.update(questionnaireUpdate, account.getUserCode());
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
        int rows = this.questionnaireService.delete(id, account.getUserCode());
        if (rows == 1) {
            return true;
        }
        return false;
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
    public HashMap<String,Object> pageQuery(Questionnaire questionnaireQuery, @RequestParam(value = "page", required = false, defaultValue = "1") int pageNum, @RequestParam(value = "rows", required = false, defaultValue = "20") int pageSize, @RequestParam(value = "sidx", required = false, defaultValue = "ts") String sortName, @RequestParam(value = "sord", required = false, defaultValue = "desc") String sortOrder) {
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
        Page<Questionnaire> page = new Page<Questionnaire>(pageIndex, pageSize, sort);
        // 执行查询
        page = this.questionnaireService.selectPage(questionnaireQuery, page);
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
}