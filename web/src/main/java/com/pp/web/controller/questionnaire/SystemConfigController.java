/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.QuestionnaireStudent;
import com.pp.basic.domain.SystemConfig;
import com.pp.basic.service.QuestionnaireStudentService;
import com.pp.basic.service.SystemConfigService;
import com.pp.common.core.Page;
import com.pp.web.account.Account;
import com.pp.web.controller.BaseController;
import com.pp.web.controller.until.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 系统配置Controller
 * 
 * @author
 */
@Controller
@RequestMapping("/web/systemconfig")
public class SystemConfigController extends BaseController {

    @Autowired
    SystemConfigService systemConfigService;

    @Autowired
    QuestionnaireStudentService questionnaireStudentService;

    /**
     * 显示列表页面
     */
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public String listPage() {
        return "SystemConfig/system_config_list";
    }

    /**
     * 显示新增页面
     */
    @RequestMapping(value = "/addPage", method = RequestMethod.GET)
    public String addPage() {
        return "SystemConfig/system_config_add";
    }

    /**
     * 显示修改页面
     */
    @RequestMapping(value = "/editPage", method = RequestMethod.GET)
    public String editPage(Long id, Model model) {
        //TODO 数据验证
        return "SystemConfig/system_config_edit";
    }

    /**
     * 保存数据
     */
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public boolean insert(SystemConfig systemConfig) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        this.systemConfigService.insert(systemConfig, account.getUserCode());
        return true;
    }

    /**
     * 修改数据
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public boolean update(SystemConfig systemConfigUpdate) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        int rows = this.systemConfigService.update(systemConfigUpdate, account.getUserCode());
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
        int rows = this.systemConfigService.delete(id, account.getUserCode());
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
    public Page<SystemConfig> pageQuery(SystemConfig systemConfigQuery, @RequestParam(value = "page", required = false, defaultValue = "1") int pageNum, @RequestParam(value = "rows", required = false, defaultValue = "20") int pageSize, @RequestParam(value = "sidx", required = false, defaultValue = "ts") String sortName, @RequestParam(value = "sord", required = false, defaultValue = "desc") String sortOrder) {
        //TODO 数据验证

        Page<SystemConfig> page = this.buildPage(pageNum, pageSize, sortName, sortOrder);
        // 执行查询
        page = this.systemConfigService.selectPage(systemConfigQuery, page);
        // 返回查询结果
        return page;
    }

    /**
     * 查询提醒问卷
     */
    @RequestMapping(value = "/findRemindQuestionnaire", method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public HashMap<String,Object> findRemindQuestionnaire() {
        //TODO 数据验证

        // 设置合理的参数
        int pageNum = 1;
        int pageSize = 20;
        // 排序--默认
        String sortName="ts";
        String sortOrder = "desc";
        // 创建分页对象
        Page<QuestionnaireStudent> page = this.buildPage(pageNum, pageSize, sortName, sortOrder);
        Account account = AccountUtils.getCurrentAccount();
        QuestionnaireStudent questionnaireStudentQuery = new QuestionnaireStudent();
        questionnaireStudentQuery.setStudentCode(account.getUserCode());
        questionnaireStudentQuery.setQuestionnaireProcessStatusCode(QuestionnaireStudent.PROCESS_CODE_UNDO);
        List<QuestionnaireStudent> list =this.questionnaireStudentService.selectList(questionnaireStudentQuery);
        //声明
        List<SystemConfig> configQuestionnaires = new ArrayList<>();
        if(list!=null&&list.size()>0){
            for (QuestionnaireStudent student:list) {
                SystemConfig configQuestionnaire = new SystemConfig();
                configQuestionnaire.setQuestionnaireCode(student.getQuestionnaireCode());
                configQuestionnaires.add(configQuestionnaire);
            }
        }
        // 返回查询结果
        HashMap<String,Object> map = new HashMap<String,Object>();
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        map.put("data",page.getContent());
        map.put("count",page.getTotalElements());
        map.put("limit",page.getPageSize());
        map.put("page",page.getPageIndex());
        returnMap.put("data",map);
        returnMap.put("status",200);
        return returnMap;
    }
}