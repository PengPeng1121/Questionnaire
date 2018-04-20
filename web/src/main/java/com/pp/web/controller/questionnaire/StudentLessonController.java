/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.StudentLesson;
import com.pp.basic.domain.SystemUser;
import com.pp.basic.domain.Teacher;
import com.pp.basic.domain.vo.InitStudentLessonFail;
import com.pp.basic.service.StudentLessonService;
import com.pp.basic.service.TeacherService;
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
 * 学生选课信息表Controller
 *
 * @author
 */
@Controller
@RequestMapping("/web/studentlesson")
public class StudentLessonController extends BaseController {

    @Autowired
    StudentLessonService studentLessonService;

    @Autowired
    TeacherService teacherService;

    Logger log = LoggerFactory.getLogger(StudentLessonController.class.getName());

    /**
     * 分页查询
     */
    @RequestMapping(value = "/pageQuery", method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public HashMap<String,Object> pageQuery(StudentLesson studentLessonQuery,
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
        // 创建分页对象
        Page<StudentLesson> studentLessonPage = new Page<StudentLesson>(pageIndex, size, sort);
        // 执行查询
        studentLessonPage = this.studentLessonService.selectPage(studentLessonQuery, studentLessonPage);
        // 返回查询结果
        HashMap<String,Object> map = new HashMap<String,Object>();
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        if (studentLessonPage!=null){
            map.put("data",studentLessonPage.getContent());
            map.put("count",studentLessonPage.getTotalElements());
            map.put("limit",studentLessonPage.getPageSize());
            map.put("page",studentLessonPage.getPageIndex()+1);
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
                List<Teacher> teacherList = this.teacherService.selectAll();
                if(CollectionUtils.isEmpty(teacherList)){
                    map.put("msg", "请首先导入教师！！");
                    return map;
                }
                HashMap<String,String> teacherMap = new HashMap<>();
                for (Teacher teacher:teacherList) {
                    teacherMap.put(teacher.getTeacherName(),teacher.getTeacherCode());
                }
                List<StudentLesson> studentLessonList = new ArrayList<>();
                for (int i = 2; i <= rows; i++) {
                    InitStudentLessonFail failData = new InitStudentLessonFail();
                    HSSFRow row = sheet.getRow(i);
                    if (row != null) {
                        StudentLesson initStudent = checkIsEmpty(failData,row,i,teacherMap);
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
                    if (subList.size() == SystemCommon.INSERT_NUM_ONCE) {
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
            log.error("导入失败说明：数据导入异常！msg:"+e.getMessage());
            map.put("msg", "导入失败说明：数据导入异常！msg:"+e.getMessage());
            return map;
        }
        return map;
    }

    //根据规则 过滤一行中必须填的内容 是否为空
    private StudentLesson checkIsEmpty(InitStudentLessonFail data,HSSFRow row,int i,HashMap<String,String> teacherMap) {
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
            if (row.getCell(5) == null || row.getCell(5).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("教师名称有误;");
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
        String teacherCode = teacherMap.get(row.getCell(5).toString().trim());
        if(StringUtils.isEmpty(teacherCode)){
            throw new IllegalArgumentException("没有该教师（！"+row.getCell(5).toString().trim()+")信息");
        }
        //本系统的课程编码为 东大的课程编码+"_"+教师编码+"_"+学期
        String lessonCode = row.getCell(0).toString().trim()+"_"+teacherCode+"_"+row.getCell(4).toString().trim();

        studentLesson.setTerm(row.getCell(4).toString().trim());
        studentLesson.setStudentName(row.getCell(3).toString().trim());
        studentLesson.setStudentCode(row.getCell(2).toString().trim());
        studentLesson.setLessonName(row.getCell(1).toString().trim());
        studentLesson.setLessonCode(lessonCode);
        return studentLesson;
    }

    /**
     * 自动维护必修课选课关系
     */
    @RequestMapping(value = "/autoInitRelation", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> autoInitRelation(StudentLesson studentlesson) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("status",300);
        if(StringUtils.isEmpty(studentlesson.getTerm())){
            map.put("msg","学期不能为空");
            return map;
        }
        Account account = AccountUtils.getCurrentAccount();
        if(!account.getRole().equals(SystemUser.AUTHOR_ADMIN)) {
            map.put("msg","为管理员操作，当前用户没有管理员权限");
            return map;
        }
        studentlesson.setUpdateUser(account.getUserCode());
        try {
            this.studentLessonService.autoInitRelation(studentlesson);
        }catch (Exception e){
            log.error("自动维护必修课选课关系失败"+e.getMessage(),e);
            map.put("msg",e.getMessage());
            return map;
        }
        map.put("status",200);
        return map;
    }
}