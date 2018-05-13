/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.QuestionnaireQuestion;
import com.pp.basic.domain.QuestionnaireQuestionTemplate;
import com.pp.basic.domain.QuestionnaireTemplate;
import com.pp.basic.domain.SystemUser;
import com.pp.basic.service.QuestionnaireQuestionTemplateService;
import com.pp.basic.service.QuestionnaireTemplateService;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import com.pp.web.account.Account;
import com.pp.web.controller.BaseController;
import com.pp.web.controller.until.AccountUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.*;

/**
 * 问卷模板表Controller
 * 
 * @author
 */
@Controller
@RequestMapping("/web/questionnairetemplate")
public class QuestionnaireTemplateController extends BaseController {

    @Autowired
    QuestionnaireTemplateService questionnaireTemplateService;


    @Autowired
    QuestionnaireQuestionTemplateService questionnaireQuestionTemplateService;

    /**
     * 逻辑删除数据
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> delete(Long id) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("status",300);
        Account account = AccountUtils.getCurrentAccount();
        if(!account.getRole().equals(SystemUser.AUTHOR_ADMIN)) {
            map.put("msg","为管理员操作，当前用户没有管理员权限");
            return map;
        }
        int rows = this.questionnaireTemplateService.delete(id, account.getUserCode());
        if (rows == 1) {
            map.put("status",200);
            return map;
        }
        map.put("msg","删除失败");
        return map;
    }

    /**
     * 分页查询
     */
    @RequestMapping(value = "/pageQuery", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String,Object> pageQuery(QuestionnaireTemplate questionnaireTemplateQuery,
                                            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                            @RequestParam(value = "dir", required = false, defaultValue = "asc") String sortOrder,
                                            @RequestParam(value = "sd") String sortName) {

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
        Page<QuestionnaireTemplate> templatePage = new Page<QuestionnaireTemplate>(pageIndex, size, sort);
        // 执行查询
        templatePage = this.questionnaireTemplateService.selectPage(questionnaireTemplateQuery, templatePage);
        // 返回查询结果
        // 返回查询结果
        HashMap<String,Object> map = new HashMap<String,Object>();
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        if (templatePage!=null){
            map.put("data",templatePage.getContent());
            map.put("count",templatePage.getTotalElements());
            map.put("limit",templatePage.getPageSize());
            map.put("page",templatePage.getPageIndex()+1);
            returnMap.put("data",map);
            returnMap.put("status",200);
        }else {
            returnMap.put("data",map);
            returnMap.put("msg","没有查询到数据");
            returnMap.put("status",300);
        }
        return returnMap;
    }

    /**
     * 分页查询
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String,Object> detail(String templateCode) {

        QuestionnaireQuestionTemplate questionTemplate = new QuestionnaireQuestionTemplate();
        questionTemplate.setTemplateCode(templateCode);
        // 执行查询
        List<QuestionnaireQuestionTemplate> questionTemplates = this.questionnaireQuestionTemplateService.selectList(questionTemplate);
        // 返回查询结果
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        if (!CollectionUtils.isEmpty(questionTemplates)){
            returnMap.put("questions",questionTemplates);
            returnMap.put("templateName",questionTemplates.get(0).getTemplateName());
            returnMap.put("status",200);
        }else {
            returnMap.put("msg","没有查询到数据");
            returnMap.put("status",300);
        }
        return returnMap;
    }

    @RequestMapping(value = "/importQuestionTemplate", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> importQuestion(HttpServletRequest request) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("status",300);
        Account account = AccountUtils.getCurrentAccount();
        String templateCode = UUID.randomUUID().toString().replace("-","");
        if(!account.getRole().equals(SystemUser.AUTHOR_ADMIN)) {
            map.put("msg","为管理员操作，当前用户没有管理员权限");
            return map;
        }

        // 保存附件
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartRequest.getFile("templateFile");

        int rows = 0;// 实际导入行数
        // 最大导入条数
        Integer importNum = 50;
        String fileName = multipartFile.getOriginalFilename();
        int lastIndexOfDot = fileName.lastIndexOf('.');
        int fileNameLength = fileName.length();
        String prefix = fileName.substring(lastIndexOfDot + 1, fileNameLength);
        try {
            QuestionnaireTemplate template = new QuestionnaireTemplate();
            //准备数据
            template.setTemplateCode(templateCode);
            //文件名为模板名
            template.setTemplateName(fileName.substring(0,lastIndexOfDot));
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
                List<QuestionnaireQuestionTemplate> questionTemplates = new ArrayList<>();
                for (int i = 2; i <= rows; i++) {
                    HSSFRow row = sheet.getRow(i);
                    if (row != null) {
                        QuestionnaireQuestionTemplate questionTemplate = new QuestionnaireQuestionTemplate();
                        if(StringUtils.isNotEmpty(checkIsEmpty(row,i))){
                            map.put("msg", checkIsEmpty(row,i));
                            return map;
                        }
                        prepareData(questionTemplate,row,i,template);
                        questionTemplates.add(questionTemplate);
                    }
                }
                if (CollectionUtils.isNotEmpty(questionTemplates)) {
                    try {
                        this.questionnaireTemplateService.saveTemplate(template,questionTemplates,account.getUserCode());
                    } catch (Exception r) {
                        map.put("msg","写入失败：" + r.getMessage());
                    }
                }else {
                    map.put("msg","写入失败：原因是模板内容为空！");
                }
                map.put("status",200);
            }
        } catch (Exception e) {
            map.put("msg", "导入失败说明：数据导入异常！"+e.getMessage());
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
    private void prepareData(QuestionnaireQuestionTemplate questionTemplate,HSSFRow row,int i,QuestionnaireTemplate template) {
        try {
            if (row.getCell(0).toString().equals("选择题") ){
                questionTemplate.setQuestionTypeCode(QuestionnaireQuestion.QUESTION_TYPE_CODE_CHOICE);
                questionTemplate.setQuestionTypeName(QuestionnaireQuestion.QUESTION_TYPE_NAME_CHOICE);
                questionTemplate.setAnswerGroup(row.getCell(3).toString());
                questionTemplate.setQuestionScore(Integer.parseInt(row.getCell(4).toString()));
            } else {
                questionTemplate.setQuestionTypeCode(QuestionnaireQuestion.QUESTION_TYPE_CODE_DESC);
                questionTemplate.setQuestionTypeName(QuestionnaireQuestion.QUESTION_TYPE_NAME_DESC);
            }
            questionTemplate.setTemplateCode(template.getTemplateCode());
            questionTemplate.setTemplateName(template.getTemplateName());
            questionTemplate.setQuestionCode(template.getTemplateCode()+"_question_"+i);
            questionTemplate.setQuestionName(row.getCell(1).toString());
            if (row.getCell(2).toString().equals("是") ){
                questionTemplate.setIsMustAnswer(QuestionnaireQuestion.IS_MUST_ANSWER);
            } else {
                questionTemplate.setIsMustAnswer(QuestionnaireQuestion.IS_NOT_MUST_ANSWER);
            }
        } catch (Exception e) {
            throw new RuntimeException("导入异常!"+e.getMessage());
        }
    }
}