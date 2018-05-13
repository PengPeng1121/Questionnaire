/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.*;
import com.pp.basic.domain.vo.InitLessonFail;
import com.pp.basic.service.*;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import com.pp.web.account.Account;
import com.pp.web.common.SystemCommon;
import com.pp.web.controller.BaseController;
import com.pp.web.controller.until.AccountUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    LessonContrastService lessonContrastService;

    Logger log = LoggerFactory.getLogger(LessonController.class.getName());

    /**
     * 删除课程
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> delete(Long id) {
        Account account = AccountUtils.getCurrentAccount();
        HashMap<String,Object> map = new HashMap<>();
        map.put("status",300);
        if(!account.getRole().equals(SystemUser.AUTHOR_ADMIN)) {
            map.put("msg","为管理员操作，当前用户没有管理员权限");
            return map;
        }
        int rows = this.lessonService.delete(id, account.getUserCode());
        //todo 删除课程和老师关系
        //todo 删除学生和课程关系
        if (rows == 1) {
            map.put("status",200);
            return map;
        }
        map.put("msg","删除失败");
        return map;
    }

    /**
     * 详情查询
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String,Object> detail(String lessonCode) {

        Lesson lesson = new Lesson();
        lesson.setLessonCode(lessonCode);
        // 执行查询
        lesson = this.lessonService.selectOne(lesson);
        // 返回查询结果
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        if (lesson!=null){
            returnMap.put("lesson",lesson);
            returnMap.put("status",200);
        }else {
            returnMap.put("msg","没有查询到数据");
            returnMap.put("status",300);
        }
        return returnMap;
    }

    /**
     * 分页查询
     */
    @RequestMapping(value = "/pageQuery", method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public HashMap<String,Object> pageQuery(Lesson lessonQuery,
                                            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                            @RequestParam(value = "dir", required = false, defaultValue = "asc") String sortOrder,
                                            @RequestParam(value = "sd") String sortName) {
        // 设置合理的参数
        if (size < 1) {
            size = 20;
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
        Page<Lesson> lessonPage = new Page<Lesson>(pageIndex, size, sort);
        // 执行查询
        lessonPage = this.lessonService.selectPage(lessonQuery, lessonPage);
        // 返回查询结果
        HashMap<String,Object> map = new HashMap<String,Object>();
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        if (lessonPage!=null){
            if(!CollectionUtils.isEmpty(lessonPage.getContent())){
                for (Lesson lesson:lessonPage.getContent()){
                    StudentLesson studentLesson = new StudentLesson();
                    studentLesson.setLessonCode(lesson.getLessonCode());
                    studentLesson.setTerm(lesson.getTerm());
                    Long total = this.studentLessonService.count(studentLesson);
                    lesson.setStudentAccount(total);
                }
                map.put("data",lessonPage.getContent());
            }else {
                map.put("data",lessonPage.getContent());
            }
            map.put("count",lessonPage.getTotalElements());
            map.put("limit",lessonPage.getPageSize());
            map.put("page",lessonPage.getPageIndex()+1);
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
            List<Teacher> teacherList = this.teacherService.selectAll();
            if(CollectionUtils.isEmpty(teacherList)){
                map.put("msg", "请首先导入教师！！");
                return map;
            }
            HashMap<String,String> teacherMap = new HashMap<>();
            for (Teacher teacher:teacherList) {
                teacherMap.put(teacher.getTeacherName(),teacher.getTeacherCode());
            }
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
                List<LessonContrast> lessonContrasts = new ArrayList<>();
                for (int i = 2; i <= rows; i++) {
                    InitLessonFail failData = new InitLessonFail();
                    HSSFRow row = sheet.getRow(i);
                    if (row != null) {
                        Lesson lesson = new Lesson();
                        TeacherLesson teacherLesson = new TeacherLesson();
                        LessonContrast lessonContrast = new LessonContrast();
                        checkIsEmpty(failData,row,i,lesson,teacherLesson,teacherMap,lessonContrast);
                        if (failData.getFailReason() == null) {
                            lessonList.add(lesson);
                            teacherLessons.add(teacherLesson);
                            lessonContrasts.add(lessonContrast);
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
                //课程
                List<Lesson> subLessons = new ArrayList<>();
                List<TeacherLesson> subTeacherLessonList = new ArrayList<>();
                List<LessonContrast> subLessonContrastList = new ArrayList<>();
                for (Lesson lesson : lessonList) {
                    subLessons.add(lesson);
                    if (subLessons.size() == SystemCommon.INSERT_NUM_ONCE) {
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
                //教师与学生关系
                for (TeacherLesson teacherLesson : teacherLessons) {
                    subTeacherLessonList.add(teacherLesson);
                    if (subTeacherLessonList.size() == SystemCommon.INSERT_NUM_ONCE) {
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
                //课程对照表
                for (LessonContrast lessonContrast : lessonContrasts) {
                    subLessonContrastList.add(lessonContrast);
                    if (subLessonContrastList.size() == SystemCommon.INSERT_NUM_ONCE) {
                        try {
                            this.lessonContrastService.insert(subLessonContrastList,account.getUserCode());
                        } catch (Exception r) {
                            map.put("msg","写入失败：" + r.getMessage());
                        }
                        subLessonContrastList = new ArrayList<>();
                    }
                }
                if (subLessonContrastList.size() > 0) {
                    try {
                        this.lessonContrastService.insert(subLessonContrastList,account.getUserCode());
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
    private void checkIsEmpty(InitLessonFail data, HSSFRow row, int i, Lesson lesson, TeacherLesson teacherLesson, HashMap<String,String> teacherMap, LessonContrast lessonContrast) {
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
                reason.append("授课教师名称;");
                flag = false;
            }
            if (row.getCell(4) == null || row.getCell(4).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("学期;");
                flag = false;
            }
            if (row.getCell(5) == null || row.getCell(5).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("授课班级;");
                flag = false;
            }
            if (row.getCell(6) == null || row.getCell(6).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("是否必修;");
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
        lesson.setLessonClass(row.getCell(5).toString().trim());
        if (row.getCell(6).toString().trim().equals("是")){
            lesson.setIsMustCheck(1);
        }else  if (row.getCell(6).toString().trim().equals("否")){
            lesson.setIsMustCheck(0);
        }else {
            throw new IllegalArgumentException("是否必修类型错误！");
        }
        String teacherCode = teacherMap.get(row.getCell(3).toString().trim());
        if(StringUtils.isEmpty(teacherCode)){
            throw new IllegalArgumentException("没有该教师（！"+row.getCell(3).toString().trim()+")信息");
        }
        //本系统的课程编码为 东大的课程编码+"_"+教师编码+"_"+学期
        String lessonCode = row.getCell(0).toString().trim()+"_"+teacherCode+"_"+row.getCell(4).toString().trim();

        lesson.setLessonCode(lessonCode);
        lesson.setLessonName(row.getCell(1).toString().trim());
        lesson.setLessonTeacherCode(teacherCode);
        lesson.setLessonTeacherName(row.getCell(3).toString().trim());
        lesson.setTerm(row.getCell(4).toString().trim());

        teacherLesson.setLessonCode(lessonCode);
        teacherLesson.setLessonName(row.getCell(1).toString().trim());
        teacherLesson.setTeacherCode(lesson.getLessonTeacherCode());
        teacherLesson.setTeacherName(row.getCell(3).toString().trim());
        teacherLesson.setTerm(row.getCell(4).toString().trim());

        lessonContrast.setLessonCode(lessonCode);
        lessonContrast.setLessonCodeNeu(row.getCell(0).toString().trim());
    }

}