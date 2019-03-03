package com.project.csed.smartlearning;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CourseAdapterForStudent extends RecyclerView.Adapter<CourseAdapterForStudent.StudentCourseHolder>
{
    private List<CourseModel> courseForStudentList;
    private Context context;
    private String userName = "aaa";

    public CourseAdapterForStudent(List<CourseModel> courserForStudentList, Context context) {
        this.courseForStudentList = courserForStudentList;
        this.context = context;
    }

    @NonNull
    @Override
    public StudentCourseHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View row =LayoutInflater.from(parent.getContext()).inflate(R.layout.course_row_design_for_student,parent,false);
        StudentCourseHolder studentCourseHolder=new StudentCourseHolder(row);
        return studentCourseHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final StudentCourseHolder studentCourseHolder, int i) {
        final CourseModel exampleCourse= courseForStudentList.get(i);
        studentCourseHolder.courseName.setText(exampleCourse.getCourseName());
        studentCourseHolder.teacherName.setText(exampleCourse.getTeacherName());
        studentCourseHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch Course Activity
                Intent intent = new Intent(context, CourseActivity.class);
                intent.putExtra("course_name", exampleCourse.getCourseName());
                intent.putExtra("user_type", "Student");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseForStudentList.size() ;
    }

    public  class StudentCourseHolder extends RecyclerView.ViewHolder
    {
        TextView teacherName , courseName;
        LinearLayout linearLayout;
        ImageView deletebutton;

        public StudentCourseHolder(@NonNull View itemView) {
            super(itemView);
            teacherName= itemView.findViewById(R.id.teacherName);
            courseName= itemView.findViewById(R.id.courseNameStudent);
            linearLayout = itemView.findViewById(R.id.CourseRowidStudent);
            deletebutton=itemView.findViewById(R.id.deletebunStudent);
            deletebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an AlertDialog.Builder and set the message, and click listeners
                    // for the positive and negative buttons on the dialog.
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(R.string.course_remove_dialog_msg);
                    builder.setPositiveButton(R.string.course_delete_dialog_delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked the "Delete" button, so delete the Course.
                            delete(getAdapterPosition());
                        }
                    });
                    builder.setNegativeButton(R.string.course_delete_dialog_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked the "Cancel" button, so dismiss the dialog
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    });
                    // Create and show the AlertDialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }
    }

    public void delete(int position) {
        //removes the row from UI
        final CourseModel courseModel = courseForStudentList.get(position);
        courseForStudentList.remove(position);
        notifyItemRemoved(position);

        // Remove the row from database
        // Remove from users table
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userkey = currentUser.getUid();
        DatabaseReference usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userkey);
        usersDatabaseReference.child("Courses").child(courseModel.getCourseName()).setValue(null);

        // Get user name
        usersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userName = user.getUserName();
                // Remove from courses table
                DatabaseReference mCourseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Courses").child(courseModel.getCourseName()).child("Students");
                mCourseDatabaseReference.child(userName).setValue(null);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        Toast.makeText(context, R.string.course_removed_successfully_toast, Toast.LENGTH_SHORT).show();
    }
}
