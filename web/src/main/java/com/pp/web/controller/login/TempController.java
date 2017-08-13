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
@RequestMapping("/web")
public class TempController {

    /**
     * 获取系统时间
     *
     * @return
     */
    @RequestMapping(value = "/getTime", method ={RequestMethod.POST,RequestMethod.GET} )
    @ResponseBody
    public Map<String,Object> getUserPrivilege(){
        Map<String,Object> map = new HashMap<>();
        Long timeStamp = System.currentTimeMillis();;
        map.put("timeStamp",timeStamp);
        return map;
    }
}
