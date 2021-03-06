/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.QuestionnaireStudent;
import com.pp.basic.domain.vo.QuestionnaireInfoVo;
import com.pp.basic.domain.vo.QuestionnaireStudentExportVo;
import com.pp.basic.service.QuestionnaireStudentService;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import com.pp.web.account.Account;
import com.pp.web.controller.BaseController;
import com.pp.web.controller.until.AccountUtils;
import com.pp.web.controller.until.PoiUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 学生问卷调查关系表Controller
 *
 * @author
 */
@Controller
@RequestMapping("/web/questionnairestudent")
public class QuestionnaireStudentController extends BaseController {

    @Autowired
    QuestionnaireStudentService questionnaireStudentService;

    /**
     * 分页查询
     */
    @RequestMapping(value = "/pageQuery", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String,Object> pageQuery(String questionnaireProcessStatusCode,
                                            String term,
                                            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                            @RequestParam(value = "dir", required = false, defaultValue = "asc") String sortOrder,
                                            @RequestParam(value = "sd") String sortName) {
        // 开始页码
        int pageIndex = page - 1;
        // 排序
        Sort sort = null;
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = Sort.desc(sortName);
        } else {
            sort = Sort.asc(sortName);
        }
        Page<QuestionnaireInfoVo> questionnaireInfoVoPage = new Page<>(pageIndex, size,sort);
        Account account =AccountUtils.getCurrentAccount();
        HashMap<String,Object> param = new HashMap<String,Object>();
        param.put("studentCode",account.getUserCode());
        if(StringUtils.isNotEmpty(questionnaireProcessStatusCode)){
            param.put("questionnaireProcessStatusCode",questionnaireProcessStatusCode);
        }
        if(StringUtils.isNotEmpty(term)){
            param.put("term",term);
        }
        //过期时间--过期时间到了不显示
        param.put("questionnaireExpireTime",new Date());
        // 执行查询
        questionnaireInfoVoPage = this.questionnaireStudentService.showStudentQuestionnaire(param, questionnaireInfoVoPage);
        // 返回查询结果
        HashMap<String,Object> map = new HashMap<String,Object>();
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        if (questionnaireInfoVoPage!=null){
            map.put("data",questionnaireInfoVoPage.getContent());
            map.put("count",questionnaireInfoVoPage.getTotalElements());
            map.put("limit",questionnaireInfoVoPage.getPageSize());
            map.put("page",questionnaireInfoVoPage.getPageIndex()+1);
            returnMap.put("status",200);
            returnMap.put("data",map);
        }else {
            returnMap.put("data",map);
            returnMap.put("msg","没有查询到数据");
            returnMap.put("status",300);
        }
        return returnMap;
    }

    /**
     * 查询没有做的问卷
     */
    @RequestMapping(value = "/findUnDoQuestionnaire",method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public HashMap<String,Object> findUnDoQuestionnaire(Integer pageIndex) {
        // 设置合理的参数
        if (pageIndex == null || pageIndex < 1) {
            pageIndex = 1;
        }
        int pageSize = 1000;
        // 排序--默认
        String sortName="ts";
        String sortOrder = "desc";
        Sort sort = null;
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = Sort.desc(sortName);
        } else {
            sort = Sort.asc(sortName);
        }
        // 创建分页对象
        Page<QuestionnaireInfoVo> page = new Page<QuestionnaireInfoVo>(pageIndex, pageSize, sort);
        Account account = AccountUtils.getCurrentAccount();
        // 执行查询
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("studentCode",account.getUserCode());
        //过期时间--过期时间到了不显示
        map.put("questionnaireExpireTime",new Date());
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        try {
            map.put("questionnaireProcessStatusCode",QuestionnaireStudent.PROCESS_CODE_UNDO);
            page = this.questionnaireStudentService.showStudentQuestionnaire(map, page);
            // 返回查询结果
            map.clear();
            if (page!=null){
                map.put("data",page.getContent());
                map.put("count",page.getContent().size());
                map.put("limit",page.getPageSize());
                map.put("page",page.getPageIndex()+1);
                returnMap.put("data",map);
                returnMap.put("status",200);
            }else {
                returnMap.put("data",map);
                returnMap.put("msg","没有查询到数据");
                returnMap.put("status",300);
            }
        }catch (Exception e){
            returnMap.put("data",map);
            returnMap.put("msg","没有查询到数据");
            returnMap.put("status",300);
        }
        return returnMap;
    }


//    @RequestMapping(value = "/pushQuestionnaire",method ={RequestMethod.POST,RequestMethod.GET})
//    @ResponseBody
//    public Boolean pushQuestionnaire(String questionnaireCode) {
//        log.info("pushQuestionnaire---questionnaireCode:"+questionnaireCode);
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
//        QuestionnaireLesson questionnaireLesson = new QuestionnaireLesson();
//        List<QuestionnaireLesson> questionnaireLessonList = this.questionnaireLessonService.selectList(questionnaireLesson);
//        if(CollectionUtils.isEmpty(questionnaireLessonList)){
//            throw new RuntimeException("该问卷没有关联课程，请确认");
//        }
//        //得到了所有选择课程的学生
//        List<StudentLesson> studentLessonList = new ArrayList<>();
//        for (QuestionnaireLesson lesson: questionnaireLessonList) {
//            StudentLesson studentLesson = new StudentLesson();
//            studentLesson.setLessonCode(lesson.getLessonCode());
//            List<StudentLesson> subStudentLessons = this.studentLessonService.selectList(studentLesson);
//            studentLessonList.addAll(subStudentLessons);
//        }
//        List<QuestionnaireStudent> questionnaireStudentList = prepareData(studentLessonList,questionnaire);
//        this.questionnaireStudentService.insert(questionnaireStudentList,account.getUserName());
//        questionnaire.setQuestionnaireStatusCode(Questionnaire.ALREADY_WITH_STUDENT_CODE);
//        questionnaire.setQuestionnaireName(Questionnaire.ALREADY_WITH_STUDENT_NAME);
//        this.questionnaireService.update(questionnaire,account.getUserName());
//        // 返回查询结果
//        return true;
//    }
//
//    private List<QuestionnaireStudent> prepareData(List<StudentLesson> studentLessonList,Questionnaire questionnaire){
//        List<QuestionnaireStudent> questionnaireStudentList = new ArrayList<>();
//        for (StudentLesson studentLesson:studentLessonList) {
//            QuestionnaireStudent questionnaireStudent = new QuestionnaireStudent();
//            questionnaireStudent.setStudentCode(studentLesson.getStudentCode());
//            questionnaireStudent.setStudentName(studentLesson.getStudentName());
//            questionnaireStudent.setQuestionnaireCode(QuestionnaireStudent.PROCESS_CODE_UNDO);
//            questionnaireStudent.setQuestionnaireName(QuestionnaireStudent.PROCESS_NAME_UNDO);
//            questionnaire.setQuestionnaireCode(questionnaire.getQuestionnaireCode());
//            questionnaire.setQuestionnaireName(questionnaire.getQuestionnaireName());
//            questionnaireStudentList.add(questionnaireStudent);
//        }
//        return null;
//    }

    /**
     * 导出
     */
    @RequestMapping(value = "/export", method = {RequestMethod.POST ,RequestMethod.GET})
    public void export(HttpServletResponse response,String questionnaireCodes) {
        if (StringUtils.isEmpty(questionnaireCodes)){
            throw new IllegalArgumentException("输入的问卷编码为空");
        }
        String questionnaireCodeArray[] =null;
        String questionnaireCode = "";
        List<QuestionnaireStudentExportVo> questionnaireStudentExportVoList = new ArrayList<>();
        //英文逗号分隔
        if(questionnaireCodes.contains(",")){
            questionnaireCodeArray = questionnaireCodes.split(",");
            if(questionnaireCodeArray!=null&&questionnaireCodeArray.length>10){
                throw new IllegalArgumentException("一次最多导出十条问卷的问完成学生！");
            }
            for (String s:questionnaireCodeArray) {
                List<QuestionnaireStudentExportVo> subList = findExportInfo(s);
                if(CollectionUtils.isEmpty(subList)){
                    continue;
                }
                questionnaireStudentExportVoList.addAll(subList);
            }
        }else {
            questionnaireCode = questionnaireCodes;
            questionnaireStudentExportVoList =findExportInfo(questionnaireCode);
        }
        writeToExcel(questionnaireStudentExportVoList,response);
    }

    private List<QuestionnaireStudentExportVo> findExportInfo(String questionnaireCode){
        List<QuestionnaireStudentExportVo> studentExportVoList = questionnaireStudentService.exportStudentUnDoInfo(questionnaireCode);
        return studentExportVoList;
    }


    //写入
    private void  writeToExcel(List<QuestionnaireStudentExportVo> questionnaireStudentList, HttpServletResponse response){
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();
            String title = "未完成问卷的学生导出_" + f.format(date);
            String name = title;
            String fileName = new String((name).getBytes(), PoiUtils.Excel_EnCode);

            //类型设置
            response.setContentType("application/binary;charset=ISO8859_1");
            // 名称和格式
            response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".xls");

            String[] columnName = {"学生姓名", "学号", "问卷调查名称","问卷创建时间","问卷提醒时间","课程名"};
            Object[][] data = null;
            if(!CollectionUtils.isEmpty(questionnaireStudentList)){
                int index = 0;
                if (data == null) {
                    Integer total = questionnaireStudentList.size();
                    if (total > 100000) {
                        throw new RuntimeException("暂不支持导出数超过10万条记录！");
                    }
                    data = new Object[total.intValue()][6];
                }
                for (int i = 0; i < questionnaireStudentList.size(); i++) {
                    QuestionnaireStudentExportVo vo = questionnaireStudentList.get(i);
                    data[index][0] = this.notNull(vo.getStudentName());
                    data[index][1] = this.notNull(vo.getStudentCode());
                    data[index][2] = this.notNull(vo.getQuestionnaireName());
                    if(vo.getCreateTime()!=null){
                        data[index][3] = this.notNull(f.format(vo.getCreateTime()));
                    }else {
                        data[index][3] = "";
                    }
                    if(vo.getRemindTime()!=null){
                        data[index][4] = this.notNull(f.format(vo.getRemindTime()));
                    }else {
                        data[index][4] = "";
                    }

                    data[index][5] = this.notNull(vo.getLessonName());
                    index++;
                }
            }

            ServletOutputStream outputStream = response.getOutputStream();
            PoiUtils.export(name, title, columnName, data,outputStream);
        } catch (Exception e) {
            throw new IllegalArgumentException("报表导出失败!" + e.getMessage());
        }
    }

    /**
     * null处理
     * @param o
     * @return
     */
    private Object notNull(Object o) {
        if (o == null) {
            return "";
        }
        return o;
    }

}