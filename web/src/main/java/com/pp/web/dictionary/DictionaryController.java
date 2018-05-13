package com.pp.web.dictionary;

import com.pp.basic.domain.*;
import com.pp.basic.service.AnswerGroupService;
import com.pp.basic.service.LessonService;
import com.pp.basic.service.QuestionnaireTemplateService;
import com.pp.basic.service.TeacherLessonService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * Created by zhaopeng on 2017/3/14.
 */
@Controller
@RequestMapping("/dictionary")
public class DictionaryController {

    @Autowired
    LessonService lessonService;

    @Autowired
    TeacherLessonService teacherLessonService;

    @Autowired
    AnswerGroupService answerGroupService;

    @Autowired
    QuestionnaireTemplateService questionnaireTemplateService;

    /**
     * 查询字典  课程和教师
     *
     * @return
     */
    @RequestMapping(value = "/dictionary", method ={RequestMethod.POST,RequestMethod.GET} )
    @ResponseBody
    public Map<String,Object> dictionary() throws IllegalAccessException {
        Map<String,Object> map = new HashMap<>();
        Set<String> set = new HashSet<>();
        List<Lesson> lessons = this.lessonService.selectAll();
        if (!CollectionUtils.isEmpty(lessons)){
            for (Lesson lesson:lessons) {
                set.add(lesson.getTerm());
            }
        }
        //学期
        map.put("terms",set);
        Map<String,Object> processStatusMap= new HashMap<>();
        processStatusMap.put("0","未答");
        processStatusMap.put("1","进行中");
        processStatusMap.put("2","答完");
        map.put("processStatusMap",processStatusMap);
        return map;
    }

    @RequestMapping(value = "/dictionaryChoice", method ={RequestMethod.POST,RequestMethod.GET} )
    @ResponseBody
    public Map<String,Object> dictionaryChoice(String group) throws IllegalAccessException {
        Map<String,Object> map = new HashMap<>();
        Map<String,Object> answerMap = new HashMap<>();

        AnswerGroup answerGroupQuery = new AnswerGroup();
        answerGroupQuery.setGroupCode(group);
        List<AnswerGroup> groups = answerGroupService.selectList(answerGroupQuery);
        for (AnswerGroup answerGroup:groups) {
            map.put(answerGroup.getAnswer(),answerGroup.getAnswerValue());
        }
        answerMap.put("answers",map);
        return answerMap;
    }

    @RequestMapping(value = "/allGroup", method ={RequestMethod.POST,RequestMethod.GET} )
    @ResponseBody
    public Map<String,Object> allGroup() throws IllegalAccessException {
        Map<String,Object> answerMap = new HashMap<>();
        Map<String,Object> resultMap = new HashMap<>();
        List<AnswerGroup> allGroup = answerGroupService.selectAll();
        Set<String> groupSet = new HashSet<>();
        for (AnswerGroup answerGroup:allGroup) {
            groupSet.add(answerGroup.getGroupCode());
        }
        for (String group: groupSet) {
            Map<String,Object> map = new HashMap<>();
            AnswerGroup answerGroupQuery = new AnswerGroup();
            answerGroupQuery.setGroupCode(group);
            List<AnswerGroup> groups = answerGroupService.selectList(answerGroupQuery);
            for (AnswerGroup answerGroup:groups) {
                map.put(answerGroup.getAnswer(),answerGroup.getAnswerValue());
            }
            answerMap.put(group,map);
        }
        resultMap.put("groups",answerMap);
        return resultMap;
    }

    @RequestMapping(value = "/allTemplate", method ={RequestMethod.POST,RequestMethod.GET} )
    @ResponseBody
    public Map<String,Object> allTemplate() throws IllegalAccessException {

        Map<String,Object> resultMap = new HashMap<>();
        List<QuestionnaireTemplate> allTemplate = questionnaireTemplateService.selectAll();
        resultMap.put("templates",allTemplate);
        return resultMap;
    }

    @RequestMapping(value = "/findTeacherByTermAndLesson", method ={RequestMethod.POST,RequestMethod.GET} )
    @ResponseBody
    public Map<String,Object> findTeacherByTermAndLesson(String term,String lessonCode) throws IllegalAccessException {
        Map<String,Object> map = new HashMap<>();
        TeacherLesson teacherLesson = new TeacherLesson();
        teacherLesson.setTerm(term);
        teacherLesson.setLessonCode(lessonCode);
        List<TeacherLesson> teachers = this.teacherLessonService.selectList(teacherLesson);
        map.put("teachers",teachers);
        return map;
    }

    @RequestMapping(value = "/findLessonsByTerm", method ={RequestMethod.POST,RequestMethod.GET} )
    @ResponseBody
    public Map<String,Object> findLessonsByTerm(String term) throws IllegalAccessException {
        Map<String,Object> map = new HashMap<>();
        Lesson lesson = new Lesson();
        lesson.setTerm(term);
        List<Lesson> lessons = this.lessonService.selectList(lesson);
        map.put("lessons",lessons);
        return map;
    }
}
