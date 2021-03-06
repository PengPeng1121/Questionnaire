/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.*;
import com.pp.basic.service.*;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import com.pp.web.account.Account;
import com.pp.web.controller.BaseController;
import com.pp.web.controller.until.AccountUtils;
import com.pp.web.controller.until.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.util.*;

/**
 * 文卷调查问题信息表Controller
 *
 * @author
 */
@Controller
@RequestMapping("/web/questionnairequestion")
public class QuestionnaireQuestionController extends BaseController {

    @Autowired
    QuestionnaireService questionnaireService;

    @Autowired
    QuestionnaireQuestionService questionnaireQuestionService;

    @Autowired
    QuestionnaireLessonService questionnaireLessonService;

    @Autowired
    QuestionnaireStudentService questionnaireStudentService;

    @Autowired
    LessonService lessonService;

    @Autowired
    StudentLessonService studentLessonService;

    @Autowired
    TeacherLessonService teacherLessonService;

    @Autowired
    TeacherService teacherService;

    @Autowired
    QuestionnaireQuestionTemplateService questionnaireQuestionTemplateService;

    @Autowired
    QuestionnaireScoreService questionnaireScoreService;

    @Autowired
    SystemConfigService systemConfigService;

    @Autowired
    SystemConstantService systemConstantService;

    Logger log = LoggerFactory.getLogger(QuestionnaireQuestionController.class.getName());

    @RequestMapping(value = "/queryQuestionAndQuestionnaire", method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public HashMap<String ,Object> queryQuestionAndQuestionnaire(String questionnaireCode){
        HashMap<String ,Object> map = new HashMap<>();
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireCode(questionnaireCode);
        // 执行查询
        questionnaire = this.questionnaireService.selectOne(questionnaire);
        QuestionnaireStudent questionnaireStudent = new QuestionnaireStudent();
        questionnaireStudent.setQuestionnaireCode(questionnaireCode);
        //查询改问卷的所有应做学生
        Long allStudent = this.questionnaireStudentService.count(questionnaireStudent);
        questionnaireStudent.setQuestionnaireProcessStatusCode(QuestionnaireStudent.PROCESS_CODE_DONE);
        //查询改问卷的所有已做学生
        Long doneStudent = this.questionnaireStudentService.count(questionnaireStudent);
        //查询改问卷的所对应的课程
        QuestionnaireLesson questionnaireLesson = new QuestionnaireLesson();
        questionnaireLesson.setQuestionnaireCode(questionnaireCode);
        questionnaireLesson = this.questionnaireLessonService.selectOne(questionnaireLesson);
        String lessonName = "";
        String teacherName = "";
        if(questionnaireLesson!=null){
            lessonName = questionnaireLesson.getLessonName();
            TeacherLesson teacherLesson = new TeacherLesson();
            teacherLesson.setLessonCode(questionnaireLesson.getLessonCode());
            teacherLesson.setLessonName(lessonName);
            teacherLesson.setTerm(questionnaireLesson.getTerm());
            teacherLesson = this.teacherLessonService.selectOne(teacherLesson);
            if (teacherLesson!=null){
                teacherName = teacherLesson.getTeacherName();
            }
        }
        // 返回查询结果
        map.put("questionnaire",questionnaire);
        map.put("teacherName",teacherName);
        map.put("lessonName",lessonName);
        map.put("allStudent",allStudent);
        map.put("doneStudent",doneStudent);
        if(questionnaire!=null){
            QuestionnaireQuestion questionnaireQuestion = new QuestionnaireQuestion();
            questionnaireQuestion.setQuestionnaireCode(questionnaire.getQuestionnaireCode());
            List<QuestionnaireQuestion> questionnaireQuestions= this.questionnaireQuestionService.selectList(questionnaireQuestion);
            map.put("questions",questionnaireQuestions);
        }
        return map;
    }


    @RequestMapping(value = "/pageQuery", method = RequestMethod.GET)
    @ResponseBody
    public Page<QuestionnaireQuestion> pageQuery(QuestionnaireQuestion questionnaireQuestionQuery, @RequestParam(value = "page", required = false, defaultValue = "1") int pageNum, @RequestParam(value = "rows", required = false, defaultValue = "20") int pageSize, @RequestParam(value = "sidx", required = false, defaultValue = "ts") String sortName, @RequestParam(value = "sord", required = false, defaultValue = "desc") String sortOrder) {
        HashMap<String ,Object> map = new HashMap<>();

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
        Page<QuestionnaireQuestion> page = new Page<QuestionnaireQuestion>(pageIndex, pageSize, sort);
        // 执行查询
        page = this.questionnaireQuestionService.selectPage(questionnaireQuestionQuery, page);

        return page;
    }

    @RequestMapping(value = "/importQuestion", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> importQuestion(HttpServletRequest request, String questionnaireName,String lessonCode,String term,String teacherCode,String endTime) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("status",300);
        Questionnaire questionnaire = new Questionnaire();
        Account account = AccountUtils.getCurrentAccount();
        String questionnaireCode = "Q_"+UUID.randomUUID().toString();
        if(!account.getRole().equals(SystemUser.AUTHOR_ADMIN)) {
            map.put("msg","为管理员操作，当前用户没有管理员权限");
            return map;
        }
        if(StringUtils.isEmpty(endTime)){
            map.put("msg","截止时间不能为空");
            return map;
        }else if (StringUtils.isEmpty(lessonCode)){
            map.put("msg","课程编码不能为空");
            return map;
        }else if (StringUtils.isEmpty(questionnaireName)){
            map.put("msg","问卷名称不能为空");
            return map;
        }else if (StringUtils.isEmpty(term)){
            map.put("msg","学期不能为空");
            return map;
        }
        //准备数据
        questionnaire.setQuestionnaireName(questionnaireName);
        questionnaire.setQuestionnaireCode(questionnaireCode);
        questionnaire.setQuestionnaireStatusCode(Questionnaire.CODE_INIT);
        questionnaire.setQuestionnaireStatusName(Questionnaire.NAME_INIT);
        questionnaire.setTerm(term);
        questionnaire.setQuestionnaireEndTime(DateUtils.timeStamp2Date(endTime));

        //根据课程和学期唯一确定
        Lesson lesson = new Lesson();
        lesson.setLessonCode(lessonCode);
        lesson.setTerm(term);
        if(!this.lessonService.exists(lesson)){
            map.put("msg","该课程编码找不到对应课程，请确认");
            return map;
        }
        lesson = this.lessonService.selectOne(lesson);
        //查询教师
        Teacher teacher = new Teacher();
        teacher.setTeacherCode(teacherCode);
        if(!this.teacherService.exists(teacher)){
            map.put("msg","该教师编码找不到对应教师，请确认");
            return map;
        }
        teacher= teacherService.selectOne(teacher);
        questionnaire.setLessonCode(lesson.getLessonCode());
        questionnaire.setLessonName(lesson.getLessonName());
        questionnaire.setTeacherName(teacher.getTeacherName());
        questionnaire.setTeacherCode(teacher.getTeacherCode());

        // 保存附件
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartRequest.getFile("questionFile");

        int rows = 0;// 实际导入行数
        // 最大导入条数
        Integer importNum = 50;
        String fileName = multipartFile.getOriginalFilename();
        int lastIndexOfDot = fileName.lastIndexOf('.');
        int fileNameLength = fileName.length();
        String prefix = fileName.substring(lastIndexOfDot + 1, fileNameLength);
        try {
            //写入
            this.questionnaireService.insert(questionnaire, account.getUserCode());
            questionnaire = this.questionnaireService.selectOne(questionnaire);
            if (prefix.toLowerCase().equals("xlsx") || prefix.toLowerCase().equals("xls")) {
                InputStream in = multipartFile.getInputStream();
                HSSFWorkbook wb = new HSSFWorkbook(in);
                // 读取工作表的数据第一个单元格
                HSSFSheet sheet = wb.getSheetAt(0);
                rows = sheet.getLastRowNum(); // 获得行数
                map.put("rows", rows);
                if (rows > importNum) {
                    map.put("msg", "一次性最多导入" + importNum + "条问题！！");
                    return map;
                }
                List<QuestionnaireQuestion> questionnaireQuestionList = new ArrayList<>();
                for (int i = 2; i <= rows; i++) {
                    HSSFRow row = sheet.getRow(i);
                    if (row != null) {
                        QuestionnaireQuestion questionnaireQuestion = new QuestionnaireQuestion();
                        if(StringUtils.isNotEmpty(checkIsEmpty(row,i))){
                            map.put("msg", checkIsEmpty(row,i));
                            return map;
                        }
                        prepareData(questionnaireQuestion,row,i,questionnaire);
                        questionnaireQuestionList.add(questionnaireQuestion);
                    }
                }
                if (CollectionUtils.isNotEmpty(questionnaireQuestionList)) {
                    try {
                        this.questionnaireQuestionService.insert(questionnaireQuestionList,account.getUserCode());
                    } catch (Exception r) {
                        map.put("msg","写入失败：" + r.getMessage());
                    }
                }
            } else {
                if(questionnaire!=null){
                    this.questionnaireService.delete(questionnaire.getId(), account.getUserCode());
                }
                map.put("msg", "导入失败说明：文件格式不正确，仅支持xlsx和xls文件！");
                return map;
            }
        } catch (Exception e) {
            if(questionnaire!=null){
                this.questionnaireService.delete(questionnaire.getId(), account.getUserCode());
            }
            map.put("msg", "导入失败说明：数据导入异常！"+e.getMessage());
            return map;
        }

        try {

            QuestionnaireLesson questionnaireLesson = new QuestionnaireLesson();
            questionnaireLesson.setLessonCode(lesson.getLessonCode());
            questionnaireLesson.setLessonName(lesson.getLessonName());
            questionnaireLesson.setTerm(term);
            questionnaireLesson.setQuestionnaireCode(questionnaire.getQuestionnaireCode());
            questionnaireLesson.setQuestionnaireName(questionnaire.getQuestionnaireName());

            this.questionnaireLessonService.insert(questionnaireLesson,account.getUserName());
            questionnaire.setQuestionnaireStatusCode(Questionnaire.ALREADY_WITH_LESSON_CODE);
            questionnaire.setQuestionnaireStatusName(Questionnaire.ALREADY_WITH_LESSON_NAME);
            this.questionnaireService.update(questionnaire,account.getUserName());
            //关联学生
            StudentLesson studentLesson = new StudentLesson();
            studentLesson.setLessonCode(lessonCode);
            List<StudentLesson> studentLessonList = new ArrayList<>();
            if(this.studentLessonService.exists(studentLesson)){
                studentLessonList =  this.studentLessonService.selectList(studentLesson);
            }
            relateStudent(studentLessonList,questionnaire,account.getUserCode());
            //写入问卷评分表
            QuestionnaireScore questionnaireScoreInsert = new QuestionnaireScore();
            copyQuestionnaireData(questionnaire,questionnaireScoreInsert);
            questionnaireScoreService.insert(questionnaireScoreInsert,account.getUserCode());
            //写入提醒表
            try{
                SystemConstant systemConstantQuery = new SystemConstant();
                systemConstantQuery.setConstantType(SystemConstant.CONSTANT_TYPE_EXPIRES);
                List<SystemConstant> list = this.systemConstantService.selectList(systemConstantQuery);
                if(!CollectionUtils.isEmpty(list)){
                    SystemConstant constant = list.get(0);
                    Long time = Integer.parseInt(constant.getConstant())*24*60*60*1000L;
                    SystemConfig insertConfig = new SystemConfig();
                    insertConfig.setRemindTime(getDiffDate(questionnaire.getQuestionnaireEndTime(),time));
                    insertConfig.setQuestionnaireCode(questionnaire.getQuestionnaireCode());
                    insertConfig.setQuestionnaireName(questionnaire.getQuestionnaireName());
                    this.systemConfigService.insert(insertConfig,account.getUserCode());
                }
            }catch (Exception e){
                log.error("写入提醒表失败,msg:"+e.getMessage(),e);
            }
        }catch (Exception e){
            if(questionnaire!=null){
                this.questionnaireService.delete(questionnaire.getId(), account.getUserCode());
            }
            log.error("导入失败说明：数据导入异常！msg:"+e.getMessage());
            map.put("msg","请求出错："+e.getMessage());
            return map;
        }
        return map;
    }

    //根据规则 过滤一行中必须填的内容 是否为空
    private String checkIsEmpty(HSSFRow row,int i) {
        StringBuilder reason = new StringBuilder("");
        reason.append("第").append(i + 1).append("行的");
        Boolean flag = true;
        try {
            if (row.getCell(0) == null || row.getCell(0).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("问题类型为空;");
                flag = false;
            }else if (!(row.getCell(0).toString().equals("简答题") || row.getCell(0).toString().equals("选择题"))) {
                reason.append("问题类型有误，只能为“选择题”或者“简答题”;");
                flag = false;
            }
            if (row.getCell(1) == null || row.getCell(1).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("问题内容有误;");
                flag = false;
            }
            if (row.getCell(2) == null || row.getCell(2).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("是否必填为空;");
                flag = false;
            }else if(!(row.getCell(2).toString().equals("是") || row.getCell(2).toString().equals("否")))  {
                reason.append("是否必填为空有误，只能为“是”或者“否”;");
                flag = false;
            }
            if (row.getCell(0).toString().equals("选择题")){
                if (row.getCell(3) == null || row.getCell(3).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                    reason.append("选项组不应该为空;");
                    flag = false;
                }
            }
        } catch (Exception e) {
            flag = false;
        }
        if (!flag) {
            return reason.toString();
        }else {
            return "";
        }
    }

    //根据规则 过滤一行中必须填的内容 是否为空
    private void prepareData(QuestionnaireQuestion questionnaireQuestion,HSSFRow row,int i,Questionnaire questionnaire) {
        try {
            if (row.getCell(0).toString().equals("选择题") ){
                questionnaireQuestion.setQuestionTypeCode(QuestionnaireQuestion.QUESTION_TYPE_CODE_CHOICE);
                questionnaireQuestion.setQuestionTypeName(QuestionnaireQuestion.QUESTION_TYPE_NAME_CHOICE);
                //选项组
                String answerGroup = row.getCell(3).toString();
                if(answerGroup.endsWith(".0")){
                    answerGroup = answerGroup.substring(0,answerGroup.indexOf("."));
                }
                questionnaireQuestion.setAnswerGroup(answerGroup);
                //权重
                String questionScore = row.getCell(4).toString();
                if(StringUtils.isNotBlank(questionScore)){
                    if(questionScore.endsWith(".0")){
                        questionScore = questionScore.substring(0,questionScore.indexOf("."));
                    }
                    questionnaireQuestion.setQuestionScore(Integer.parseInt(questionScore));
                }else {
                    questionnaireQuestion.setQuestionScore(1);
                }
            } else {
                questionnaireQuestion.setQuestionTypeCode(QuestionnaireQuestion.QUESTION_TYPE_CODE_DESC);
                questionnaireQuestion.setQuestionTypeName(QuestionnaireQuestion.QUESTION_TYPE_NAME_DESC);
            }
            questionnaireQuestion.setQuestionnaireCode(questionnaire.getQuestionnaireCode());
            questionnaireQuestion.setQuestionnaireName(questionnaire.getQuestionnaireName());
            questionnaireQuestion.setQuestionCode(questionnaire.getQuestionnaireCode()+"_question_"+i);
            questionnaireQuestion.setQuestionName(row.getCell(1).toString());
            if (row.getCell(2).toString().equals("是") ){
                questionnaireQuestion.setIsMustAnswer(QuestionnaireQuestion.IS_MUST_ANSWER);
            } else {
                questionnaireQuestion.setIsMustAnswer(QuestionnaireQuestion.IS_NOT_MUST_ANSWER);
            }
        } catch (Exception e) {
            throw new RuntimeException("导入异常,请联系供应商!");
        }
    }

    //问卷关联学生
    private void relateStudent(List<StudentLesson> studentLessonList,Questionnaire questionnaire,String user){
        List<QuestionnaireStudent> questionnaireStudentList = new ArrayList<>();

        for (StudentLesson studentLesson:studentLessonList) {
            QuestionnaireStudent questionnaireStudent = new QuestionnaireStudent();
            questionnaireStudent.setQuestionnaireProcessStatusCode(QuestionnaireStudent.PROCESS_CODE_UNDO);
            questionnaireStudent.setQuestionnaireProcessStatusName(QuestionnaireStudent.PROCESS_NAME_UNDO);
            questionnaireStudent.setStudentCode(studentLesson.getStudentCode());
            questionnaireStudent.setStudentName(studentLesson.getStudentName());
            questionnaireStudent.setQuestionnaireCode(questionnaire.getQuestionnaireCode());
            questionnaireStudent.setQuestionnaireName(questionnaire.getQuestionnaireName());
            questionnaireStudentList.add(questionnaireStudent);
        }
        if(CollectionUtils.isNotEmpty(questionnaireStudentList)){
            this.questionnaireStudentService.insert(questionnaireStudentList,user);
        }else {
            throw new IllegalArgumentException("没有学生选择该们课程");
        }
        questionnaire.setQuestionnaireStatusCode(Questionnaire.ALREADY_WITH_STUDENT_CODE);
        questionnaire.setQuestionnaireStatusName(Questionnaire.ALREADY_WITH_STUDENT_NAME);
        this.questionnaireService.update(questionnaire,user);
    }

    //根据模板复制属性
    private void copyTemplateData(QuestionnaireQuestion question,QuestionnaireQuestionTemplate questionTemplate) {
        question.setQuestionTypeCode(questionTemplate.getQuestionTypeCode());
        question.setQuestionTypeName(questionTemplate.getQuestionTypeName());
        question.setAnswerGroup(questionTemplate.getAnswerGroup());
        question.setQuestionCode(questionTemplate.getQuestionCode());
        question.setQuestionName(questionTemplate.getQuestionName());
        question.setQuestionScore(questionTemplate.getQuestionScore());
        question.setIsMustAnswer(questionTemplate.getIsMustAnswer());
    }

    //根据模板复制属性
    private void copyQuestionnaireData(Questionnaire questionnaire,QuestionnaireScore questionnaireScoreInsert) {
        questionnaireScoreInsert.setLessonCode(questionnaire.getLessonCode());
        questionnaireScoreInsert.setLessonName(questionnaire.getLessonName());
        questionnaireScoreInsert.setQuestionnaireCode(questionnaire.getQuestionnaireCode());
        questionnaireScoreInsert.setQuestionnaireName(questionnaire.getQuestionnaireName());
        questionnaireScoreInsert.setTeacherCode(questionnaire.getTeacherCode());
        questionnaireScoreInsert.setTeacherName(questionnaire.getTeacherName());
        questionnaireScoreInsert.setTerm(questionnaire.getTerm());
        questionnaireScoreInsert.setScore(0);
    }

    //根据模板复制属性
    private void copyStudentData(StudentLesson studentLesson, QuestionnaireStudent questionnaireStudent) {
        questionnaireStudent.setQuestionnaireProcessStatusCode(QuestionnaireStudent.PROCESS_CODE_UNDO);
        questionnaireStudent.setQuestionnaireProcessStatusName(QuestionnaireStudent.PROCESS_NAME_UNDO);
        questionnaireStudent.setStudentCode(studentLesson.getStudentCode());
        questionnaireStudent.setStudentName(studentLesson.getStudentName());
    }

    //自动生成问卷
    @RequestMapping(value = "/autoCreate", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> autoCreate(HttpServletRequest request) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("status",300);
        try{
            String templateCode ="";
            //问卷名称 学期+课程+教师
            String questionnaireName ="";
            String lessonCodes ="";
            String endTime = "" ;
            String expireTime = "" ;
            try {
                BufferedReader reader = request.getReader();
                String input = "";
                StringBuffer requestBody = new StringBuffer();
                while ((input = reader.readLine()) != null) {
                    requestBody.append(input);
                }
                if (StringUtils.isEmpty(requestBody.toString())) {
                    throw new IllegalArgumentException("至少包含一条数据！");
                } else {
                    JSONObject jsonObject = new JSONObject(requestBody.toString());
                    templateCode = (String)jsonObject.get("templateCode");
                    expireTime = jsonObject.get("expireTime").toString();
                    endTime = jsonObject.get("endTime").toString();
                    lessonCodes = (String) jsonObject.get("lessonCodes");
                }
            }catch (Exception e){
                log.error("入参转换失败:"+e.getMessage(),e);
            }
            Account account = AccountUtils.getCurrentAccount();
            if(!account.getRole().equals(SystemUser.AUTHOR_ADMIN)) {
                map.put("msg","为管理员操作，当前用户没有管理员权限");
                return map;
            }
            if(StringUtils.isEmpty(templateCode)){
                map.put("msg","选择模板不能为空");
                return map;
            }
            QuestionnaireQuestionTemplate questionTemplateQuery = new QuestionnaireQuestionTemplate();
            questionTemplateQuery.setTemplateCode(templateCode);
            List<QuestionnaireQuestionTemplate> questionTemplates =this.questionnaireQuestionTemplateService.selectList(questionTemplateQuery);
            if(CollectionUtils.isEmpty(questionTemplates)){
                map.put("msg","选择模板不能为空");
                return map;
            }
            if(endTime == null){
                map.put("msg","截止时间不能为空");
                return map;
            }else  if (StringUtils.isEmpty(lessonCodes)){
                map.put("msg","课程编码不能为空");
                return map;
            }else  if (expireTime == null){
                map.put("msg","过期时间不能为空");
                return map;
            }
            //循环
            String[] lessonCodeArray = lessonCodes.split(",");
            if(lessonCodeArray!=null&& lessonCodeArray.length!=0) {
                for (int i = 0; i < lessonCodeArray.length; i++) {
                    String lessonCode = lessonCodeArray[i];
                    if (StringUtils.isEmpty(lessonCode)) {
                        continue;
                    }
                    //查询教师
                    TeacherLesson teacherLesson = new TeacherLesson();
                    teacherLesson.setLessonCode(lessonCode);
                    teacherLesson = teacherLessonService.selectOne(teacherLesson);
                    if (teacherLesson == null) {
                        map.put("msg", "该课程编码找不到对应教师，请确认，编码：" + lessonCode);
                        log.error("该课程编码找不到对应教师，请确认，编码：" + lessonCode);
                        return map;
                    }
                    String term = teacherLesson.getTerm();
                    questionnaireName = term + "-" + teacherLesson.getLessonName() + "-" + teacherLesson.getTeacherName();
                    String questionnaireCode = "Q_"+UUID.randomUUID().toString();
                    //准备数据 问卷问题
                    List<QuestionnaireQuestion> questionnaireQuestionList = new ArrayList<>();
                    for (QuestionnaireQuestionTemplate questionTemplate : questionTemplates) {
                        QuestionnaireQuestion questionnaireQuestion = new QuestionnaireQuestion();
                        copyTemplateData(questionnaireQuestion, questionTemplate);
                        questionnaireQuestion.setQuestionnaireCode(questionnaireCode);
                        questionnaireQuestion.setQuestionnaireName(questionnaireName);
                        questionnaireQuestionList.add(questionnaireQuestion);
                    }
                    //准备数据 问卷
                    Questionnaire questionnaire = new Questionnaire();
                    questionnaire.setQuestionnaireName(questionnaireName);
                    questionnaire.setQuestionnaireCode(questionnaireCode);
                    questionnaire.setQuestionnaireStatusCode(Questionnaire.ALREADY_WITH_STUDENT_CODE);
                    questionnaire.setQuestionnaireStatusName(Questionnaire.ALREADY_WITH_STUDENT_NAME);
                    questionnaire.setTerm(term);
                    questionnaire.setTemplateCode(templateCode);
                    //截止时间
                    questionnaire.setQuestionnaireEndTime(DateUtils.timeStamp2Date(endTime.toString()));
                    //过期时间
                    questionnaire.setQuestionnaireExpireTime(DateUtils.timeStamp2Date(expireTime.toString()));
                    //根据课程和学期唯一确定
                    Lesson lesson = new Lesson();
                    lesson.setLessonCode(lessonCode);
                    lesson.setTerm(term);
                    lesson = this.lessonService.selectOne(lesson);
                    if (lesson == null) {
                        map.put("msg", "该课程编码找不到对应课程，请确认，编码：" + lessonCode);
                        log.error("该课程编码找不到对应课程，请确认，编码：" + lessonCode);
                        return map;
                    }

                    questionnaire.setLessonCode(lesson.getLessonCode());
                    questionnaire.setLessonName(lesson.getLessonName());
                    questionnaire.setTeacherName(teacherLesson.getTeacherName());
                    questionnaire.setTeacherCode(teacherLesson.getTeacherCode());
                    //准备数据 问卷课程
                    QuestionnaireLesson questionnaireLesson = new QuestionnaireLesson();
                    questionnaireLesson.setLessonCode(lesson.getLessonCode());
                    questionnaireLesson.setLessonName(lesson.getLessonName());
                    questionnaireLesson.setTerm(term);
                    questionnaireLesson.setQuestionnaireCode(questionnaireCode);
                    questionnaireLesson.setQuestionnaireName(questionnaireName);
                    //准备数据 问卷学生
                    StudentLesson studentLessonQuery = new StudentLesson();
                    studentLessonQuery.setLessonCode(lessonCode);
                    List<StudentLesson> studentLessonList = this.studentLessonService.selectList(studentLessonQuery);
                    List<QuestionnaireStudent> questionnaireStudentList = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(studentLessonList)) {
                        for (StudentLesson studentLesson : studentLessonList) {
                            QuestionnaireStudent questionnaireStudent = new QuestionnaireStudent();
                            copyStudentData(studentLesson, questionnaireStudent);
                            questionnaireStudent.setQuestionnaireCode(questionnaireCode);
                            questionnaireStudent.setQuestionnaireName(questionnaireName);
                            questionnaireStudentList.add(questionnaireStudent);
                        }
                    } else {
                        map.put("msg", "没有学生选择该门课程！请确认后操作！，编码：" + lessonCode);
                        log.error("没有学生选择该门课程！请确认后操作！，编码：" + lessonCode);
                        return map;
                    }
                    //准备数据 问卷学生评分
                    QuestionnaireScore questionnaireScoreInsert = new QuestionnaireScore();
                    copyQuestionnaireData(questionnaire, questionnaireScoreInsert);
                    //开始写入数据库
                    this.questionnaireQuestionService.save(questionnaire, questionnaireScoreInsert, questionnaireLesson, questionnaireQuestionList, questionnaireStudentList, account.getUserCode());
                    //写入提醒表
                    try{
                        SystemConstant systemConstantQuery = new SystemConstant();
                        systemConstantQuery.setConstantType(SystemConstant.CONSTANT_TYPE_EXPIRES);
                        List<SystemConstant> list = this.systemConstantService.selectList(systemConstantQuery);
                        if(!CollectionUtils.isEmpty(list)){
                            SystemConstant constant = list.get(0);
                            Long time = Integer.parseInt(constant.getConstant())*24*60*60*1000L;
                            SystemConfig insertConfig = new SystemConfig();
                            insertConfig.setQuestionnaireCode(questionnaire.getQuestionnaireCode());
                            insertConfig.setRemindTime(getDiffDate(questionnaire.getQuestionnaireEndTime(),time));
                            insertConfig.setQuestionnaireName(questionnaire.getQuestionnaireName());
                            this.systemConfigService.insert(insertConfig,account.getUserCode());
                        }
                    }catch (Exception e){
                        log.error("写入提醒表失败,msg:"+e.getMessage(),e);
                    }
                }
            }
        }catch (Exception e){
            log.error("生成调查问卷失败,msg:"+e.getMessage(),e);
            map.put("msg","生成调查问卷失败,msg:"+e.getMessage());
            return map;
        }
        map.put("status",200);
        return map;
    }


    private Date getDiffDate(Date remindDate,Long time){
        if(remindDate==null){
            return new Date();
        }
        Long diff = remindDate.getTime() - time;
        if (diff!=null){
            return DateUtils.timeStamp2Date(diff.toString());
        }
        return new Date();
    }
}