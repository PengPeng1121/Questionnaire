/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.service;

import com.pp.basic.domain.SystemUser;
import com.pp.basic.domain.vo.InitStudent;
import com.pp.common.core.service.IService;

import java.util.List;

/**
 * 学生信息表Service接口
 * 
 * @author
 */
public interface SystemUserService extends IService<SystemUser> {

    public void importSystemUser(List<InitStudent> list , String user);
}