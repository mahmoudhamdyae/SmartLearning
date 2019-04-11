package com.project.csed.smartlearning;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CourseActivity extends AppCompatActivity {
    CardView quizzes, addStudent, materials, chat;
    String courseName, userType;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    User user ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        //To Only Get The UserName
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (user != null) {
                    Toast.makeText(CourseActivity.this, user.getUserName(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });




        // Examine the intent that was used to launch this activity,
        // in order to get course name.
        Intent intent = getIntent();
        courseName = intent.getStringExtra("course_name");

        // Change the app bar to show course name
        setTitle(courseName);

        quizzes = findViewById(R.id.quizzes);
        addStudent = findViewById(R.id.add_student);
        materials = findViewById(R.id.materials);
        chat = findViewById(R.id.chat);

        userType = intent.getStringExtra("user_type");
        if (userType.equals("Student"))
            addStudent.setVisibility(View.GONE);

        quizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Quiz Activity
                Intent intent = new Intent(CourseActivity.this, QuizActivity.class);
                intent.putExtra("course_name", courseName);
                intent.putExtra("userType", userType);
                startActivity(intent);
            }
        });

        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open add student activity
                Intent activ = new Intent(CourseActivity.this,AddStudentActivity.class);
                activ.putExtra("name", courseName);
                startActivity(activ);
            }
        });

        materials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo open materials activity
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo open chat activity

                Intent intent1=new Intent(CourseActivity.this,ChatActivity.class);
                intent1.putExtra("name",user.getUserName());
                intent1.putExtra("coursename",courseName);
                intent1.putExtra("userType",userType);
                startActivity(intent1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.students:
                // todo open students activity
                return true;
            case R.id.course_details:
                // todo open course details activity
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
