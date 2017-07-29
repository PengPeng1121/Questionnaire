package com.pp.web.dictionary;

import com.pp.basic.domain.*;
import com.pp.basic.service.*;
import com.pp.web.account.Account;
import com.pp.web.account.AccountContext;
import com.pp.web.common.ChoiceQuestionEnum;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaopeng on 2017/3/14.
 */
@Controller
@RequestMapping("/dictionary")
public class DictionaryController {
    Logger log = LoggerFactory.getLogger(DictionaryController.class.getName());

    @Autowired
    LessonService lessonService;

    @Autowired
    TeacherService teacherService;

    /**
     * 查询字典  课程和教师
     *
     * @return
     */
    @RequestMapping(value = "/dictionary", method ={RequestMethod.POST,RequestMethod.GET} )
    @ResponseBody
    public Map<String,Object> dictionary() throws IllegalAccessException {
        Map<String,Object> map = new HashMap<>();
        List<Lesson> lessons = this.lessonService.selectAll();
        map.put("lesson",lessons);
        List<Teacher> teachers = this.teacherService.selectAll();
        map.put("teacher",teachers);

        Map<String,Object> answerMap = new HashMap<>();
        answerMap.put("A",ChoiceQuestionEnum.CHOICE_A.getName());
        answerMap.put("B",ChoiceQuestionEnum.CHOICE_B.getName());
        answerMap.put("C",ChoiceQuestionEnum.CHOICE_C.getName());
        answerMap.put("D",ChoiceQuestionEnum.CHOICE_D.getName());
        answerMap.put("E",ChoiceQuestionEnum.CHOICE_E.getName());
        map.put("answer",answerMap);
        return map;
    }

    @RequestMapping(value = "/dictionaryChoice", method ={RequestMethod.POST,RequestMethod.GET} )
    @ResponseBody
    public Map<String,Object> dictionaryChoice() throws IllegalAccessException {
        Map<String,Object> map = new HashMap<>();
        map.put("A",ChoiceQuestionEnum.CHOICE_A.getName());
        map.put("B",ChoiceQuestionEnum.CHOICE_B.getName());
        map.put("C",ChoiceQuestionEnum.CHOICE_C.getName());
        map.put("D",ChoiceQuestionEnum.CHOICE_D.getName());
        map.put("E",ChoiceQuestionEnum.CHOICE_E.getName());
        return map;
    }

}
