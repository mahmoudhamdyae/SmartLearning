package com.project.csed.smartlearning;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class QuizStatisticsActivity extends AppCompatActivity {
    int correctAnswers = 0, numberOfQuestions = 0;
    String courseName, quizDate, quizNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_statistics);

        Intent intent = getIntent();
        correctAnswers = Integer.parseInt(intent.getStringExtra("correctAnswers"));
        numberOfQuestions = Integer.parseInt(intent.getStringExtra("numberOfQuestions"));
        courseName = intent.getStringExtra("courseName");
        quizDate = intent.getStringExtra("quizDate");
        quizNumber = intent.getStringExtra("quizNumber");

        // Get user id
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Save student degree in database
        DatabaseReference quizRef = FirebaseDatabase.getInstance().getReference().child("Quizzes").child(courseName).child(quizDate);
        quizRef.child("Students").child(userId).setValue(correctAnswers);

        // View Degree
        TextView degree = findViewById(R.id.degree);
        String degreeString = "Course : " + courseName;
        degreeString += "\nQuiz NO : " + quizNumber;
        degreeString += "\nDegree : " + correctAnswers;
        degreeString += " of " + numberOfQuestions;
        degree.setText(degreeString);

        Button ok = findViewById(R.id.ok_button);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
