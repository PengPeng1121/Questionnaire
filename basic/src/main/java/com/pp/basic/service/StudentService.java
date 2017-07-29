/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.service;

import com.pp.basic.domain.Student;
import com.pp.basic.domain.vo.InitStudent;
import com.pp.common.core.service.IService;
import com.pp.basic.domain.vo.InitStudentFail;

import java.util.List;

/**
 * 学生信息表Service接口
 * 
 * @author
 */
public interface StudentService extends IService<Student> {

    public List<InitStudentFail> importStudent(List<InitStudent> list ,String user);
}