package com.pp.web.controller.login;

import com.pp.basic.domain.QuestionnaireStudent;
import com.pp.basic.domain.SystemConfig;
import com.pp.basic.domain.SystemUser;
import com.pp.basic.service.QuestionnaireStudentService;
import com.pp.basic.service.SystemConfigService;
import com.pp.basic.service.SystemUserService;
import com.pp.web.account.Account;
import com.pp.web.account.AccountContext;
import com.pp.web.common.SystemCommon;
import com.pp.web.controller.until.AccountUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaopeng on 2017/3/14.
 */
@Controller
@RequestMapping("/login")
public class LoginController {
    Logger log = LoggerFactory.getLogger(LoginController.class.getName());

    @Autowired
    SystemUserService systemUserService;

    @Autowired
    QuestionnaireStudentService questionnaireStudentService;

    @Autowired
    SystemConfigService systemConfigService;

    /**
     * 登录
     *
     * @return
     */
    @RequestMapping(value = "/login", method ={RequestMethod.POST,RequestMethod.GET} )
    @ResponseBody
    public Map<String,Object> login(HttpServletRequest request, HttpServletResponse response, String userCode, String password)
            throws IllegalAccessException {
        log.info("welcome user:"+userCode);
        Map<String,Object> map = new HashMap<>();
        Account account = AccountContext.getAccount();
        SystemUser systemUser = new SystemUser();
        if(!StringUtils.isEmpty(userCode)){
            systemUser.setUserCode(userCode);
        }else {
            throw new IllegalAccessException("用户名不能为空");
        }
        if(!StringUtils.isEmpty(userCode)){
            systemUser.setUserPassword(password);
        }else {
            throw new IllegalAccessException("密码不能为空");
        }
        //判断用户是否存在 及权限
        if(this.systemUserService.exists(systemUser)){
            //普通用户查询未答问卷个数
            if(SystemUser.AUTHOR_USER.equals(systemUser.getUserAuthority())){
                QuestionnaireStudent questionnaireStudent = new QuestionnaireStudent();
                questionnaireStudent.setStudentCode(systemUser.getUserCode());
                questionnaireStudent.setQuestionnaireProcessStatusCode(QuestionnaireStudent.PROCESS_CODE_UNDO);
                List<QuestionnaireStudent> list =this.questionnaireStudentService.selectList(questionnaireStudent);
                //声明
                List<SystemConfig> configQuestionnaires = new ArrayList<>();
                if(list!=null&&list.size()>0){
                    account.setUnDoQuestionnaireNum(list.size());
                    for (QuestionnaireStudent student:list) {
                        SystemConfig configQuestionnaire = new SystemConfig();
                        configQuestionnaire.setQuestionnaireCode(student.getQuestionnaireCode());
                        configQuestionnaires.add(configQuestionnaire);
                    }
                }else {
                    account.setUnDoQuestionnaireNum(0);
                }
                //提醒问卷个数
                if(configQuestionnaires.size()>0){
                    Integer count = 0;
                    for (SystemConfig config:configQuestionnaires) {
                        Long num = this.systemConfigService.count(config);
                        count = count + num.intValue();
                    }
                    account.setRemindDoQuestionnaireNum(count);
                }else {
                    account.setRemindDoQuestionnaireNum(0);
                }
            }
            //保存在页面
            map.put("account",account);
            AccountContext.setAccount(account);
        }else {
            throw new IllegalAccessException("用户不存在或者密码错误！");
        }
        return map;
    }

    @RequestMapping(value = "/logout", method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public Boolean logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);//防止创建Session
        if(session == null){
            return true;
        }
        session.removeAttribute(SystemCommon.COOKIE_NAME);
        return true;
    }

    @RequestMapping(value = "/isLogin",method ={RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public HashMap<String,Object> isLogin() throws IOException {
        Account account = AccountUtils.getCurrentAccount();
        HashMap<String,Object> map = new HashMap<String,Object> ();
        if(account!=null){
            if(account.getRole().equals("1")){
                map.put("admin",true);
            }else {
                map.put("admin",false);
            }
            map.put("status",200);
            map.put("userName",account.getUserName());
            map.put("userCode",account.getUserCode());
        }else {
            map.put("status",302);
        }
        return map;
    }
}
