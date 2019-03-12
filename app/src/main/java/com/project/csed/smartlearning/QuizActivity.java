package com.project.csed.smartlearning;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    Button addButton;
    RecyclerView recyclerView;
    // todo set empty view
    View emptyView;
    TextView subtitle;

    String courseName, userType;

    List<Quiz> quizList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        subtitle = findViewById(R.id.empty_subtitle_text);
        addButton = findViewById(R.id.addbtn);
        recyclerView = findViewById(R.id.recyclerView);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        emptyView = findViewById(R.id.empty_view);

        // Examine the intent that was used to launch this activity,
        // in order to get course name.
        Intent intent = getIntent();
        // todo There is a problem here, it does not get course name
        courseName = intent.getStringExtra("courseName");

        // Change the app bar to show course name
        setTitle(courseName);

        userType = intent.getStringExtra("userType");

        // Student is not allowed to add quizzes
        if (userType.equals("Student")) {
            addButton.setVisibility(View.GONE);
            subtitle.setVisibility(View.GONE);
            studentActivity();
        }
        else
            teacherActivity();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizActivity.this, AddQuizActivity.class);
                intent.putExtra("course_name", courseName);
                startActivity(intent);
            }
        });
    }

    private void teacherActivity(){
        final QuizAdapterForTeacher quizAdapterForTeacher = new QuizAdapterForTeacher(quizList, this);

        // todo Read Quizzes from database

        Quiz quiz = new Quiz(1, "date");
        quizList.add(quiz);

        recyclerView.setLayoutManager(new LinearLayoutManager(QuizActivity.this));
        recyclerView.setAdapter(quizAdapterForTeacher);
    }

    private void studentActivity(){
        final QuizAdapterForStudent quizAdapterForStudent = new QuizAdapterForStudent(quizList, this);

        // todo Read Quizzes from database

        Quiz quiz = new Quiz(1, "date");
        quizList.add(quiz);

        recyclerView.setLayoutManager(new LinearLayoutManager(QuizActivity.this));
        recyclerView.setAdapter(quizAdapterForStudent);
    }
}
