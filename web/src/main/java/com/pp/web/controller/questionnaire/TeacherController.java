/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.SystemUser;
import com.pp.basic.domain.Teacher;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        int rows = this.teacherService.delete(id, account.getUserCode());
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
    public HashMap<String,Object> pageQuery(Teacher teacherQuery,
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
        Page<Teacher> teacherPage = new Page<Teacher>(pageIndex, size, sort);
        // 执行查询
        teacherPage = this.teacherService.selectPage(teacherQuery, teacherPage);
        // 返回查询结果
        HashMap<String, Object> map = new HashMap<String, Object>();
        HashMap<String, Object> returnMap = new HashMap<String, Object>();
        if (teacherPage != null) {
            map.put("data", teacherPage.getContent());
            map.put("page", teacherPage.getPageIndex() + 1);
            map.put("count", teacherPage.getTotalElements());
            map.put("limit", teacherPage.getPageSize());
            returnMap.put("data", map);
            returnMap.put("status", 200);
        } else {
            returnMap.put("data", map);
            returnMap.put("msg", "没有查询到数据");
            returnMap.put("status", 300);
        }
        return returnMap;
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
                    //物理删除
                    if(!CollectionUtils.isEmpty(allTeacherIds)) {
                        this.teacherService.deletePhysically(allTeacherIds);
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