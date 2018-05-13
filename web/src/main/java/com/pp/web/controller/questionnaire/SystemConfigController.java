/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.web.controller.questionnaire;

import com.pp.basic.domain.QuestionnaireStudent;
import com.pp.basic.domain.SystemConfig;
import com.pp.basic.domain.SystemConstant;
import com.pp.basic.service.QuestionnaireStudentService;
import com.pp.basic.service.SystemConfigService;
import com.pp.basic.service.SystemConstantService;
import com.pp.web.account.Account;
import com.pp.web.controller.BaseController;
import com.pp.web.controller.until.AccountUtils;
import com.pp.web.controller.until.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger log = LoggerFactory.getLogger(SystemConfigController.class.getName());

    @Autowired
    SystemConfigService systemConfigService;

    @Autowired
    QuestionnaireStudentService questionnaireStudentService;

    @Autowired
    SystemConstantService systemConstantService;

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
            SystemConstant systemConstantQuery = new SystemConstant();
            try{
                systemConstantQuery.setConstantType(SystemConstant.CONSTANT_TYPE_EXPIRES);
                systemConstantQuery = systemConstantService.selectOne(systemConstantQuery);
            }catch (Exception e){
                log.error("常量数据表有问题，请及时处理");
                List<SystemConstant> systemConstantList = systemConstantService.selectList(systemConstantQuery);
                if(!CollectionUtils.isEmpty(systemConstantList)){
                    systemConstantQuery = systemConstantList.get(0);
                }
            }
            Long time = 0L;
            if(systemConstantQuery!=null){
                time = Integer.parseInt(systemConstantQuery.getConstant())*24*60*60*1000L;
            }

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
                        remindConfig.setEndTime(getAddDate(remindConfig.getRemindTime(),time));
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
            returnMap.put("msg","查询数据失败"+e.getMessage());
            returnMap.put("status",300);
        }
        return returnMap;
    }

    private Date getAddDate(Date remindDate,Long time){
        if(remindDate==null){
            return new Date();
        }
        Long diff = remindDate.getTime() + time;
        if (diff!=null){
            return DateUtils.timeStamp2Date(diff.toString());
        }
        return new Date();
    }
}