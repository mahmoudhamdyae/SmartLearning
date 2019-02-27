package com.project.csed.smartlearning;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CourseAdapterForStudent extends RecyclerView.Adapter<CourseAdapterForStudent.StudentCourseHolder>
{
private List<courserForStudentPOJO>  courserForStudentList;
private Context context;

    public CourseAdapterForStudent(List<courserForStudentPOJO> courserForStudentList, Context context) {
        this.courserForStudentList = courserForStudentList;
        this.context = context;
    }

    @NonNull
    @Override
    public StudentCourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
      View roww =LayoutInflater.from(parent.getContext()).inflate(R.layout.course_row_design_for_student,parent,false);
      StudentCourseHolder studentCourseHolder=new StudentCourseHolder(roww);
        return studentCourseHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StudentCourseHolder studentCourseHolder, int i) {
        courserForStudentPOJO exampleCourse= courserForStudentList.get(i);
        studentCourseHolder.courseName.setText(exampleCourse.getCourseName());
        studentCourseHolder.teacherName.setText(exampleCourse.getTeacherName());
    }

    @Override
    public int getItemCount() {
         return courserForStudentList.size() ;
    }

    public  class StudentCourseHolder extends RecyclerView.ViewHolder
    {
    TextView teacherName , courseName;


        public StudentCourseHolder(@NonNull View itemView) {
            super(itemView);
            teacherName= itemView.findViewById(R.id.teacherName);
            courseName= itemView.findViewById(R.id.courseNameStudent);

        }
    }
}
