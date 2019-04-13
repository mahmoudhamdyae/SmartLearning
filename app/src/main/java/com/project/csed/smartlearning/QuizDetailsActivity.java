package com.project.csed.smartlearning;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuizDetailsActivity extends AppCompatActivity {
    Button modifyQuiz;
    String quizDate, courseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_details);

        // Examine the intent that was used to launch this activity,
        // in order to get quiz date.
        Intent intent = getIntent();
        quizDate = intent.getStringExtra("quizDate");
        courseName = intent.getStringExtra("courseName");

        modifyQuiz = findViewById(R.id.quiz_modify_button);
        modifyQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open quiz modify activity
                Intent intent = new Intent(QuizDetailsActivity.this, QuizModifyActivity.class);
                intent.putExtra("quizDate", quizDate);
                intent.putExtra("courseName", courseName);
                startActivity(intent);
            }
        });

        DatabaseReference quizReference = FirebaseDatabase.getInstance().getReference().child("Courses").child(courseName).child("Quizzes").child(quizDate);
        quizReference.child("number").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String title = "Quiz number " + String.valueOf(dataSnapshot.getValue());
                // Change the app bar to show quiz number
                setTitle(title);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // todo add Students to the recycler view
    }
}
