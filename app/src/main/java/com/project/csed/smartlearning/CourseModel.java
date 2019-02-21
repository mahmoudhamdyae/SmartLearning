package com.project.csed.smartlearning;

public class CourseModel {
    private String CourseName, StudentNo, YearDate, teacherName;

    public CourseModel(String courseName, String studentNo, String yearDate, String teacherName) {
        this.CourseName= courseName;
        this.StudentNo = studentNo;
        this.YearDate = yearDate;
        this.teacherName = teacherName;
    }

    public CourseModel() {
    }

    public String getCourseName() {
        return CourseName;
    }

    public String getStudentNo() {
        return StudentNo;
    }

    public String getYearDate() {
        return YearDate;
    }

    public void setCourseName(String courseName) {
        CourseName = courseName;
    }

    public void setStudentNo(String studentNo) {
        StudentNo = studentNo;
    }

    public void setYearDate(String yearDate) {
        YearDate = yearDate;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
