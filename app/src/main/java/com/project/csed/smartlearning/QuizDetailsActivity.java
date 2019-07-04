package com.project.csed.smartlearning;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuizDetailsActivity extends AppCompatActivity {
    Button modifyQuiz, addQuestion;
    String quizDate, courseName;
    String name, email, userId;
    TextView noOneSolvedThisQuizText,ListOfStudentText;
    QuizStudentsAdapter quizStudentsAdapter;

    List<User> usersList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageView sleepingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_details);
        sleepingImage = findViewById(R.id.sleepingImage);
        quizStudentsAdapter = new QuizStudentsAdapter(QuizDetailsActivity.this, usersList);
        recyclerView = findViewById(R.id.recyclerView);

        noOneSolvedThisQuizText = findViewById(R.id.text);
        ListOfStudentText=findViewById(R.id.text2);

        // Examine the intent that was used to launch this activity,
        // in order to get quiz date.
        Intent intent = getIntent();
        quizDate = intent.getStringExtra("quizDate");
        courseName = intent.getStringExtra("courseName");

        addQuestion = findViewById(R.id.add_question);
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuestionFun();
            }
        });

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




        // Get Users id
        DatabaseReference quizReference = FirebaseDatabase.getInstance().getReference().child("Quizzes").child(courseName).child(quizDate);
        quizReference.child("number").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String title = "Quiz number " + dataSnapshot.getValue();
                // Change the app bar to show quiz number
                setTitle(title);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        //
        readData(new FireBaseCallBack() {
            @Override
            public void onCallBack(List<User> myUserList) {
                //if this method is called it means there are students who solved the quiz
                sleepingImage.setVisibility(View.GONE);
                noOneSolvedThisQuizText.setVisibility(View.GONE);
                ListOfStudentText.setVisibility(View.VISIBLE);



            }
        });


        //this is called if no students solved the quiz
        sleepingImage.setVisibility(View.VISIBLE);
        noOneSolvedThisQuizText.setVisibility(View.VISIBLE);
        ListOfStudentText.setVisibility(View.GONE);

    }



    private void addQuestionFun() {
        Intent intent = new Intent(QuizDetailsActivity.this, AddQuizActivity.class);
        intent.putExtra("addQuestion", "addQuestion");
        intent.putExtra("course_name", courseName);
        intent.putExtra("quizDate", quizDate);
        startActivity(intent);
    }


    private void readData(final FireBaseCallBack callBack) {

        DatabaseReference getIdRef = FirebaseDatabase.getInstance().getReference().child("Quizzes")
                .child(courseName).child(quizDate).child("Students");
        getIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    userId = dataSnapshot1.getKey();
                    final String degree = dataSnapshot1.getValue().toString();

                    // Get user data
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            name = dataSnapshot2.child("userName").getValue().toString();
                            email = dataSnapshot2.child("email").getValue().toString();

                            User user = new User(name, email, degree, userId);
                            usersList.add(user);
                            quizStudentsAdapter.notifyDataSetChanged();

                            recyclerView.setVisibility(View.VISIBLE);
                            noOneSolvedThisQuizText.setText(R.string.quiz_details_students_who_solve_this_quiz);

                            callBack.onCallBack(usersList);

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

    private interface FireBaseCallBack {
        void onCallBack(List<User> myUserList);
    }


    @Override
    protected void onStart() {
        super.onStart();

    }
}

