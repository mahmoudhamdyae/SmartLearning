package com.project.csed.smartlearning;

public class CourseModel {
    private String CourseName,StudentNo,YearDate;

    public CourseModel(String courseName,String studentNo, String yearDate) {
        this.CourseName= courseName;
        this.StudentNo = studentNo;
        this.YearDate = yearDate;

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
}
