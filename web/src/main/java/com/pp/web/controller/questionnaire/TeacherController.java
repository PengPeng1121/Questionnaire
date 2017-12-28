/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.Lesson;
import com.pp.basic.domain.Teacher;
import com.pp.basic.domain.TeacherLesson;
import com.pp.basic.domain.vo.InitLessonFail;
import com.pp.basic.service.TeacherService;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import com.pp.web.account.Account;
import com.pp.web.common.SystemCommon;
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
 * 教师信息表Controller
 *
 * @author
 */
@Controller
@RequestMapping("/web/teacher")
public class TeacherController extends BaseController {

    Logger log = LoggerFactory.getLogger(LessonController.class.getName());

    @Autowired
    TeacherService teacherService;
    /**
     * 保存数据
     */
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public boolean insert(Teacher teacher) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        this.teacherService.insert(teacher, account.getUserCode());
        return true;
    }

    /**
     * 修改数据
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public boolean update(Teacher teacherUpdate) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        int rows = this.teacherService.update(teacherUpdate, account.getUserCode());
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
        int rows = this.teacherService.delete(id, account.getUserCode());
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
    public Page<Teacher> pageQuery(Teacher teacherQuery, @RequestParam(value = "page", required = false, defaultValue = "1") int pageNum, @RequestParam(value = "rows", required = false, defaultValue = "20") int pageSize, @RequestParam(value = "sidx", required = false, defaultValue = "ts") String sortName, @RequestParam(value = "sord", required = false, defaultValue = "desc") String sortOrder) {
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
        Page<Teacher> page = new Page<Teacher>(pageIndex, pageSize, sort);
        // 执行查询
        page = this.teacherService.selectPage(teacherQuery, page);
        // 返回查询结果
        return page;
    }

    @RequestMapping(value = "/InitTeacherData", method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> InitTeacherData(Model model, @RequestParam("fileUpload") MultipartFile file) {
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
                List<Teacher> teachers = new ArrayList<>();
                for (int i = 2; i <= rows; i++) {
                    InitLessonFail failData = new InitLessonFail();
                    HSSFRow row = sheet.getRow(i);
                    if (row != null) {
                        Teacher teacher = new Teacher();
                        checkIsEmpty(failData,row,i,teacher);
                        if (failData.getFailReason() == null) {
                            teachers.add(teacher);
                        } else {
                            resultList.add(failData);
                        }
                    }
                }
                List<Teacher> subTeacherList = new ArrayList<>();
                //删除所有教师
                //导入之前清空数据
                try {
                    List<Teacher> allTeacher = this.teacherService.selectAll();
                    List<Long> allTeacherIds = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(allTeacher)){
                        for (Teacher teacher:allTeacher) {
                            allTeacherIds.add(teacher.getId());
                        }
                    }
                    if(!CollectionUtils.isEmpty(allTeacherIds)) {
                        this.teacherService.delete(allTeacherIds, account.getUserCode());
                    }
                }catch (Exception e){
                    map.put("msg", "导入失败说明：删除之前数据失败！msg:"+e.getMessage());
                    return map;
                }
                //教师
                for (Teacher teacher : teachers) {
                    if(teacher!=null &&teacher.getTeacherCode()!=null){
                        subTeacherList.add(teacher);
                        if (subTeacherList.size() == SystemCommon.INSERT_NUM_ONCE) {
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
    private void checkIsEmpty(InitLessonFail data, HSSFRow row, int i, Teacher teacher) {
        StringBuilder reason = new StringBuilder("");
        reason.append("第").append(i + 1).append("行的");
        Boolean flag = true;
        try {
            if (row.getCell(0) == null || row.getCell(0).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("教师编码;");
                flag = false;
            }
            if (row.getCell(1) == null || row.getCell(1).getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                reason.append("教师名称;");
                flag = false;
            }
            reason.append("不能为空");
        } catch (Exception e) {
            flag = false;
        }
        if (!flag) {
            return ;
        }
        teacher.setTeacherCode(row.getCell(0).toString().trim());
        teacher.setTeacherName(row.getCell(1).toString().trim());
    }
}