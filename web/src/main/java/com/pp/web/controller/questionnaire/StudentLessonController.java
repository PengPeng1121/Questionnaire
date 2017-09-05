/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.StudentLesson;
import com.pp.basic.domain.vo.InitStudentLessonFail;
import com.pp.basic.service.StudentLessonService;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import com.pp.web.account.Account;
import com.pp.web.controller.BaseController;
import com.pp.web.controller.until.AccountUtils;
import org.apache.commons.collections.CollectionUtils;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生选课信息表Controller
 *
 * @author
 */
@Controller
@RequestMapping("/web/studentlesson")
public class StudentLessonController extends BaseController {

    @Autowired
    StudentLessonService studentLessonService;

    /**
     * 保存数据
     */
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public boolean insert(StudentLesson studentLesson) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        this.studentLessonService.insert(studentLesson, account.getUserCode());
        return true;
    }

    /**
     * 修改数据
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public boolean update(StudentLesson studentLessonUpdate) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        int rows = this.studentLessonService.update(studentLessonUpdate, account.getUserCode());
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
        int rows = this.studentLessonService.delete(id, account.getUserCode());
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
    public HashMap<String,Object> pageQuery(StudentLesson studentLessonQuery, @RequestParam(value = "page", required = false, defaultValue = "1") int pageNum, @RequestParam(value = "rows", required = false, defaultValue = "20") int pageSize, @RequestParam(value = "sidx", required = false, defaultValue = "ts") String sortName, @RequestParam(value = "sord", required = false, defaultValue = "desc") String sortOrder) {
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
        Page<StudentLesson> page = new Page<StudentLesson>(pageIndex, pageSize, sort);
        // 执行查询
        page = this.studentLessonService.selectPage(studentLessonQuery, page);
        // 返回查询结果
        HashMap<String,Object> map = new HashMap<String,Object>();
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        if (page!=null){
            map.put("data",page.getContent());
            map.put("count",page.getTotalElements());
            map.put("limit",page.getPageSize());
            map.put("page",page.getPageIndex());
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
    @RequestMapping(value = "/listQuery", method = RequestMethod.GET)
    @ResponseBody
    public List<StudentLesson> listQuery(String studentCode, @RequestParam(value = "page", required = false, defaultValue = "1") int pageNum, @RequestParam(value = "rows", required = false, defaultValue = "20") int pageSize, @RequestParam(value = "sidx", required = false, defaultValue = "ts") String sortName, @RequestParam(value = "sord", required = false, defaultValue = "desc") String sortOrder) {
        StudentLesson studentLesson =new StudentLesson();
        studentLesson.setStudentCode(studentCode);
        // 执行查询
        List<StudentLesson> studentLessonList = this.studentLessonService.selectList(studentLesson);
        // 返回查询结果
        return studentLessonList;
    }

    @RequestMapping(value = "/InitStudentLessonData", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> InitStudentLessonData(Model model, @RequestParam("fileUpload") MultipartFile file) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("status", 302);
        int rows = 0;// 实际导入行数
        // 最大导入条数
        Integer importNum = 5000;
        String fileName = file.getOriginalFilename();
        int lastIndexOfDot = fileName.lastIndexOf('.');
        int fileNameLength = fileName.length();
        String prefix = fileName.substring(lastIndexOfDot + 1, fileNameLength);
        Account account = AccountUtils.getCurrentAccount();
        try {
            if (prefix.toLowerCase().equals("xlsx") || prefix.toLowerCase().equals("xls")) {
                InputStream in = file.getInputStream();
                HSSFWorkbook wb = new HSSFWorkbook(in);
                List<InitStudentLessonFail> resultList = new ArrayList<>();
                // 读取工作表的数据第一个单元格
                HSSFSheet sheet = wb.getSheetAt(0);
                rows = sheet.getLastRowNum(); // 获得行数
                map.put("rows", rows);
                if (rows > importNum) {
                    map.put("msg", "一次性最多导入" + importNum + "条！！");
                    return map;
                }
                List<StudentLesson> studentLessonList = new ArrayList<>();
                for (int i = 1; i <= rows; i++) {
                    InitStudentLessonFail failData = new InitStudentLessonFail();
                    HSSFRow row = sheet.getRow(i);
                    if (row != null) {
                        StudentLesson initStudent = checkIsEmpty(failData,row,i);
                        if (failData.getFailReason() == null) {
                            studentLessonList.add(initStudent);
                        } else {
                            resultList.add(failData);
                        }
                    }
                }
                if (CollectionUtils.isEmpty(studentLessonList)) {
                    map.put("list", resultList);
                    map.put("size", resultList.size());
                    map.put("msg", "本次导入不存在有效的课程与学生关系数据！");
                    return map;
                }
                //导入前删除本次导入的excel中已经有的关系
                HashMap<String,Object> paramMap = new HashMap<>();
                for (StudentLesson studentLesson : studentLessonList) {
                    paramMap.put(studentLesson.getLessonCode()+"-"+studentLesson.getTerm(),studentLesson.getLessonCode()+"-"+studentLesson.getTerm());
                }
                if(!paramMap.isEmpty()){
                    List<StudentLesson> studentLessonDelList = new ArrayList<>();
                    for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                        String value = (String) entry.getValue();
                        String str[] = value.split("-");
                        String lessonCode = str[0];
                        String term = str[1];
                        StudentLesson studentLessonDel = new StudentLesson();
                        studentLessonDel.setTerm(term);
                        studentLessonDel.setLessonCode(lessonCode);
                        List<StudentLesson> delList = this.studentLessonService.selectList(studentLessonDel);
                        studentLessonDelList.addAll(delList);
                    }
                    if (!CollectionUtils.isEmpty(studentLessonDelList)){
                        List<Long> delIds = new ArrayList<>();
                        for (StudentLesson sl:studentLessonDelList) {
                            delIds.add(sl.getId());
                        }
                        if(!CollectionUtils.isEmpty(delIds)){
                            this.studentLessonService.delete(delIds,account.getUserCode());
                        }
                    }
                }
                //批量写入
                //一次100条
                List<StudentLesson> subList = new ArrayList<>();
                for (StudentLesson studentLesson : studentLessonList) {
                    subList.add(studentLesson);
                    if (subList.size() == 100) {
                        try {
                            this.studentLessonService.insert(subList,account.getUserCode());
                        } catch (Exception r) {
                            map.put("msg","写入失败：" + r.getMessage());
                        }
                        subList = new ArrayList<>();
                    }
                }
                //处理结余的
                if (subList.size() > 0) {
                    try {
                        this.studentLessonService.insert(subList,account.getUserCode());
                    } catch (Exception r) {
                        map.put("msg","写入失败：" + r.getMessage());
                    }
                }
                if (CollectionUtils.isNotEmpty(resultList)) {
                    map.put("list", resultList);
                    map.put("size", resultList.size());
                } else {
                    map.put("status", 200);
                    map.put("msg", "数据导入全部成功！");
                }
            } else {
                map.put("msg", "导入失败说明：文件格式不正确，仅支持xlsx和xls文件！");
            }
        } catch (Exception e) {
            map.put("msg", "导入失败说明：数据导入异常！");
            return map;
        }
        return map;
    }

    //根据规则 过滤一行中必须填的内容 是否为空
    private StudentLesson checkIsEmpty(InitStudentLessonFail data,HSSFRow row,int i) {
        StudentLesson studentLesson = new StudentLesson();
        StringBuilder reason = new StringBuilder("");
        reason.append("第").append(i + 1).append("行的");
        Boolean flag = true;
        try {
            if (row.getCell(0) == null || row.getCell(0).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("课程编码;");
                flag = false;
            }
            if (row.getCell(1) == null || row.getCell(1).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("课程名称;");
                flag = false;
            }
            if (row.getCell(2) == null || row.getCell(2).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("学号为空;");
                flag = false;
            }
            if (row.getCell(3) == null || row.getCell(3).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("姓名有误;");
                flag = false;
            }
            if (row.getCell(4) == null || row.getCell(4).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("学期有误;");
                flag = false;
            }
        } catch (Exception e) {
            flag = false;
        }
        if (!flag) {
            data.setStudentCode(row.getCell(2).toString());
            data.setLessonCode(row.getCell(0).toString());
            data.setFailReason(reason.toString());
            return new StudentLesson();
        }
        studentLesson.setTerm(row.getCell(4).toString().trim());
        studentLesson.setStudentName(row.getCell(3).toString().trim());
        studentLesson.setStudentCode(row.getCell(2).toString().trim());
        studentLesson.setLessonName(row.getCell(1).toString().trim());
        studentLesson.setLessonCode(row.getCell(0).toString().trim());
        return studentLesson;
    }
}