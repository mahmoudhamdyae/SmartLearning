package com.project.csed.smartlearning.models;

public class CourseModel {
    private String CourseName, YearDate, teacherName;

    public CourseModel(String courseName, String yearDate, String teacherName) {
        this.CourseName= courseName;
        this.YearDate = yearDate;
        this.teacherName = teacherName;
    }

    public CourseModel() {
    }

    public String getCourseName() {
        return CourseName;
    }

    public String getYearDate() {
        return YearDate;
    }

    public void setCourseName(String courseName) {
        CourseName = courseName;
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