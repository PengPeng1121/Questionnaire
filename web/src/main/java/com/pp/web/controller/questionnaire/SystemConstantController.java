/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.Questionnaire;
import com.pp.basic.domain.SystemConfig;
import com.pp.basic.domain.SystemConstant;
import com.pp.basic.domain.SystemUser;
import com.pp.basic.service.QuestionnaireService;
import com.pp.basic.service.SystemConfigService;
import com.pp.basic.service.SystemConstantService;
import com.pp.web.account.Account;
import com.pp.web.controller.BaseController;
import com.pp.web.controller.until.AccountUtils;
import com.pp.web.controller.until.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 系统常量Controller
 *
 * @author
 */
@Controller
@RequestMapping("/web")
public class SystemConstantController extends BaseController {

    Logger log = LoggerFactory.getLogger(SystemConstantController.class.getName());

    @Autowired
    SystemConstantService systemConstantService;

    @Autowired
    SystemConfigService systemConfigService;

    @Autowired
    QuestionnaireService questionnaireService;

    /**
     * 设置策略
     */
    @RequestMapping(value = "/postStrategy", method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public HashMap<String,Object> postStrategy(HttpServletRequest request) {

        Account account = AccountUtils.getCurrentAccount();
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        returnMap.put("status",300);
        try {
            if(!account.getRole().equals(SystemUser.AUTHOR_ADMIN)) {
                returnMap.put("msg","为管理员操作，当前用户没有管理员权限");
                return returnMap;
            }
            BufferedReader reader = request.getReader();
            String input = "";
            StringBuffer requestBody = new StringBuffer();
            while ((input = reader.readLine())!=null){
                requestBody.append(input);
            }
            String remindTime ;
            if(StringUtils.isEmpty(requestBody.toString())){
                throw new IllegalArgumentException("至少包含一条数据！");
            }else{
                JSONObject jsonObject = new JSONObject(requestBody.toString());
                remindTime = (String) jsonObject.get("remindTime");
            }
            if(StringUtils.isEmpty(remindTime)){
                returnMap.put("msg","超时时间不能为空！");
                return returnMap;
            }
            Integer day = Integer.parseInt(remindTime);
            Long time = day*24*60*60*1000L;
            //先根据类型删除
            SystemConstant systemConstant = new SystemConstant();
            systemConstant.setConstantType(SystemConstant.CONSTANT_TYPE_EXPIRES);
            if (this.systemConstantService.exists(systemConstant)){
                systemConstant = this.systemConstantService.selectOne(systemConstant);
                if(systemConstant!=null){
                    this.systemConstantService.delete(systemConstant.getId(),account.getUserCode());
                    systemConstant.setConstantType(SystemConstant.CONSTANT_TYPE_EXPIRES);
                    systemConstant.setConstant(day.toString());
                    this.systemConstantService.insert(systemConstant,account.getUserCode());
                }else {
                    //不存在写入
                    SystemConstant insertSystemConstant = new SystemConstant();
                    insertSystemConstant.setConstant(day.toString());
                    insertSystemConstant.setConstantType(SystemConstant.CONSTANT_TYPE_EXPIRES);
                    this.systemConstantService.insert(insertSystemConstant,account.getUserCode());
                }
            }else {
                //不存在写入
                SystemConstant insertSystemConstant = new SystemConstant();
                insertSystemConstant.setConstant(day.toString());
                insertSystemConstant.setConstantType(SystemConstant.CONSTANT_TYPE_EXPIRES);
                this.systemConstantService.insert(insertSystemConstant,account.getUserCode());
            }
            //设置systemConfig表
            List<SystemConfig> systemConfigs  = this.systemConfigService.selectAll();
            if(!CollectionUtils.isEmpty(systemConfigs)){
                List<Long> ids = new ArrayList<>();
                for (SystemConfig systemConfig :systemConfigs ) {
                    ids.add(systemConfig.getId());
                }
                this.systemConfigService.delete(ids,account.getUserCode());
            }
            //写入提醒表
            Questionnaire questionnaireQuery = new Questionnaire();
            questionnaireQuery.setQuestionnaireStatusCode(Questionnaire.ALREADY_WITH_STUDENT_CODE);
            List<Questionnaire> questionnaireList = this.questionnaireService.selectList(questionnaireQuery);
            if(!CollectionUtils.isEmpty(questionnaireList)){
                List<SystemConfig> insertConfigList  = this.systemConfigService.selectAll();
                for (Questionnaire questionnaire :questionnaireList ){
                    SystemConfig insertConfig = new SystemConfig();
                    insertConfig.setRemindTime(getDiffDate(questionnaire.getQuestionnaireEndTime(),time));
                    insertConfig.setQuestionnaireCode(questionnaire.getQuestionnaireCode());
                    insertConfig.setQuestionnaireName(questionnaire.getQuestionnaireName());
                    insertConfigList.add(insertConfig);
                }
                this.systemConfigService.insert(insertConfigList,account.getUserCode());
            }
            returnMap.put("status",200);
        }catch (Exception e){
            log.error("设置策略失败："+e.getMessage(),e);
            returnMap.put("status",500);
            returnMap.put("msg",e.getMessage());
        }

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
        try{
            systemConstant = this.systemConstantService.selectOne(systemConstant);
            returnMap.put("remindTime",systemConstant.getConstant());
            returnMap.put("status",200);
        }catch (Exception e){
            returnMap.put("status",400);
        }
        return returnMap;
    }

    private Date getDiffDate(Date remindDate,Long time){
        if(remindDate==null){
            return new Date();
        }
        Long diff = remindDate.getTime() - time;
        if (diff!=null){
            return DateUtils.timeStamp2Date(diff.toString());
        }
        return new Date();
    }
}