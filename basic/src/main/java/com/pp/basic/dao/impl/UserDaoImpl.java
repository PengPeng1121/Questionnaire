/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.dao.impl;

import org.springframework.stereotype.Repository;

import com.pp.basic.dao.UserDao;
import com.pp.basic.domain.User;
import com.pp.common.core.dao.AbstractDemoDao;

/**
 * 用户表DAO接口实现
 * 
 * @author
 */
@Repository
public class UserDaoImpl extends AbstractDemoDao<User> implements UserDao {

}