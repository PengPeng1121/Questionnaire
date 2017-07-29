/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.service.impl;

import com.pp.basic.domain.vo.InitStudent;
import org.springframework.stereotype.Service;

import com.pp.basic.domain.SystemUser;
import com.pp.common.core.service.AbstractGenericService;
import com.pp.basic.service.SystemUserService;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 学生信息表Service接口实现
 * 
 * @author
 */
@Service
public class SystemUserServiceImpl extends AbstractGenericService<SystemUser> implements SystemUserService {

    @Override
    public void importSystemUser(List<InitStudent> list, String user) {

        if(CollectionUtils.isEmpty(list)){
            return;
        }

        List<SystemUser> systemUsers =new ArrayList<SystemUser>();

        for (InitStudent initStudent:list){
            SystemUser systemUser = new SystemUser();
            systemUser.setUserPassword(initStudent.getStudentCode());
            systemUser.setUserCode(initStudent.getStudentCode());
            systemUser.setUserName(initStudent.getStudentName());
            systemUser.setUserAuthority("2");
            systemUsers.add(systemUser);
        }

        this.insert(systemUsers,user);
    }
}