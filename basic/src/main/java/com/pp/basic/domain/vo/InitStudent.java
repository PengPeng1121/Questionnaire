package com.pp.basic.domain.vo;

/**
 * Created by asus on 2017/5/22.
 */
public class InitStudent {
    // 学号
    private String studentCode;

    // 姓名
    private String studentName;

    // 学生年级
    private String studentGrade;

    // 学生班级
    private String studentClass;

    // 学生是否毕业（0:否;1:是）
    private Integer isStudentGraduate;

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentGrade() {
        return studentGrade;
    }

    public void setStudentGrade(String studentGrade) {
        this.studentGrade = studentGrade;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public Integer getIsStudentGraduate() {
        return isStudentGraduate;
    }

    public void setIsStudentGraduate(Integer isStudentGraduate) {
        this.isStudentGraduate = isStudentGraduate;
    }
}
