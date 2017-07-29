/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.dao.impl;

import org.springframework.stereotype.Repository;

import com.pp.basic.dao.StudentDao;
import com.pp.basic.domain.Student;
import com.pp.common.core.dao.AbstractDemoDao;

/**
 * 学生信息表DAO接口实现
 * 
 * @author
 */
@Repository
public class StudentDaoImpl extends AbstractDemoDao<Student> implements StudentDao {

}