package com.project.csed.smartlearning;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class AddStudentAdapter extends RecyclerView.Adapter<AddStudentAdapter.AddStudentHolder> {
    private List<User> StudentList;
    private Context context;
    ArrayList<User> CheckStudentList =new ArrayList<>();
    public AddStudentAdapter(List<User> StudentList, Context context) {
        this.StudentList = StudentList;
        this.context = context;

    }

    @NonNull
    @Override
    public AddStudentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View studentRow = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.addstudent_row_design, viewGroup, false);
        AddStudentHolder studentHolder = new AddStudentHolder(studentRow);
        return studentHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final AddStudentHolder studentHolder, int i) {
        final User user = StudentList.get(i);
        studentHolder.StudentName.setText(user.getUserName());
        studentHolder.emailAddress.setText(user.getEmail());

        studentHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(studentHolder.SelectStudent.isChecked())
                {
                    CheckStudentList.remove(user);
                    studentHolder.SelectStudent.setChecked(false);
                }
                else
                {
                    CheckStudentList.add(user);
                    studentHolder.SelectStudent.setChecked(true);
                }
            }
        });
        studentHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                final CheckBox chk = (CheckBox) v;
                if(chk.isChecked())
                {
                    CheckStudentList.add(StudentList.get(pos));
                }
                else
                {
                    CheckStudentList.remove(StudentList.get(pos));
                }
            }
        });
    }

    @Override
    public int getItemCount() { return StudentList.size();}
    public class AddStudentHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView StudentName,emailAddress;
        CheckBox SelectStudent;
        LinearLayout linearLayout;
        ItemClickListener itemClickListener;

        public AddStudentHolder(@NonNull View itemView) {
            super(itemView);
            StudentName = itemView.findViewById(R.id.StudentName);
            emailAddress = itemView.findViewById(R.id.emailAddress);
            SelectStudent = itemView.findViewById(R.id.SelectStudent);
            linearLayout = itemView.findViewById(R.id.AddStudentrow);
            SelectStudent.setOnClickListener(this);
        }
        public void setItemClickListener(ItemClickListener ic)
        {
            this.itemClickListener= ic;
        }
        @Override
        public void onClick(View v)
        {
        this.itemClickListener.onItemClick(v,getLayoutPosition());
        }
    }
}

