package com.project.csed.smartlearning;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        CourseModel courseModel=courseAdapterList.get(i);
        courseHolder.courseName.setText(courseModel.getCourseName());
        courseHolder.studentNo.setText(courseModel.getStudentNo());
        courseHolder.year.setText(courseModel.getYearDate());
        courseHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo open course activity
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
                    delete(getAdapterPosition());
                }
            });
        }
    }
}