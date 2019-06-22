package com.project.csed.smartlearning;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuizAnswerActivity extends AppCompatActivity {
    private TextView questionNumberText, questionText, option1Text, option2Text, option3Text, option4Text;
    private Button nextQuestion;

    String quizDate, answer, courseName, quizNumber;
    int questionNumber = 1, sum = 0, numberOfQuestions = 0;

    DatabaseReference quizReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_answer);

        // Examine the intent that was used to launch this activity,
        // in order to get quiz number.
        Intent intent = getIntent();
        quizDate = intent.getStringExtra("quizDate");
        courseName = intent.getStringExtra("courseName");
        quizNumber = intent.getStringExtra("quizNumber");

        // Change the app bar to show quiz number
        quizReference = FirebaseDatabase.getInstance().getReference().child("Quizzes").child(courseName).child(quizDate);
        quizReference.child("number").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String title = "Quiz number " + dataSnapshot.getValue();
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
        nextQuestion = findViewById(R.id.next_question_button);

        final int[] questNo = new int[1];
        quizReference.child("Questions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Snapshot) {
                questNo[0] = (int) Snapshot.getChildrenCount();
                numberOfQuestions = questNo[0];
                readQuestions();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        option1Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeAnswersUncClickable();
                if (checkAnswer(option1Text.getText().toString()))
                    option1Text.setBackgroundColor(Color.GREEN);
                else {
                    option1Text.setBackgroundColor(Color.RED);
                    setColorForRightAnswer();
                }
                questionNumber++;
                nextQuestion.setVisibility(View.VISIBLE);
            }
        });

        option2Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeAnswersUncClickable();
                if (checkAnswer(option2Text.getText().toString()))
                    option2Text.setBackgroundColor(Color.GREEN);
                else {
                    option2Text.setBackgroundColor(Color.RED);
                    setColorForRightAnswer();
                }
                questionNumber++;
                nextQuestion.setVisibility(View.VISIBLE);
            }
        });

        option3Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeAnswersUncClickable();
                if (checkAnswer(option3Text.getText().toString()))
                    option3Text.setBackgroundColor(Color.GREEN);
                else {
                    option3Text.setBackgroundColor(Color.RED);
                    setColorForRightAnswer();
                }
                questionNumber++;
                nextQuestion.setVisibility(View.VISIBLE);
            }
        });

        option4Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeAnswersUncClickable();
                if (checkAnswer(option4Text.getText().toString()))
                    option4Text.setBackgroundColor(Color.GREEN);
                else {
                    option4Text.setBackgroundColor(Color.RED);
                    setColorForRightAnswer();
                }
                questionNumber++;
                nextQuestion.setVisibility(View.VISIBLE);
            }
        });

        nextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readQuestions();
                nextQuestion.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void readQuestions() {
        // Return color of the texts
        option1Text.setBackgroundColor(Color.GRAY);
        option2Text.setBackgroundColor(Color.GRAY);
        option3Text.setBackgroundColor(Color.GRAY);
        option4Text.setBackgroundColor(Color.GRAY);

        // Make the text clickable again
        option1Text.setClickable(true);
        option2Text.setClickable(true);
        option3Text.setClickable(true);
        option4Text.setClickable(true);

        if (questionNumber == numberOfQuestions)
            nextQuestion.setText(R.string.add_quiz_finish_button);

        if (questionNumber <= numberOfQuestions) {
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
        }
        else {
            // Open Statistics Activity to show the information of the quiz and save it in firebase
            Intent intent = new Intent(QuizAnswerActivity.this, QuizStatisticsActivity.class);
            intent.putExtra("correctAnswers", String.valueOf(sum));
            intent.putExtra("numberOfQuestions", String.valueOf(numberOfQuestions));
            intent.putExtra("courseName", courseName);
            intent.putExtra("quizDate", quizDate);
            intent.putExtra("quizNumber", quizNumber);
            startActivity(intent);
            finish();
        }
    }

    /**
     *
     * @param optionSelected is the answer that the student chose
     * @return true if the answer is right, false if it is wrong
     */
    private boolean checkAnswer(String optionSelected) {
        if (optionSelected.equals(answer)){
            sum++;
            return true;
        }
        else
            return false;
    }

    private void makeAnswersUncClickable() {
        option1Text.setClickable(false);
        option2Text.setClickable(false);
        option3Text.setClickable(false);
        option4Text.setClickable(false);
    }

    private void setColorForRightAnswer() {
        if (option1Text.getText().toString().equals(answer))
            option1Text.setBackgroundColor(Color.GREEN);
        else if (option2Text.getText().toString().equals(answer))
            option2Text.setBackgroundColor(Color.GREEN);
        else if (option3Text.getText().toString().equals(answer))
            option3Text.setBackgroundColor(Color.GREEN);
        else if (option4Text.getText().toString().equals(answer))
            option4Text.setBackgroundColor(Color.GREEN);
    }
}
