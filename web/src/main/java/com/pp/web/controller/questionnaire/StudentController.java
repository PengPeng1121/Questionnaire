/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.Student;
import com.pp.basic.domain.SystemUser;
import com.pp.basic.domain.vo.InitStudent;
import com.pp.basic.domain.vo.InitStudentFail;
import com.pp.basic.service.StudentService;
import com.pp.basic.service.SystemUserService;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import com.pp.web.account.Account;
import com.pp.web.common.SystemCommon;
import com.pp.web.controller.BaseController;
import com.pp.web.controller.until.AccountUtils;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

/**
 * 学生信息表Controller
 *
 * @author
 */
@Controller
@RequestMapping("/web/student")
public class StudentController extends BaseController {

    @Autowired
    StudentService studentService;

    @Autowired
    SystemUserService systemUserService;

    Logger log = LoggerFactory.getLogger(StudentController.class.getName());

    /**
     * 分页查询
     */
    @RequestMapping(value = "/pageQuery", method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public HashMap<String,Object> pageQuery(Student studentQuery,
                                            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                            @RequestParam(value = "dir", required = false, defaultValue = "asc") String sortOrder,
                                            @RequestParam(value = "sd") String sortName) {
        // 设置合理的参数
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
        Page<Student> studentPage = new Page<Student>(pageIndex, size, sort);
        // 执行查询
        studentPage = this.studentService.selectPage(studentQuery, studentPage);
        // 返回查询结果
        HashMap<String,Object> map = new HashMap<String,Object>();
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        if (studentPage!=null){
            map.put("limit",studentPage.getPageSize());
            map.put("count",studentPage.getTotalElements());
            map.put("data",studentPage.getContent());
            map.put("page",studentPage.getPageIndex()+1);
            returnMap.put("status",200);
            returnMap.put("data",map);
        }else {
            returnMap.put("data",map);
            returnMap.put("msg","没有查询到数据");
            returnMap.put("status",300);
        }
        return returnMap;
    }

    @RequestMapping(value = "/InitStudentData", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> InitStudentData(Model model, @RequestParam("fileUpload") MultipartFile file) {
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
                List<InitStudentFail> resultList = new ArrayList<InitStudentFail>();
                // 读取工作表的数据第一个单元格
                HSSFSheet sheet = wb.getSheetAt(0);
                rows = sheet.getLastRowNum(); // 获得行数
                map.put("rows", rows);
                if (rows > importNum) {
                    map.put("msg", "一次性最多导入" + importNum + "条！！");
                    return map;
                }
                List<InitStudent> InitStudentList = new ArrayList<>();
                for (int i = 2; i <= rows; i++) {
                    InitStudentFail failData = new InitStudentFail();
                    HSSFRow row = sheet.getRow(i);
                    if (row != null) {
                        InitStudent initStudent = checkIsEmpty(failData,row,i);
                        if (failData.getFailReason() == null) {
                            InitStudentList.add(initStudent);
                        } else {
                            resultList.add(failData);
                        }
                    }
                }
                if (CollectionUtils.isEmpty(InitStudentList)) {
                    map.put("list", resultList);
                    map.put("size", resultList.size());
                    map.put("msg", "本次导入不存在有效数据！");
                    return map;
                }
                //批量写入
                List<InitStudentFail> importFailedList = new ArrayList<>();
                List<InitStudent> dealList = this.handleRepeatData(InitStudentList, resultList);
                if (CollectionUtils.isEmpty(dealList)) {
                    map.put("list", resultList);
                    map.put("size", resultList.size());
                    map.put("msg", "本次导入不存在有效数据！！");
                    return map;
                }
                //导入之前清空数据
                try {
                    List<Student> allStudent = this.studentService.selectAll();
                    SystemUser systemUser = new SystemUser();
                    //删除用户表中的普通用户
                    systemUser.setUserAuthority(SystemUser.AUTHOR_USER);
                    List<SystemUser> allSystemUser = this.systemUserService.selectList(systemUser);
                    List<Long> allStudentIds = new ArrayList<>();
                    List<Long> allSystemUserIds = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(allStudent)){
                        for (Student student:allStudent) {
                            allStudentIds.add(student.getId());
                        }
                    }
                    if (!CollectionUtils.isEmpty(allSystemUser)){
                        for (SystemUser systemUser1:allSystemUser) {
                            allSystemUserIds.add(systemUser1.getId());
                        }
                    }
                    //物理删除
                    if(!CollectionUtils.isEmpty(allSystemUserIds)){
                        this.systemUserService.deletePhysically(allSystemUserIds);
                    }
                    if(!CollectionUtils.isEmpty(allStudentIds)) {
                        this.studentService.deletePhysically(allStudentIds);
                    }
                }catch (Exception e){
                    map.put("msg", "导入失败说明：删除之前数据失败！msg:"+e.getMessage());
                    return map;
                }
                //一次100条
                List<InitStudent> subList = new ArrayList<>();
                for (InitStudent initStudent : dealList) {
                    subList.add(initStudent);
                    if (subList.size() == SystemCommon.INSERT_NUM_ONCE) {
                        try {
                            importFailedList = this.studentService.importStudent(subList,account.getUserCode());
                            this.systemUserService.importSystemUser(subList,account.getUserCode());
                        } catch (Exception r) {
                            map.put("msg","写入失败：" + r.getMessage());
                        }
                        subList = new ArrayList<>();
                        //将几次结果都写入
                        resultList.addAll(importFailedList);
                    }
                }
                //处理结余的
                if (subList.size() > 0) {
                    try {
                        importFailedList = this.studentService.importStudent(subList, account.getUserCode());
                        this.systemUserService.importSystemUser(subList,account.getUserCode());
                    } catch (Exception r) {
                        map.put("msg","写入失败：" + r.getMessage());
                    }
                }
                //将几次结果都写入
                resultList.addAll(importFailedList);
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
    private InitStudent checkIsEmpty(InitStudentFail data,HSSFRow row,int i) {
        InitStudent initStudent = new InitStudent();
        StringBuilder reason = new StringBuilder("");
        reason.append("第").append(i + 1).append("行的");
        Boolean flag = true;
        try {
            if (row.getCell(0) == null || row.getCell(0).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("学号为空;");
                flag = false;
            }
            if (row.getCell(1) == null || row.getCell(1).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("姓名有误;");
                flag = false;
            }
            if (row.getCell(2) == null || row.getCell(2).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("年级为空;");
                flag = false;
            }
            if (row.getCell(3) == null || row.getCell(3).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("班级为空;");
                flag = false;
            }
            if (row.getCell(4) == null || row.getCell(4).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("是否毕业为空;");
                flag = false;
            }
        } catch (Exception e) {
            flag = false;
        }
        if (!flag) {
            data.setStudentCode(row.getCell(0).toString());
            data.setFailReason(reason.toString());
            return new InitStudent();
        }

        //是否毕业
        String isStudentGraduate = row.getCell(4).toString();
        if(StringUtils.isNotBlank(isStudentGraduate)){
            if(isStudentGraduate.endsWith(".0")){
                isStudentGraduate = isStudentGraduate.substring(0,isStudentGraduate.indexOf("."));
            }else {
                isStudentGraduate = "99";//错误数据99
            }
        }
        initStudent.setIsStudentGraduate(Integer.parseInt(isStudentGraduate));
        //班级
        String studentClass = row.getCell(3).toString();
        if(studentClass.endsWith(".0")){
            studentClass = studentClass.substring(0,studentClass.indexOf("."));
        }
        initStudent.setStudentClass(studentClass);

//        initStudent.setStudentClass(row.getCell(3).toString().trim());

        //班级
        String studentGrade = row.getCell(2).toString().trim();
        if(studentGrade.endsWith(".0")){
            studentGrade = studentGrade.substring(0,studentGrade.indexOf("."));
        }
        initStudent.setStudentGrade(studentGrade);
//        initStudent.setStudentGrade(row.getCell(2).toString().trim());

        //姓名
        String studentName = row.getCell(1).toString().trim();
        if(studentName.endsWith(".0")){
            studentName = studentName.substring(0,studentName.indexOf("."));
        }
        initStudent.setStudentName(studentName);
//        initStudent.setStudentName(row.getCell(1).toString().trim());

        //学号
        String studentCode = row.getCell(0).toString().trim();
        if(studentName.endsWith(".0")){
            studentCode = studentCode.substring(0,studentCode.indexOf("."));
        }
        initStudent.setStudentCode(studentCode);
//        initStudent.setStudentCode(row.getCell(0).toString().trim());
        return initStudent;
    }

    /**
     * 过滤重复的学号，返回过滤之后的
     *
     * @param InitStudentList 待处理的数据
     * @param retFailDataList    校验失败的数据
     * @return 过滤之后的列表
     */
    private List<InitStudent> handleRepeatData(List<InitStudent> InitStudentList, List<InitStudentFail> retFailDataList) {
        List<String> repeatSpareCodeList = new ArrayList<String>();
        Map<String, InitStudent> InitStudentMap = new HashMap<String, InitStudent>();
        for (InitStudent InitStudent : InitStudentList) {
            if (InitStudentMap.containsKey(InitStudent.getStudentCode())) {
                addFailData(retFailDataList, InitStudent, "重复的学号");
                repeatSpareCodeList.add(InitStudent.getStudentCode());
                continue;
            }
            InitStudentMap.put(InitStudent.getStudentCode(), InitStudent);
        }
        // 如果没有重复的，说明原来的就可以做下一步操作
        if (CollectionUtils.isEmpty(repeatSpareCodeList)) {
            return InitStudentList;
        }
        // 如果有重复的，把原来的要删掉，同时更新提示信息
        List<InitStudent> newList = new ArrayList<>();
        for (String str : repeatSpareCodeList) {
            InitStudent initData = InitStudentMap.get(str);
            if (null != initData) {
                addFailData(retFailDataList, initData, "重复的学号");
                InitStudentMap.remove(str);
            }
        }
        Iterator<String> iterator = InitStudentMap.keySet().iterator();
        while (iterator.hasNext()) {
            String spareCode = iterator.next();
            InitStudent initData = InitStudentMap.get(spareCode);
            if (null != initData) {
                newList.add(initData);
            }
        }
        // 数据清一下
        InitStudentMap.clear();
        InitStudentMap.clear();
        return newList;
    }

    /**
     * 将错误结果添加到返回结果中
     *
     * @param retFailDataList 返回结果
     * @param InitStudent  待导入数据
     * @param failReason      失败原因
     */
    private void addFailData(List<InitStudentFail> retFailDataList, InitStudent InitStudent, String failReason) {
        InitStudentFail InitStudentFail = new InitStudentFail();
        InitStudentFail.setFailReason(failReason);
        retFailDataList.add(InitStudentFail);
    }

    /**
     * 过滤掉无效的数据，如下情况会被过滤掉，添加到提示列表中：
     * 1、学号在库存中已存在的；
     *
     * @param dealList        待处理列表
     * @param retFailDataList 校验失败列表
     * @return 校验通过列表
     */
    private List<InitStudent> filterInvalidData(List<InitStudent> dealList, List<InitStudentFail> retFailDataList) {
        List<String> studentCodeList = new ArrayList<String>();
        for (InitStudent InitStudent : dealList) {
            studentCodeList.add(InitStudent.getStudentCode());
        }
        List<Student> students = this.studentService.selectAll();
        List<String> existStudentCodeList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(students)) {
            for (Student student : students) {
                existStudentCodeList.add(student.getStudentCode());
            }
            students.clear();
        }
        // 剩下的有效的数据
        List<InitStudent> newList = new ArrayList<InitStudent>();
        for (InitStudent InitStudent : dealList) {
            if (existStudentCodeList.contains(InitStudent.getStudentCode())) {
                this.addFailData(retFailDataList, InitStudent, "该学号在库存中已存在");
                continue;
            }
            newList.add(InitStudent);
        }
        return newList;
    }
}