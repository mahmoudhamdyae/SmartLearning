package com.project.csed.smartlearning;

public class courserForStudentPOJO {
    String teacherName,courseName;

    public courserForStudentPOJO(String teacherName, String courseName) {
        this.teacherName = teacherName;
        this.courseName = courseName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
