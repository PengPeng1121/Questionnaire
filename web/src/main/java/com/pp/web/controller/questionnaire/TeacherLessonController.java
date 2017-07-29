/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

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
import com.pp.basic.domain.TeacherLesson;
import com.pp.basic.service.TeacherLessonService;

/**
 * 教师授课信息表Controller
 * 
 * @author
 */
@Controller
@RequestMapping("/web/teacherlesson")
public class TeacherLessonController extends BaseController {

    @Autowired
    TeacherLessonService teacherLessonService;

    /**
     * 显示列表页面
     */
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public String listPage() {
        return "common/core/TeacherLesson/teacher_lesson_list";
    }

    /**
     * 显示新增页面
     */
    @RequestMapping(value = "/addPage", method = RequestMethod.GET)
    public String addPage() {
        return "common/core/TeacherLesson/teacher_lesson_add";
    }

    /**
     * 显示修改页面
     */
    @RequestMapping(value = "/editPage", method = RequestMethod.GET)
    public String editPage(Long id, Model model) {
        //TODO 数据验证
        return "common/core/TeacherLesson/teacher_lesson_edit";
    }

    /**
     * 保存数据
     */
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public boolean insert(TeacherLesson teacherLesson) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        this.teacherLessonService.insert(teacherLesson, account.getUserCode());
        return true;
    }

    /**
     * 修改数据
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public boolean update(TeacherLesson teacherLessonUpdate) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        int rows = this.teacherLessonService.update(teacherLessonUpdate, account.getUserCode());
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
        int rows = this.teacherLessonService.delete(id, account.getUserCode());
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
    public Page<TeacherLesson> pageQuery(TeacherLesson teacherLessonQuery, @RequestParam(value = "page", required = false, defaultValue = "1") int pageNum, @RequestParam(value = "rows", required = false, defaultValue = "20") int pageSize, @RequestParam(value = "sidx", required = false, defaultValue = "ts") String sortName, @RequestParam(value = "sord", required = false, defaultValue = "desc") String sortOrder) {
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
        Page<TeacherLesson> page = new Page<TeacherLesson>(pageIndex, pageSize, sort);
        // 执行查询
        page = this.teacherLessonService.selectPage(teacherLessonQuery, page);
        // 返回查询结果
        return page;
    }

}