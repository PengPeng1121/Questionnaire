/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.QuestionnaireStudent;
import com.pp.basic.domain.SystemConfig;
import com.pp.basic.service.QuestionnaireStudentService;
import com.pp.basic.service.SystemConfigService;
import com.pp.web.account.Account;
import com.pp.web.controller.BaseController;
import com.pp.web.controller.until.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
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
     * 查询提醒问卷
     */
    @RequestMapping(value = "/findRemindQuestionnaire", method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public HashMap<String,Object> findRemindQuestionnaire() {
        //查询未做的问卷个数
        Account account = AccountUtils.getCurrentAccount();
        QuestionnaireStudent questionnaireStudentQuery = new QuestionnaireStudent();
        HashMap<String,Object> map = new HashMap<String,Object>();
        HashMap<String,Object> returnMap = new HashMap<String,Object>();
        try {
            questionnaireStudentQuery.setStudentCode(account.getUserCode());
            questionnaireStudentQuery.setQuestionnaireProcessStatusCode(QuestionnaireStudent.PROCESS_CODE_UNDO);
            List<QuestionnaireStudent> list = this.questionnaireStudentService.selectList(questionnaireStudentQuery);
            //构建 查询问卷条件
            List<SystemConfig> configQuestionnaires = new ArrayList<>();
            if(list!=null&&list.size()>0){
                for (QuestionnaireStudent student:list) {
                    SystemConfig configQuestionnaire = new SystemConfig();
                    configQuestionnaire.setQuestionnaireCode(student.getQuestionnaireCode());
                    //与当前时间对比
                    configQuestionnaire.setRemindTimeEnd(new Date());
                    configQuestionnaires.add(configQuestionnaire);
                }
            }
            //根据条件查询，结果为当前时间的提醒问卷
            List<SystemConfig> remindList = new ArrayList<>();
            if(configQuestionnaires!=null&&configQuestionnaires.size()>0){
                for (SystemConfig systemConfig:configQuestionnaires) {
                    SystemConfig remindConfig  = this.systemConfigService.selectOne(systemConfig);
                    if (remindConfig!=null){
                        remindList.add(remindConfig);
                    }
                }
            }

            // 返回查询结果
            if (!CollectionUtils.isEmpty(remindList)){
                map.put("data",remindList);
                map.put("count",remindList.size());
                returnMap.put("data",map);
                returnMap.put("status",200);
            }else {
                returnMap.put("data",map);
                returnMap.put("msg","没有查询到数据");
                returnMap.put("status",300);
            }
        }catch (Exception e){
            returnMap.put("msg","没有查询到数据");
            returnMap.put("status",300);
        }
        return returnMap;
    }

}