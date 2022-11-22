package com.project.csed.smartlearning.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.csed.smartlearning.ui.course.CourseActivity;
import com.project.csed.smartlearning.R;
import com.project.csed.smartlearning.models.CourseModel;
import com.project.csed.smartlearning.models.User;

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
    public void onBindViewHolder(@NonNull final CourseHolder courseHolder, int i) {
        final CourseModel courseModel = courseAdapterList.get(i);
        courseHolder.courseName.setText(courseModel.getCourseName());
        courseHolder.year.setText("Course year: "+courseModel.getYearDate());

        //get students no.
        final FirebaseDatabase databasecount=FirebaseDatabase.getInstance();
        DatabaseReference mystudentRef=databasecount.getReference().child("Courses").child(courseModel.getCourseName()).child("Students");
        mystudentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Snapshot) {
                String studentNo = "Registered students: " + Snapshot.getChildrenCount();
                courseHolder.studentNo.setText(studentNo);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        courseHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch pressed view (course) activity
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
        // Remove the row from UI
        final CourseModel courseModel = courseAdapterList.get(position);
        courseAdapterList.remove(position);
        notifyItemRemoved(position);

        // Remove the course from database

        // this Two Lines Delete the chat from fireBase
        DatabaseReference mChatDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Rooms").child(courseModel.getCourseName());
        mChatDatabaseReference.removeValue();
        // Remove quizzes
        DatabaseReference mQuizDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Quizzes").child(courseModel.getCourseName());
        mQuizDatabaseReference.removeValue();
        // Remove Materials from database
        DatabaseReference mMaterialDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Materials").child(courseModel.getCourseName());
        mMaterialDatabaseReference.removeValue();
        // Remove from users table (students)
        DatabaseReference mCourseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Courses").child(courseModel.getCourseName());
        mCourseDatabaseReference.child("Students").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                    User stuser = snapshot1.getValue(User.class);
                    DatabaseReference mStudentDB = FirebaseDatabase.getInstance().getReference().child("Users").child(stuser.getUserId()).child("Courses").child(courseModel.getCourseName());
                    mStudentDB.setValue(null);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }});

        // Remove from users table (teacher)
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userkey = currentUser.getUid();
        final DatabaseReference usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userkey).child("Courses").child(courseModel.getCourseName());
        usersDatabaseReference.setValue(null);

        // Remove from courses table
        mCourseDatabaseReference.removeValue();


        Toast.makeText(context, R.string.course_deleted_successfully_toast, Toast.LENGTH_SHORT).show();
    }

    public class  CourseHolder extends RecyclerView.ViewHolder{
        TextView courseName,studentNo,year;
        ImageView deletebutton;
        ConstraintLayout constraintLayout;

        public CourseHolder(@NonNull View itemView) {
            super(itemView);

            courseName = itemView.findViewById(R.id.courseName);
            studentNo = itemView.findViewById(R.id.studentNo);
            year = itemView.findViewById(R.id.year);
            constraintLayout = itemView.findViewById(R.id.CourseRowid);
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