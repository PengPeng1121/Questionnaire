package com.pp.web.controller.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaopeng on 2017/3/14.
 */
@Controller
@RequestMapping("/admin")
public class TempController {

    /**
     * 登录
     *
     * @return
     */
    @RequestMapping(value = "/get/user/privilege", method ={RequestMethod.POST,RequestMethod.GET} )
    @ResponseBody
    public Map<String,Object> getUserPrivilege(){
        Map<String,Object> map = new HashMap<>();
        map.put("admin",true);
        map.put("apps",null);
        map.put("company",null);
        map.put("uid",1);
        map.put("username","admin");
        Map<String,Object> returnMap = new HashMap<>();
        returnMap.put("data",map);
        returnMap.put("status",200);
        returnMap.put("message","ok");
        return returnMap;
    }

    /**
     * 登录
     *
     * @return
     */
    @RequestMapping(value = "/login", method ={RequestMethod.POST,RequestMethod.GET} )
    @ResponseBody
    public Map<String,Object> login(){
        Map<String,Object> map = new HashMap<>();
        map.put("admin",true);
        map.put("apps",null);
        map.put("first_login",false);
        map.put("uid",1);
        map.put("pwdwarn","");
        map.put("username","admin");

        Map<String,Object> returnMap = new HashMap<>();
        returnMap.put("data",map);
        returnMap.put("status",200);
        returnMap.put("message","ok");
        return returnMap;
    }
}
