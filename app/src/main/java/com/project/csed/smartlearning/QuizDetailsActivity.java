package com.project.csed.smartlearning;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuizDetailsActivity extends AppCompatActivity {
    Button modifyQuiz;
    String quizDate, courseName;

    List<User> usersList = new ArrayList<>();

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

        QuizStudentsAdapter quizStudentsAdapter = new QuizStudentsAdapter(QuizDetailsActivity.this, usersList);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        // Get Users id
        DatabaseReference getIdRef = FirebaseDatabase.getInstance().getReference().child("Courses")
                .child(courseName).child("Quizzes").child(quizDate).child("Students");
        getIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    final String userId = dataSnapshot1.getKey();
                    // Get user data

//                    User user1 = new User("nameeeee", "Emailllll");
//                    usersList.add(user1);

                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            String name = dataSnapshot2.child("userName").getValue().toString();
                            String email = dataSnapshot2.child("email").getValue().toString();
                            Toast.makeText(QuizDetailsActivity.this, name, Toast.LENGTH_SHORT).show();
                            Toast.makeText(QuizDetailsActivity.this, email, Toast.LENGTH_SHORT).show();
//                            User user1 = new User(name, email);
                            // todo next two lines does not work
                            User user1 = new User("nameeeee", "Emailllll");
                            usersList.add(user1);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(QuizDetailsActivity.this));
        recyclerView.setAdapter(quizStudentsAdapter);
    }
}
