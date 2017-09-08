package com.pp.web.dictionary;

import com.pp.basic.domain.Lesson;
import com.pp.basic.domain.Teacher;
import com.pp.basic.domain.TeacherLesson;
import com.pp.basic.service.LessonService;
import com.pp.basic.service.TeacherLessonService;
import com.pp.basic.service.TeacherService;
import com.pp.web.common.ChoiceQuestionEnum_A;
import com.pp.web.common.ChoiceQuestionEnum_B;
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
    TeacherService teacherService;

    @Autowired
    TeacherLessonService teacherLessonService;

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
        map.put("terms",set);
        List<Teacher> teachers = this.teacherService.selectAll();
        map.put("teacher",teachers);

        Map<String,Object> answerMap = new HashMap<>();
        answerMap.put("A", ChoiceQuestionEnum_A.CHOICE_A.getName());
        answerMap.put("B", ChoiceQuestionEnum_A.CHOICE_B.getName());
        answerMap.put("C", ChoiceQuestionEnum_A.CHOICE_C.getName());
        answerMap.put("D", ChoiceQuestionEnum_A.CHOICE_D.getName());
        answerMap.put("E", ChoiceQuestionEnum_A.CHOICE_E.getName());
        map.put("answer",answerMap);

        Map<String,Object> questionnaireStatusMap = new HashMap<>();
        questionnaireStatusMap.put("0","初始");
        questionnaireStatusMap.put("1","已关联课程");
        questionnaireStatusMap.put("2","已推送学生");
        questionnaireStatusMap.put("3","学生已回答");
        map.put("questionnaireStatusMap",questionnaireStatusMap);

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
        if(group.toLowerCase().equals("a")){
            map.put("A", ChoiceQuestionEnum_A.CHOICE_A.getName());
            map.put("B", ChoiceQuestionEnum_A.CHOICE_B.getName());
            map.put("C", ChoiceQuestionEnum_A.CHOICE_C.getName());
            map.put("D", ChoiceQuestionEnum_A.CHOICE_D.getName());
            map.put("E", ChoiceQuestionEnum_A.CHOICE_E.getName());
        }else if(group.toLowerCase().equals("b")){
            map.put("A", ChoiceQuestionEnum_B.CHOICE_A.getName());
            map.put("B", ChoiceQuestionEnum_B.CHOICE_B.getName());
            map.put("C", ChoiceQuestionEnum_B.CHOICE_C.getName());
            map.put("D", ChoiceQuestionEnum_B.CHOICE_D.getName());
            map.put("E", ChoiceQuestionEnum_B.CHOICE_E.getName());
        }
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
}
