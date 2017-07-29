/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.*;
import com.pp.basic.domain.vo.InitStudent;
import com.pp.basic.domain.vo.InitStudentFail;
import com.pp.basic.service.LessonService;
import com.pp.basic.service.QuestionnaireLessonService;
import com.pp.basic.service.QuestionnaireService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pp.web.account.Account;
import com.pp.web.controller.until.AccountUtils;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import com.pp.web.controller.BaseController;
import com.pp.basic.service.QuestionnaireQuestionService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
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
    LessonService lessonService;
    /**
     * 显示列表页面
     */
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public String listPage() {
        return "common/core/QuestionnaireQuestion/questionnaire_question_list";
    }

    /**
     * 显示新增页面
     */
    @RequestMapping(value = "/addPage", method = RequestMethod.GET)
    public String addPage() {
        return "common/core/QuestionnaireQuestion/questionnaire_question_add";
    }

    /**
     * 显示修改页面
     */
    @RequestMapping(value = "/editPage", method = RequestMethod.GET)
    public String editPage(Long id, Model model) {
        //TODO 数据验证
        return "common/core/QuestionnaireQuestion/questionnaire_question_edit";
    }

    /**
     * 保存数据
     */
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public boolean insert(QuestionnaireQuestion questionnaireQuestion) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        this.questionnaireQuestionService.insert(questionnaireQuestion, account.getUserCode());
        return true;
    }

    /**
     * 修改数据
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public boolean update(QuestionnaireQuestion questionnaireQuestionUpdate) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        int rows = this.questionnaireQuestionService.update(questionnaireQuestionUpdate, account.getUserCode());
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
        int rows = this.questionnaireQuestionService.delete(id, account.getUserCode());
        if (rows == 1) {
            return true;
        }
        return false;
    }

    @RequestMapping(value = "/queryQuestionAndQuestionnaire", method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public HashMap<String ,Object> queryQuestionAndQuestionnaire(String questionnaireCode){
        HashMap<String ,Object> map = new HashMap<>();
        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setQuestionnaireCode(questionnaireCode);
        // 执行查询
        questionnaire = this.questionnaireService.selectOne(questionnaire);
        // 返回查询结果
        map.put("questionnaire",questionnaire);
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
    public Map<String,Object> importQuestion(HttpServletRequest request, String questionnaireName,String lessonCode) {
        Questionnaire questionnaire = new Questionnaire();
        Account account = AccountUtils.getCurrentAccount();
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
            throw new RuntimeException("该课程编码找不到对应课程，请确认");
        }
        if(!this.questionnaireService.exists(questionnaire)){
            throw new IllegalArgumentException("根据问卷编码没有找到问卷");
        }
        // 保存附件
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartRequest.getFile("questionFile");
        questionnaire = this.questionnaireService.selectOne(questionnaire);
        HashMap<String,Object> map = new HashMap<>();
        int rows = 0;// 实际导入行数
        // 最大导入条数
        Integer importNum = 50;
        String fileName = multipartFile.getOriginalFilename();
        int lastIndexOfDot = fileName.lastIndexOf('.');
        int fileNameLength = fileName.length();
        String prefix = fileName.substring(lastIndexOfDot + 1, fileNameLength);

        try {
            if (prefix.toLowerCase().equals("xlsx") || prefix.toLowerCase().equals("xls")) {
                InputStream in = multipartFile.getInputStream();
                HSSFWorkbook wb = new HSSFWorkbook(in);
                List<InitStudentFail> resultList = new ArrayList<InitStudentFail>();
                // 读取工作表的数据第一个单元格
                HSSFSheet sheet = wb.getSheetAt(0);
                rows = sheet.getLastRowNum(); // 获得行数
                map.put("rows", rows);
                if (rows > importNum) {
                    map.put("msg", "一次性最多导入" + importNum + "条问题！！");
                    return map;
                }
                List<QuestionnaireQuestion> questionnaireQuestionList = new ArrayList<>();
                for (int i = 1; i <= rows; i++) {
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
            if (CollectionUtils.isNotEmpty(resultList)) {
                map.put("list", resultList);
                map.put("size", resultList.size());
            } else {
                map.put("msg", "问题导入全部成功！");
            }
        } else {
            map.put("msg", "导入失败说明：文件格式不正确，仅支持xlsx和xls文件！");
        }
    } catch (Exception e) {
        map.put("msg", "导入失败说明：数据导入异常！");
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
            }else if (!(row.getCell(0).toString().equals("是") || !row.getCell(0).toString().equals("否"))) {
                reason.append("是否必填为空有误，只能为“是”或者“否”;");
                flag = false;
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
            if (row.getCell(0).toString().equals("简答题") ){
                questionnaireQuestion.setQuestionTypeCode(QuestionnaireQuestion.QUESTION_TYPE_CODE_DESC);
                questionnaireQuestion.setQuestionTypeName(QuestionnaireQuestion.QUESTION_TYPE_NAME_DESC);
            } else {
                questionnaireQuestion.setQuestionTypeCode(QuestionnaireQuestion.QUESTION_TYPE_CODE_CHOICE);
                questionnaireQuestion.setQuestionTypeName(QuestionnaireQuestion.QUESTION_TYPE_NAME_CHOICE);
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

}