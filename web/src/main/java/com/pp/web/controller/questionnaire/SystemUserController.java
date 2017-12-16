/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pp.basic.domain.Student;
import com.pp.basic.domain.SystemUser;
import com.pp.basic.domain.vo.Answer;
import com.pp.basic.service.StudentService;
import com.pp.basic.service.SystemUserService;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import com.pp.web.account.Account;
import com.pp.web.controller.BaseController;
import com.pp.web.controller.until.AccountUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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

    @Autowired
    StudentService studentService;
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
     * 保存数据
     */
    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
    @ResponseBody
    public  HashMap<String,Object> updateUser(HttpServletRequest request,String userPassword, String userCode, String userName) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        returnMap.put("status",300);
        try {
            BufferedReader reader = request.getReader();
            String input = "";
            StringBuffer requestBody = new StringBuffer();
            while ((input = reader.readLine()) != null) {
                requestBody.append(input);
            }
            SystemUser systemUser = new SystemUser();
//            String password;
//            String userName;
//            if (org.apache.commons.lang.StringUtils.isEmpty(requestBody.toString())) {
//                throw new IllegalArgumentException("至少包含一条数据！");
//            } else {
//                JSONObject jsonObject = new JSONObject(requestBody.toString());
//                password = (String) jsonObject.get("userPassword");
//                userName = (String) jsonObject.get("userName");
//            }
            if (!account.getRole().equals(SystemUser.AUTHOR_ADMIN)) {
                returnMap.put("msg", "为管理员操作，当前用户没有管理员权限");
                return returnMap;
            }
            systemUser.setUserCode(userCode);
            systemUser = this.systemUserService.selectOne(systemUser);
            if(systemUser == null){
                returnMap.put("msg","没有查询到该用户信息！");
            }
            if(StringUtils.isNotEmpty(userPassword)){
                systemUser.setUserPassword(userPassword);
            }
            if(StringUtils.isNoneEmpty(userName)){
                systemUser.setUserName(userName);
            }
            this.systemUserService.update(systemUser, account.getUserCode());
            returnMap.put("status",200);
        }catch (Exception e){
            returnMap.put("msg",e.getMessage());
        }
        return returnMap;
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
                if(!systemUserUpdate.getUserCode().equals(account.getUserCode())){
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
     * 逻辑删除用户   同时删除学生
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> delete(Long id) {
        //TODO 数据验证
        Account account = AccountUtils.getCurrentAccount();
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        returnMap.put("status",300);
        try {
            SystemUser systemUser = this.systemUserService.selectOne(id);
            this.systemUserService.delete(id, account.getUserCode());
            Student student = new Student();
            if(systemUser!=null){
                student.setStudentCode(systemUser.getUserCode());
                student = this.studentService.selectOne(student);
                if(student!=null){
                    this.studentService.delete(student.getId(), account.getUserCode());
                }else {
                    return returnMap;
                }
            }
            returnMap.put("status",200);
        }catch (Exception e){
            returnMap.put("msg",e.getMessage());
            return returnMap;
        }
        return returnMap;
    }

    /**
     * 分页查询
     */
    @RequestMapping(value = "/showSystemUser", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String,Object> pageQuery(SystemUser systemUserQuery){
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        // 执行查询
        SystemUser systemUser = this.systemUserService.selectOne(systemUserQuery);
        if (systemUser!=null){
            returnMap.put("status",200);
            returnMap.put("systemUser",systemUser);
        }else {
            returnMap.put("status",300);
            returnMap.put("msg","没有查询到该用户信息！");
        }
        // 返回查询结果
        return returnMap;
    }

}