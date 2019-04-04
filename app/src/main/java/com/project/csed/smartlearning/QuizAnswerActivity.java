package com.project.csed.smartlearning;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuizAnswerActivity extends AppCompatActivity {
    private TextView questionNumberText, questionText, option1Text, option2Text, option3Text, option4Text;

    String quizDate, answer;
    int questionNumber = 1, sum = 0;
//    long numberOfQuestions = 0;

    DatabaseReference quizReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_answer);

        // Examine the intent that was used to launch this activity,
        // in order to get quiz number.
        Intent intent = getIntent();
        quizDate = intent.getStringExtra("quizDate");

        // Change the app bar to show quiz number
        // todo change course1
        quizReference = FirebaseDatabase.getInstance().getReference().child("Courses").child("course1").child("Quizzes").child(quizDate);
        quizReference.child("number").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String title = "Quiz number " + String.valueOf(dataSnapshot.getValue());
                setTitle(title);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        questionNumberText = findViewById(R.id.question_number);
        questionText = findViewById(R.id.question);
        option1Text = findViewById(R.id.option1);
        option2Text = findViewById(R.id.option2);
        option3Text = findViewById(R.id.option3);
        option4Text = findViewById(R.id.option4);

        // todo same problem here, can not get number of questions
//        quizReference.child("Questions").child(String.valueOf(questionNumber)).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                numberOfQuestions = dataSnapshot.getChildrenCount();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });

        readQuestions();

        option1Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(option1Text.getText().toString());
                questionNumber++;
                readQuestions();
            }
        });

        option2Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(option2Text.getText().toString());
                questionNumber++;
                readQuestions();
            }
        });

        option3Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(option3Text.getText().toString());
                questionNumber++;
                readQuestions();
            }
        });

        option4Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(option4Text.getText().toString());
                questionNumber++;
                readQuestions();
            }
        });
    }

    private void readQuestions() {
//        if (questionNumber <= (int) numberOfQuestions) {
            questionNumberText.setText(String.valueOf(questionNumber));
            quizReference.child("Questions").child(String.valueOf(questionNumber)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Question question = dataSnapshot.getValue(Question.class);

                        String questionString, option1, option2, option3, option4;
                        questionString = question.getQuestion();
                        option1 = question.getOption1();
                        option2 = question.getOption2();
                        option3 = question.getOption3();
                        option4 = question.getOption4();

                        questionText.setText(questionString);
                        option1Text.setText(option1);
                        option2Text.setText(option2);
                        option3Text.setText(option3);
                        option4Text.setText(option4);

                        answer = question.getAnswer();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
//        }
//        else {
//            // todo Statistics Activity to show the information of the quiz and save it in firebase
//            Intent intent = new Intent(QuizAnswerActivity.this, Statistics.class);
//            intent.putExtra("correctAnswer", String.valueOf(sum));
//            intent.putExtra("numberOfQuestions", String.valueOf(numberOfQuestions));
//            startActivity(intent);
//            finish();
//        }
    }

    private void checkAnswer(String optionSelected) {
        if (optionSelected.equals(answer)){
            sum++;
        }
    }
}
