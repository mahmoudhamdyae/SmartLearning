package com.project.csed.smartlearning;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Toast;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddStudentActivity extends AppCompatActivity {
    CheckBox StudentCheck;
    EditText StudentNameSearch;
    //TextView StudentName;
    RecyclerView recycler;
    AddStudentAdapter adapter;
    List<User> StudentList;
    private DatabaseReference mStudentDB,mCourseDB;
    String courseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        Intent activ = getIntent();
        courseName = activ.getStringExtra("name");
        setTitle(courseName);
        StudentList = new ArrayList<>();

        StudentNameSearch = findViewById(R.id.searchstudent);
        recycler = findViewById(R.id.AddrecyclerView);
        adapter = new AddStudentAdapter(StudentList,this);

        mCourseDB = FirebaseDatabase.getInstance().getReference().child("Courses").child(courseName);
        mStudentDB = FirebaseDatabase.getInstance().getReference().child("Users");
        mStudentDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    final User student = postSnapshot.getValue(User.class);

                    if(student.getType().equals("Student"))
                    {
                        StudentList.add(student);
                    }
                }
                recycler.setLayoutManager(new LinearLayoutManager(AddStudentActivity.this));
                recycler.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }


    public void AddButton(View view) {
        //todo add checked students to courses (child:Students)
    }

    public void SearchButton(View view) {

        String Sname = StudentNameSearch.getText().toString();
        Query query1 = mStudentDB.orderByChild("userName")
                .startAt(Sname)
                .endAt(Sname+"\uf8ff");
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    StudentList.clear();
                    for(DataSnapshot dss: dataSnapshot.getChildren())
                    {
                        final User student = dss.getValue(User.class);
                        if(student.getType().equals("Student"))
                        {
                            StudentList.add(student);
                        }
                        recycler.setLayoutManager(new LinearLayoutManager(AddStudentActivity.this));
                        recycler.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }});
        Query query2 = mStudentDB.orderByChild("email")
                .startAt(Sname)
                .endAt(Sname+"\uf8ff");
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    StudentList.clear();
                    for(DataSnapshot dss: dataSnapshot.getChildren())
                    {
                        final User student = dss.getValue(User.class);
                        if(student.getType().equals("Student"))
                        {
                            StudentList.add(student);
                        }
                        recycler.setLayoutManager(new LinearLayoutManager(AddStudentActivity.this));
                        recycler.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }});

    }
}
