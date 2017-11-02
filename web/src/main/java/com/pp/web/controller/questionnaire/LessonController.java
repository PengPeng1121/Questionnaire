/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.Lesson;
import com.pp.basic.domain.StudentLesson;
import com.pp.basic.domain.Teacher;
import com.pp.basic.domain.TeacherLesson;
import com.pp.basic.domain.vo.InitLessonFail;
import com.pp.basic.service.LessonService;
import com.pp.basic.service.StudentLessonService;
import com.pp.basic.service.TeacherLessonService;
import com.pp.basic.service.TeacherService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

/**
 * 课程信息表Controller
 *
 * @author
 */
@Controller
@RequestMapping("/web/lesson")
public class LessonController extends BaseController {

    @Autowired
    LessonService lessonService;

    @Autowired
    TeacherLessonService teacherLessonService;

    @Autowired
    TeacherService teacherService;

    @Autowired
    StudentLessonService studentLessonService;

    Logger log = LoggerFactory.getLogger(LessonController.class.getName());
    /**
     * 保存数据
     */
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public boolean insert(Lesson lesson) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        this.lessonService.insert(lesson, account.getUserCode());
        return true;
    }

    /**
     * 修改数据
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public boolean update(Lesson lessonUpdate) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        int rows = this.lessonService.update(lessonUpdate, account.getUserCode());
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
        int rows = this.lessonService.delete(id, account.getUserCode());
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
    public HashMap<String,Object> pageQuery(Lesson lessonQuery, @RequestParam(value = "page", required = false, defaultValue = "1") int pageNum, @RequestParam(value = "rows", required = false, defaultValue = "20") int pageSize, @RequestParam(value = "sidx", required = false, defaultValue = "ts") String sortName, @RequestParam(value = "sord", required = false, defaultValue = "desc") String sortOrder) {
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
        Page<Lesson> page = new Page<Lesson>(pageIndex, pageSize, sort);
        // 执行查询
        page = this.lessonService.selectPage(lessonQuery, page);
        // 返回查询结果
        HashMap<String,Object> map = new HashMap<String,Object>();
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        if (page!=null){
            if(!CollectionUtils.isEmpty(page.getContent())){
                for (Lesson lesson:page.getContent()){
                    StudentLesson studentLesson = new StudentLesson();
                    studentLesson.setLessonCode(lesson.getLessonCode());
                    studentLesson.setTerm(lesson.getTerm());
                    Long total = this.studentLessonService.count(studentLesson);
                    lesson.setStudentAccount(total);
                }
                map.put("data",page.getContent());
            }else {
                map.put("data",page.getContent());
            }
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
    @RequestMapping(value = "/listQuery", method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public List<Lesson> listQuery(String teacherCode) {
        Lesson lessonQuery = new Lesson();

        lessonQuery.setLessonTeacherCode(teacherCode);
        // 执行查询
        List<Lesson> lessons= this.lessonService.selectList(lessonQuery);
        // 返回查询结果
        return lessons;
    }

    @RequestMapping(value = "/InitLessonData", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> InitLessonData(Model model, @RequestParam("fileLessonUpload") MultipartFile file) {
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
                List<InitLessonFail> resultList = new ArrayList<InitLessonFail>();
                // 读取工作表的数据第一个单元格
                HSSFSheet sheet = wb.getSheetAt(0);
                rows = sheet.getLastRowNum(); // 获得行数
                map.put("rows", rows);
                if (rows > importNum) {
                    map.put("msg", "一次性最多导入" + importNum + "条！！");
                    return map;
                }
                List<Lesson> lessonList = new ArrayList<>();
                List<TeacherLesson> teacherLessons = new ArrayList<>();
                List<Teacher> teachers = new ArrayList<>();
                HashSet<String> teacherSets = new HashSet<>();
                for (int i = 2; i <= rows; i++) {
                    InitLessonFail failData = new InitLessonFail();
                    HSSFRow row = sheet.getRow(i);
                    if (row != null) {
                        Lesson lesson = new Lesson();
                        TeacherLesson teacherLesson = new TeacherLesson();
                        Teacher teacher = new Teacher();
                        checkIsEmpty(failData,row,i,lesson,teacherLesson,teacher,teacherSets);
                        if (failData.getFailReason() == null) {
                            lessonList.add(lesson);
                            teacherLessons.add(teacherLesson);
                            teachers.add(teacher);
                        } else {
                            resultList.add(failData);
                        }
                    }
                }
                if (CollectionUtils.isEmpty(lessonList)) {
                    map.put("list", resultList);
                    map.put("size", resultList.size());
                    map.put("msg", "本次课程导入不存在有效数据！！！！");
                    return map;
                }
                if (CollectionUtils.isEmpty(lessonList)) {
                    map.put("list", resultList);
                    map.put("size", resultList.size());
                    map.put("msg", "本次课程导入不存在有效数据！");
                    return map;
                }
                //课程
                List<Lesson> subLessons = new ArrayList<>();
                List<Teacher> subTeacherList = new ArrayList<>();
                List<TeacherLesson> subTeacherLessonList = new ArrayList<>();
                for (Lesson lesson : lessonList) {
                    subLessons.add(lesson);
                    if (subLessons.size() == 100) {
                        try {
                            this.lessonService.insert(subLessons,account.getUserCode());
                        } catch (Exception r) {
                            map.put("msg","写入失败：" + r.getMessage());
                        }
                        subLessons = new ArrayList<>();
                    }
                }
                if (subLessons.size() > 0) {
                    try {
                        this.lessonService.insert(subLessons,account.getUserCode());
                    } catch (Exception r) {
                        map.put("msg","写入失败：" + r.getMessage());
                    }
                }
                //教师
                for (Teacher teacher : teachers) {
                    if(teacher!=null &&teacher.getTeacherCode()!=null){
                        subTeacherList.add(teacher);
                        if (subTeacherList.size() == 100) {
                            try {
                                this.teacherService.insert(subTeacherList,account.getUserCode());
                            } catch (Exception r) {
                                map.put("msg","写入失败：" + r.getMessage());
                            }
                            subTeacherList = new ArrayList<>();
                        }
                    }
                }
                if (subTeacherList.size() > 0) {
                    try {
                        this.teacherService.insert(subTeacherList,account.getUserCode());
                    } catch (Exception r) {
                        map.put("msg","写入失败：" + r.getMessage());
                    }
                }
                //教师与学生关系
                for (TeacherLesson teacherLesson : teacherLessons) {
                    subTeacherLessonList.add(teacherLesson);
                    if (subTeacherLessonList.size() == 100) {
                        try {
                            this.teacherLessonService.insert(subTeacherLessonList,account.getUserCode());
                        } catch (Exception r) {
                            map.put("msg","写入失败：" + r.getMessage());
                        }
                        subTeacherLessonList = new ArrayList<>();
                    }
                }
                if (subTeacherLessonList.size() > 0) {
                    try {
                        this.teacherLessonService.insert(subTeacherLessonList,account.getUserCode());
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
            log.error("导入失败说明：数据导入异常！msg:"+e.getMessage());
            map.put("msg", "导入失败说明：数据导入异常！msg:"+e.getMessage());
            return map;
        }
        return map;
    }

    //根据规则 过滤一行中必须填的内容 是否为空
    private void checkIsEmpty(InitLessonFail data, HSSFRow row, int i, Lesson lesson, TeacherLesson teacherLesson, Teacher teacher, HashSet<String> teacherSets) {
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
                reason.append("课程类型;");
                flag = false;
            }
            if (row.getCell(3) == null || row.getCell(3).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("授课教师代码;");
                flag = false;
            }
            if (row.getCell(4) == null || row.getCell(4).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("授课教师名称;");
                flag = false;
            }
            if (row.getCell(5) == null || row.getCell(5).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("学期;");
                flag = false;
            }
            reason.append("不能为空");
        } catch (Exception e) {
            flag = false;
        }
        if (!flag) {
            data.setLessonCode(row.getCell(0).toString());
            data.setFailReason(reason.toString());
            return ;
        }
        if (row.getCell(2).toString().trim().equals("0")){
            lesson.setLessonTypeCode(row.getCell(2).toString());
            lesson.setLessonTypeName("实践课");
        }else  if (row.getCell(2).toString().trim().equals("1")){
            lesson.setLessonTypeCode(row.getCell(2).toString());
            lesson.setLessonTypeName("理论课");
        }else {
           throw new IllegalArgumentException("课程类型错误！");
        }
        lesson.setLessonCode(row.getCell(0).toString().trim());
        lesson.setLessonName(row.getCell(1).toString().trim());
        lesson.setLessonTeacherCode(row.getCell(3).toString().trim());
        lesson.setLessonTeacherName(row.getCell(4).toString().trim());
        lesson.setTerm(row.getCell(5).toString().trim());

        if(!teacherSets.contains(lesson.getLessonTeacherCode())){
            teacher.setTeacherCode(row.getCell(3).toString().trim());
            teacher.setTeacherName(row.getCell(4).toString().trim());
        }
        teacherSets.add(lesson.getLessonTeacherCode());

        teacherLesson.setLessonCode(row.getCell(0).toString().trim());
        teacherLesson.setLessonName(row.getCell(1).toString().trim());
        teacherLesson.setTeacherCode(row.getCell(3).toString().trim());
        teacherLesson.setTeacherName(row.getCell(4).toString().trim());
        teacherLesson.setTerm(row.getCell(5).toString().trim());
    }

}