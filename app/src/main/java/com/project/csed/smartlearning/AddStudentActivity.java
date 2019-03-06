package com.project.csed.smartlearning;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AddStudentActivity extends AppCompatActivity {

    EditText StudentNameSearch;
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
        //add student users to the list
        mStudentDB = FirebaseDatabase.getInstance().getReference().child("Users");
        mStudentDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StudentList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    final User student = postSnapshot.getValue(User.class);

                    if(student.getType().equals("Student"))
                    {
                        StudentList.add(student);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }});
        recycler.setLayoutManager(new LinearLayoutManager(AddStudentActivity.this));
        recycler.setAdapter(adapter);
    }

    public void AddButton(View view) {
        
        for(final User user : adapter.CheckStudentList)
        {
            // add checked student users to course
            mCourseDB.child("Students").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    mCourseDB.child("Students").child(user.getUserName()).setValue(user);

                    Toast.makeText(AddStudentActivity.this, R.string.AddStudent_activity_Student_Added_Toast
                            , Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }});

            // get the course details.
            mCourseDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   final CourseModel courseModel1 = dataSnapshot.getValue(CourseModel.class);
                    // add courses under student users
                    mStudentDB.orderByChild("userName").equalTo(user.getUserName()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot data: dataSnapshot.getChildren())
                            {
                                String userkey = data.getKey();
                                mStudentDB.child(userkey).child("Courses").child(courseName).setValue(courseModel1);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }});
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
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