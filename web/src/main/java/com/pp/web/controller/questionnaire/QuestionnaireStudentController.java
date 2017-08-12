/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.*;
import com.pp.basic.domain.vo.QuestionnaireStudentExportVo;
import com.pp.basic.service.QuestionnaireLessonService;
import com.pp.basic.service.QuestionnaireService;
import com.pp.basic.service.QuestionnaireStudentService;
import com.pp.basic.service.StudentLessonService;
import com.pp.common.core.Page;
import com.pp.web.account.Account;
import com.pp.web.controller.BaseController;
import com.pp.web.controller.login.LoginController;
import com.pp.web.controller.until.AccountUtils;
import com.pp.web.controller.until.PoiUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
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

    @Autowired
    QuestionnaireService questionnaireService;

    @Autowired
    QuestionnaireLessonService questionnaireLessonService;

    @Autowired
    StudentLessonService studentLessonService;


    Logger log = LoggerFactory.getLogger(QuestionnaireStudentController.class.getName());
    /**
     * 显示列表页面
     */
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public String listPage() {
        return "QuestionnaireStudent/questionnaire_student_list";
    }

    /**
     * 显示新增页面
     */
    @RequestMapping(value = "/addPage", method = RequestMethod.GET)
    public String addPage() {
        return "QuestionnaireStudent/questionnaire_student_add";
    }

    /**
     * 显示修改页面
     */
    @RequestMapping(value = "/editPage", method = RequestMethod.GET)
    public String editPage(Long id, Model model) {
        //TODO 数据验证
        return "QuestionnaireStudent/questionnaire_student_edit";
    }

    /**
     * 保存数据
     */
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public boolean insert(QuestionnaireStudent questionnaireStudent) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        this.questionnaireStudentService.insert(questionnaireStudent, account.getUserCode());
        return true;
    }

    /**
     * 修改数据
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public boolean update(QuestionnaireStudent questionnaireStudentUpdate) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        int rows = this.questionnaireStudentService.update(questionnaireStudentUpdate, account.getUserCode());
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
        int rows = this.questionnaireStudentService.delete(id, account.getUserCode());
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
    public HashMap<String,Object> pageQuery(QuestionnaireStudent questionnaireStudentQuery,
                                                @RequestParam(value = "page", required = false, defaultValue = "1") int pageNum,
                                                @RequestParam(value = "rows", required = false, defaultValue = "20") int pageSize,
                                                @RequestParam(value = "sidx", required = false, defaultValue = "ts") String sortName,
                                                @RequestParam(value = "sord", required = false, defaultValue = "desc") String sortOrder) {
        //TODO 数据验证


        Page<QuestionnaireStudent> page = this.buildPage(pageNum, pageSize, sortName, sortOrder);
        Account account =AccountUtils.getCurrentAccount();
        questionnaireStudentQuery.setStudentCode(account.getUserCode());
        // 执行查询
        page = this.questionnaireStudentService.selectPage(questionnaireStudentQuery, page);
        // 返回查询结果
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

    /**
     * 查询没有做的问卷
     */
    @RequestMapping(value = "/findUnDoQuestionnaire",method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public HashMap<String,Object> findUnDoQuestionnaire() {
        //TODO 数据验证

        // 设置合理的参数
        int pageNum = 1;
        int pageSize = 20;
        // 排序--默认
        String sortName="ts";
        String sortOrder = "desc";
        // 创建分页对象
        Page<QuestionnaireStudent> page = this.buildPage(pageNum, pageSize, sortName, sortOrder);
        Account account = AccountUtils.getCurrentAccount();
        QuestionnaireStudent questionnaireStudentQuery = new QuestionnaireStudent();
        questionnaireStudentQuery.setQuestionnaireProcessStatusCode(QuestionnaireStudent.PROCESS_CODE_UNDO);
        questionnaireStudentQuery.setStudentCode(account.getUserCode());
        // 执行查询
        page = this.questionnaireStudentService.selectPage(questionnaireStudentQuery, page);
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


    @RequestMapping(value = "/pushQuestionnaire",method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Boolean pushQuestionnaire(String questionnaireCode) {
        log.info("pushQuestionnaire---questionnaireCode:"+questionnaireCode);
        Account account = AccountUtils.getCurrentAccount();
        if(!account.getRole().equals(SystemUser.AUTHOR_ADMIN)) {
            throw new RuntimeException("为管理员操作，当前用户没有管理员权限");
        }
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireCode(questionnaireCode);
        if(!this.questionnaireService.exists(questionnaire)){
            throw new RuntimeException("该问卷编码找不到对应问卷，请确认");
        }
        questionnaire= this.questionnaireService.selectOne(questionnaire);
        QuestionnaireLesson questionnaireLesson = new QuestionnaireLesson();
        List<QuestionnaireLesson> questionnaireLessonList = this.questionnaireLessonService.selectList(questionnaireLesson);
        if(CollectionUtils.isEmpty(questionnaireLessonList)){
            throw new RuntimeException("该问卷没有关联课程，请确认");
        }
        //得到了所有选择课程的学生
        List<StudentLesson> studentLessonList = new ArrayList<>();
        for (QuestionnaireLesson lesson: questionnaireLessonList) {
            StudentLesson studentLesson = new StudentLesson();
            studentLesson.setLessonCode(lesson.getLessonCode());
            List<StudentLesson> subStudentLessons = this.studentLessonService.selectList(studentLesson);
            studentLessonList.addAll(subStudentLessons);
        }
        List<QuestionnaireStudent> questionnaireStudentList = prepareData(studentLessonList,questionnaire);
        this.questionnaireStudentService.insert(questionnaireStudentList,account.getUserName());
        questionnaire.setQuestionnaireStatusCode(Questionnaire.ALREADY_WITH_STUDENT_CODE);
        questionnaire.setQuestionnaireName(Questionnaire.ALREADY_WITH_STUDENT_NAME);
        this.questionnaireService.update(questionnaire,account.getUserName());
        // 返回查询结果
        return true;
    }

    private List<QuestionnaireStudent> prepareData(List<StudentLesson> studentLessonList,Questionnaire questionnaire){
        List<QuestionnaireStudent> questionnaireStudentList = new ArrayList<>();
        for (StudentLesson studentLesson:studentLessonList) {
            QuestionnaireStudent questionnaireStudent = new QuestionnaireStudent();
            questionnaireStudent.setStudentCode(studentLesson.getStudentCode());
            questionnaireStudent.setStudentName(studentLesson.getStudentName());
            questionnaireStudent.setQuestionnaireCode(QuestionnaireStudent.PROCESS_CODE_UNDO);
            questionnaireStudent.setQuestionnaireName(QuestionnaireStudent.PROCESS_NAME_UNDO);
            questionnaire.setQuestionnaireCode(questionnaire.getQuestionnaireCode());
            questionnaire.setQuestionnaireName(questionnaire.getQuestionnaireName());
            questionnaireStudentList.add(questionnaireStudent);
        }
        return null;
    }

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
                data[index][3] = this.notNull(f.format(vo.getCreateTime()));
                data[index][4] = this.notNull(f.format(vo.getRemindTime()));
                data[index][5] = this.notNull(vo.getLessonName());
                index++;
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