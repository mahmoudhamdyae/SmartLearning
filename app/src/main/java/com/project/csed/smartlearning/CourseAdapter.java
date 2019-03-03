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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseHolder> {
    private List<CourseModel> courseAdapterList;
    private Context context;

    public CourseAdapter(List<CourseModel> courseAdapterList, Context context) {
        this.courseAdapterList = courseAdapterList;
        this.context = context;
    }

    @NonNull
    @Override
    public CourseHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View courseRow = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.course_row_design,viewGroup,false);
        CourseHolder holder = new CourseHolder(courseRow);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CourseHolder courseHolder, int i) {
        final CourseModel courseModel = courseAdapterList.get(i);
        courseHolder.courseName.setText(courseModel.getCourseName());
        courseHolder.studentNo.setText(courseModel.getStudentNo());
        courseHolder.year.setText(courseModel.getYearDate());
        courseHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch Course Activity
                Intent intent = new Intent(context, CourseActivity.class);
                intent.putExtra("course_name", courseModel.getCourseName());
                intent.putExtra("user_type", "Teacher");
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return courseAdapterList.size();
    }


    public void delete(int position) {
        //removes the row from UI
        CourseModel courseModel = courseAdapterList.get(position);
        courseAdapterList.remove(position);
        notifyItemRemoved(position);
        // Remove the row from database
        DatabaseReference mCourseDatabaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Courses").child(courseModel.getCourseName());
        mCourseDatabaseReference.setValue(null);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userkey = currentUser.getUid();
        DatabaseReference usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userkey).child("Courses").child(courseModel.getCourseName());
        usersDatabaseReference.setValue(null);
        Toast.makeText(context, R.string.course_deleted_successfully_toast, Toast.LENGTH_SHORT).show();
    }

    public class  CourseHolder extends RecyclerView.ViewHolder{
        TextView courseName,studentNo,year;
        ImageView deletebutton;
        LinearLayout linearLayout;

        public CourseHolder(@NonNull View itemView) {
            super(itemView);

            courseName = itemView.findViewById(R.id.courseName);
            studentNo = itemView.findViewById(R.id.studentNo);
            year = itemView.findViewById(R.id.year);
            linearLayout = itemView.findViewById(R.id.CourseRowid);
            deletebutton=itemView.findViewById(R.id.deletebun);
            deletebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an AlertDialog.Builder and set the message, and click listeners
                    // for the positive and negative buttons on the dialog.
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(R.string.course_delete_dialog_msg);
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
}