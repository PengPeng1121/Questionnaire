/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.service.impl;

import com.pp.basic.dao.StudentDao;
import com.pp.basic.domain.vo.InitStudent;
import com.pp.basic.domain.vo.InitStudentFail;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pp.basic.domain.Student;
import com.pp.common.core.service.AbstractGenericService;
import com.pp.basic.service.StudentService;

import java.util.*;

/**
 * 学生信息表Service接口实现
 *
 * @author
 */
@Service
public class StudentServiceImpl extends AbstractGenericService<Student> implements StudentService {

    @Autowired
    private StudentDao studentDao;

    @Override
    public List<InitStudentFail> importStudent(List<InitStudent> initStudents, String user) {
        List<InitStudentFail> retFailDataList = new ArrayList<InitStudentFail>();
        if (CollectionUtils.isEmpty(initStudents)) {
            return retFailDataList;
        }
        List<Student> students = new ArrayList<Student>();
        for (InitStudent initStudent:initStudents){
            Student student = new Student();
            student.setIsStudentGraduate(initStudent.getIsStudentGraduate());
            student.setStudentClass(initStudent.getStudentClass());
            student.setStudentCode(initStudent.getStudentCode());
            student.setStudentName(initStudent.getStudentName());
            student.setStudentGrade(initStudent.getStudentGrade());
            students.add(student);
        }
        this.insert(students,user);
        return retFailDataList;
    }
}