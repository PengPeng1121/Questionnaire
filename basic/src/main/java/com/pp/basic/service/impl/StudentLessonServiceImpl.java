/**
 * Copyright (c) 2016, 2017, JD and/or its affiliates. All rights reserved.
 */
package com.pp.basic.service.impl;

import com.pp.basic.domain.Lesson;
import com.pp.basic.domain.Student;
import com.pp.basic.manager.StudentLessonManager;
import com.pp.basic.service.LessonService;
import com.pp.basic.service.StudentService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pp.basic.domain.StudentLesson;
import com.pp.common.core.service.AbstractGenericService;
import com.pp.basic.service.StudentLessonService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 学生选课信息表Service接口实现
 *
 * @author
 */
@Service
public class StudentLessonServiceImpl extends AbstractGenericService<StudentLesson> implements StudentLessonService {

    @Autowired
    LessonService lessonService;

    @Autowired
    StudentService studentService;

    @Autowired
    StudentLessonManager studentLessonManager;

    @Override
    public void autoInitRelation(StudentLesson studentlesson) {
        List<StudentLesson> insertStudentLessons = new ArrayList<>();
        //查询出所有必修课选课关系
        studentlesson.setIsMustCheck(1);
        List<StudentLesson> delStudentLessons = studentLessonManager.selectList(studentlesson);
        //查询课程
        Lesson lessonQuery = new Lesson();
        lessonQuery.setTerm(studentlesson.getTerm());
        lessonQuery.setIsMustCheck(1);//必修
        if(!StringUtils.isEmpty(studentlesson.getLessonCode())){
            lessonQuery.setLessonCode(studentlesson.getLessonCode());
        }
        List<Lesson> lessons = lessonService.selectList(lessonQuery);
        if(CollectionUtils.isEmpty(lessons)){
            throw new IllegalArgumentException(studentlesson.getTerm()+"里没有找到课程");
        }
        //查询学生
        for (Lesson lesson:lessons) {
            List<String> lessonClasses = getClassUtil(lesson.getLessonClass());
            for (String lessonClass:lessonClasses) {
                Student studentQuery = new Student();
                studentQuery.setStudentClass(lessonClass);
                studentQuery.setIsStudentGraduate(0);
                List<Student> students = studentService.selectList(studentQuery);
                for(Student student :students){
                    StudentLesson insertStudentLesson = new StudentLesson();
                    insertStudentLesson.setTerm(studentlesson.getTerm());
                    insertStudentLesson.setStudentCode(student.getStudentCode());
                    insertStudentLesson.setStudentName(student.getStudentName());
                    insertStudentLesson.setLessonCode(lesson.getLessonCode());
                    insertStudentLesson.setLessonName(lesson.getLessonName());
                    insertStudentLesson.setIsMustCheck(1);
                    insertStudentLesson.setCreateTime(new Date());
                    insertStudentLesson.setUpdateTime(new Date());
                    insertStudentLesson.setCreateUser(studentlesson.getUpdateUser());
                    insertStudentLesson.setUpdateUser(studentlesson.getUpdateUser());
                    insertStudentLessons.add(insertStudentLesson);
                }
            }
        }
        autoInitRelationTransactional(delStudentLessons,insertStudentLessons);
    }

    @Transactional
    public void autoInitRelationTransactional(List<StudentLesson> delStudentLessons,List<StudentLesson> insertStudentLessons){
        if(!CollectionUtils.isEmpty(delStudentLessons)){
            List<Long> delIds = new ArrayList<>();
            for (StudentLesson del:delStudentLessons){
                delIds.add(del.getId());
            }
            //物理删除
            studentLessonManager.deletePhysically(delIds);
        }
        //写入新关系
        List<StudentLesson> subStudent = new ArrayList<>();
        for (StudentLesson studentLesson : insertStudentLessons) {
            subStudent.add(studentLesson);
            if (subStudent.size() == 200) {
                this.studentLessonManager.insert(subStudent);
                subStudent = new ArrayList<>();
            }
        }
        if (subStudent.size() > 0) {
            studentLessonManager.insert(subStudent);
        }
    }

    //从lesson 的lessonClass中取出课程名
    private List<String> getClassUtil(String classNames){
        //  String lessonClasses = "通信1501-03;电子1504-16;计算机1501";

        List<String> className = new ArrayList<String>();
        String major[] = classNames.split(";");
        for (int i =0;i<major.length;i++){
            if(major[i].contains("-")){
                String classPre = major[i].substring(0,major[i].indexOf("-")-2);

                String startClass = major[i].substring(major[i].indexOf("-")-2,major[i].indexOf("-"));

                String endClass = major[i].substring(major[i].indexOf("-")+1,major[i].length());

                Integer startNum = Integer.parseInt(startClass);
                Integer endNum = Integer.parseInt(endClass);

                while (startNum<=endNum){
                    if(startNum>9){
                        className.add(classPre+startNum);
                    }else {
                        className.add(classPre+"0"+startNum);
                    }
                    startNum++;
                }
            }else {
                className.add(major[i]);
            }
        }
        return className;
    }
}