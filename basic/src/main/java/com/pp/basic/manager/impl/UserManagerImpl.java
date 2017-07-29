/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.manager.impl;

import org.springframework.stereotype.Service;

import com.pp.basic.domain.User;
import com.pp.basic.manager.UserManager;
import com.pp.common.core.manager.AbstractGenericManager;

/**
 * 用户表Manager接口实现
 * 
 * @author
 */
@Service
public class UserManagerImpl extends AbstractGenericManager<User> implements UserManager {

}