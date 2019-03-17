package com.project.csed.smartlearning;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class QuizModifyActivity extends AppCompatActivity {

    Button nextQuestion, finish;
    TextView questionNumberTextView;
    EditText questionEditText, option1EditText, option2EditText, option3EditText, option4EditText;
    RadioGroup radioGroup;

    String quizDate;
    int questionNumber = 1;

    DatabaseReference quizReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quiz);

        // Examine the intent that was used to launch this activity,
        // in order to get quiz date.
        Intent intent = getIntent();
        quizDate = intent.getStringExtra("quizDate");

        // todo change course1
        quizReference = FirebaseDatabase.getInstance().getReference().child("Courses").child("course1").child("Quizzes").child(quizDate);
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

        nextQuestion = findViewById(R.id.add_another_question);
        finish = findViewById(R.id.finish);
        questionNumberTextView = findViewById(R.id.question_number);
        questionEditText = findViewById(R.id.question);
        option1EditText = findViewById(R.id.option1);
        option2EditText = findViewById(R.id.option2);
        option3EditText = findViewById(R.id.option3);
        option4EditText = findViewById(R.id.option4);
        radioGroup = findViewById(R.id.radioGroup);

        readQuestion();

        nextQuestion.setText(R.string.quiz_modify_next_question_button);
        nextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo we should know number of question
                if (validate()){
                    questionNumber++;
                    readQuestion();
                    modifyQuiz();
                }
                else
                    Toast.makeText(QuizModifyActivity.this, R.string.add_quiz_empty_fields_toast, Toast.LENGTH_SHORT).show();
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    modifyQuiz();
                    finish();
                }
                else
                    Toast.makeText(QuizModifyActivity.this, R.string.add_quiz_empty_fields_toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validate(){
        String question = questionEditText.getText().toString().trim();
        String option1 = option1EditText.getText().toString().trim();
        String option2 = option2EditText.getText().toString().trim();
        String option3 = option3EditText.getText().toString().trim();
        String option4 = option4EditText.getText().toString().trim();

        return (!TextUtils.isEmpty(question) && !TextUtils.isEmpty(option1) && !TextUtils.isEmpty(option2)
                && !TextUtils.isEmpty(option3) && !TextUtils.isEmpty(option4));
    }

    private void readQuestion(){
        quizReference.child("Questions").child(String.valueOf(questionNumber)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Question question = dataSnapshot.getValue(Question.class);
                    questionNumberTextView.setText(String.valueOf(question.getNumber()));
                    questionEditText.setText(question.getQuestion());
                    option1EditText.setText(question.getOption1());
                    option2EditText.setText(question.getOption2());
                    option3EditText.setText(question.getOption3());
                    option4EditText.setText(question.getOption4());
                    if (question.getAnswer().equals(question.getOption1()))
                        radioGroup.check(R.id.option1_radio_button);
                    else if (question.getAnswer().equals(question.getOption2()))
                        radioGroup.check(R.id.option2_radio_button);
                    else if (question.getAnswer().equals(question.getOption3()))
                        radioGroup.check(R.id.option3_radio_button);
                    else
                        radioGroup.check(R.id.option4_radio_button);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void modifyQuiz(){
        // todo update quiz
    }
}
