/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.Questionnaire;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import com.pp.web.account.Account;
import com.pp.web.common.SystemCommon;
import com.pp.web.controller.BaseController;
import com.pp.web.controller.until.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pp.basic.domain.SystemConstant;
import com.pp.basic.service.SystemConstantService;

import java.util.HashMap;
import java.util.List;

/**
 * 系统常量Controller
 * 
 * @author
 */
@Controller
@RequestMapping("/web/systemconstant")
public class SystemConstantController extends BaseController {

    @Autowired
    SystemConstantService systemConstantService;

    /**
     * 显示列表页面
     */
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public String listPage() {
        return "SystemConstant/system_constant_list";
    }

    /**
     * 显示新增页面
     */
    @RequestMapping(value = "/addPage", method = RequestMethod.GET)
    public String addPage() {
        return "SystemConstant/system_constant_add";
    }

    /**
     * 显示修改页面
     */
    @RequestMapping(value = "/editPage", method = RequestMethod.GET)
    public String editPage(Long id, Model model) {
        //TODO 数据验证
        return "SystemConstant/system_constant_edit";
    }

    /**
     * 保存数据
     */
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public boolean insert(SystemConstant systemConstant) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        this.systemConstantService.insert(systemConstant, account.getUserCode());
        return true;
    }

    /**
     * 修改数据
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public boolean update(SystemConstant systemConstantUpdate) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        int rows = this.systemConstantService.update(systemConstantUpdate, account.getUserCode());
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
        int rows = this.systemConstantService.delete(id, account.getUserCode());
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
    public Page<SystemConstant> pageQuery(SystemConstant systemConstantQuery, @RequestParam(value = "page", required = false, defaultValue = "1") int pageNum, @RequestParam(value = "rows", required = false, defaultValue = "20") int pageSize, @RequestParam(value = "sidx", required = false, defaultValue = "ts") String sortName, @RequestParam(value = "sord", required = false, defaultValue = "desc") String sortOrder) {
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
        Page<SystemConstant> page = new Page<SystemConstant>(pageIndex, pageSize, sort);
        // 执行查询
        page = this.systemConstantService.selectPage(systemConstantQuery, page);
        // 返回查询结果
        return page;
    }



    /**
     * 设置策略
     */
    @RequestMapping(value = "/postStrategy", method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public HashMap<String,Object> postStrategy(Integer day) {

        Account account = AccountUtils.getCurrentAccount();
        //先根据类型删除
        SystemConstant systemConstant = new SystemConstant();
        systemConstant.setConstantType(SystemConstant.CONSTANT_TYPE_EXPIRES);
        systemConstant.setIsDelete(1);
        this.systemConstantService.update(systemConstant,account.getUserCode());
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        returnMap.put("status",200);

        if(day==null){
            returnMap.put("status",500);
            returnMap.put("msg","超时时间不能为空！");
        }
        systemConstant.setConstant(day.toString());
        systemConstant.setConstantType(SystemConstant.CONSTANT_TYPE_EXPIRES);
        systemConstant.setIsDelete(0);
        this.systemConstantService.insert(systemConstant,account.getUserCode());

        return returnMap;
    }

    /**
     * 设置策略
     */
    @RequestMapping(value = "/getStrategy", method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public HashMap<String,Object> getStrategy() {
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        SystemConstant systemConstant = new SystemConstant();
        systemConstant.setConstantType(SystemConstant.CONSTANT_TYPE_EXPIRES);
        systemConstant = this.systemConstantService.selectOne(systemConstant);
        returnMap.put("data",systemConstant);
        returnMap.put("status",200);
        return returnMap;
    }
}