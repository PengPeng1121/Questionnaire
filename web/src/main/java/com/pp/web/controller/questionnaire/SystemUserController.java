/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.SystemUser;
import com.pp.basic.service.SystemUserService;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import com.pp.web.account.Account;
import com.pp.web.controller.BaseController;
import com.pp.web.controller.until.AccountUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

/**
 * 学生信息表Controller
 *
 * @author
 */
@Controller
@RequestMapping("/web/systemuser")
public class SystemUserController extends BaseController {

    Logger log = LoggerFactory.getLogger(SystemUserController.class.getName());

    @Autowired
    SystemUserService systemUserService;

    /**
     * 保存数据
     */
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public boolean insert(SystemUser systemUser) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        this.systemUserService.insert(systemUser, account.getUserCode());
        return true;
    }

    /**
     * 修改数据
     */
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> update(String userCode ,String passwordNew ,String passwordOld) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        returnMap.put("status",300);
        try {
            if(StringUtils.isEmpty(userCode) && StringUtils.isEmpty(passwordNew)&& StringUtils.isEmpty(passwordOld)){
                returnMap.put("msg","请求参数错误");
                return returnMap;
            }
            SystemUser systemUserUpdate = new SystemUser();
            systemUserUpdate.setUserCode(userCode);
            if(!systemUserService.exists(systemUserUpdate)){
                returnMap.put("msg","没有该用户信息，请确认");
                return returnMap;
            }
            systemUserUpdate =systemUserService.selectOne(systemUserUpdate);
            log.info(account.getUserName()+"在进行修改密码操作");
            //管理员可以修改任何人密码  并且不需要校验密码是否一致
            if(!(account.getRole().equals(SystemUser.AUTHOR_ADMIN))){
                if(systemUserUpdate.getUserCode()!=account.getUserCode()){
                    returnMap.put("msg","您不能进行该操作");
                    return returnMap;
                }else if(!(passwordOld.equals(systemUserUpdate.getUserPassword()))){
                    returnMap.put("msg","原密码输入不正确");
                    return returnMap;
                }
                systemUserUpdate.setUserPassword(passwordNew);
            }else {
                systemUserUpdate.setUserPassword(passwordNew);
            }
            this.systemUserService.update(systemUserUpdate, account.getUserCode());
            returnMap.put("status",200);
        }catch (Exception e){
            log.error("密码修改失败：msg"+e.getMessage(),e);
            returnMap.put("msg",e.getMessage());
        }
        return returnMap;
    }

    /**
     * 逻辑删除数据
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public boolean delete(Long id) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        int rows = this.systemUserService.delete(id, account.getUserCode());
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
    public Page<SystemUser> pageQuery(SystemUser systemUserQuery, @RequestParam(value = "page", required = false, defaultValue = "1") int pageNum, @RequestParam(value = "rows", required = false, defaultValue = "20") int pageSize, @RequestParam(value = "sidx", required = false, defaultValue = "ts") String sortName, @RequestParam(value = "sord", required = false, defaultValue = "desc") String sortOrder) {
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
        Page<SystemUser> page = new Page<SystemUser>(pageIndex, pageSize, sort);
        // 执行查询
        page = this.systemUserService.selectPage(systemUserQuery, page);
        // 返回查询结果
        return page;
    }

}