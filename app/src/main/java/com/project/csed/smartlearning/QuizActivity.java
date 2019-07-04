package com.project.csed.smartlearning;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    FloatingActionButton addButton;
    RecyclerView recyclerView;
    View emptyView;
    TextView subtitle;

    String courseName, userType;

    List<Quiz> quizList = new ArrayList<>();
    QuizAdapterForTeacher quizAdapterForTeacher;
    QuizAdapterForStudent quizAdapterForStudent;

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
        courseName = intent.getStringExtra("course_name");

        // Change the app bar to show course name
        setTitle(courseName);

        userType = intent.getStringExtra("userType");

        // Student is not allowed to add quizzes
        if (userType.equals("Student")) {
            addButton.hide();
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
                intent.putExtra("addQuestion", "addQuiz");
                startActivity(intent);
            }
        });
    }

    private void teacherActivity(){
        readQuizzes();

        quizAdapterForTeacher = new QuizAdapterForTeacher(quizList, this, courseName);
        recyclerView.setLayoutManager(new LinearLayoutManager(QuizActivity.this));
        recyclerView.setAdapter(quizAdapterForTeacher);
    }

    private void studentActivity(){
        readQuizzes();

        quizAdapterForStudent = new QuizAdapterForStudent(quizList, this, courseName);
        recyclerView.setLayoutManager(new LinearLayoutManager(QuizActivity.this));
        recyclerView.setAdapter(quizAdapterForStudent);
    }

    // Read Quizzes from database
    private void readQuizzes (){
        DatabaseReference quizReference = FirebaseDatabase.getInstance().getReference().child("Quizzes").child(courseName);
        quizReference.orderByChild("number").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quizList.clear();
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    Quiz quiz = dataSnapshot1.getValue(Quiz.class);
                    quizList.add(quiz);

                    // Get number of quizzes
                    int numberOfQuizzesForTeacher = 0, numberOfQuizzesForStudent = 0;

                    // Notify changes to the adapters and set number of quizzes
                    if (userType.equals("Teacher")) {
                        quizAdapterForTeacher.notifyDataSetChanged();
                        numberOfQuizzesForTeacher = quizAdapterForTeacher.getItemCount();
                    }
                    else {
                        quizAdapterForStudent.notifyDataSetChanged();
                        numberOfQuizzesForStudent = quizAdapterForStudent.getItemCount();
                    }

                    // If there is no quiz set empty view
                    if (numberOfQuizzesForTeacher == 0 && numberOfQuizzesForStudent == 0){
                        recyclerView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                    }
                    else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyView.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }});
    }
}
