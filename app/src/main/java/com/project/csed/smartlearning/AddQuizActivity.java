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

import java.util.Date;

public class AddQuizActivity extends AppCompatActivity {

    Button addAnotherQuestion, finish;
    TextView questionNumberTextView;
    EditText questionEditText, option1EditText, option2EditText, option3EditText, option4EditText;
    RadioGroup radioGroup;
    String question, option1, option2, option3, option4, answer;
    int answerInt = 0;
    int questionNumber = 1;
    long quizNumber;
    String courseName, dateString;

    DatabaseReference quizReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quiz);

        // Examine the intent that was used to launch this activity,
        // in order to get course name and quiz number.
        Intent intent = getIntent();
        // todo The same problem here
        courseName = intent.getStringExtra("courseName");

        // Change the app bar to show course name
        setTitle(courseName);

        // Get Quizzes number
        // todo Change course1
        quizReference = FirebaseDatabase.getInstance().getReference().child("Courses").child(/*courseName*/"course1").child("Quizzes");
        quizReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quizNumber = dataSnapshot.getChildrenCount();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // Add Quiz number quizNumber to Database
        Date date = new Date();
        dateString = String.valueOf(date);
        // todo problem here - quizNumber is always the default value
        Quiz quiz = new Quiz((int) (quizNumber) + 1, dateString);
        quizReference.child(dateString).setValue(quiz);

        addAnotherQuestion = findViewById(R.id.add_another_question);
        finish = findViewById(R.id.finish);
        questionNumberTextView = findViewById(R.id.question_number);
        questionEditText = findViewById(R.id.question);
        option1EditText = findViewById(R.id.option1);
        option2EditText = findViewById(R.id.option2);
        option3EditText = findViewById(R.id.option3);
        option4EditText = findViewById(R.id.option4);
        radioGroup = findViewById(R.id.radioGroup);

        questionNumberTextView.setText(String.valueOf(questionNumber));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            public void onCheckedChanged(RadioGroup group, int checkedId) {
               switch(checkedId){
                    case R.id.option1_radio_button:
                        answerInt = 1;
                        break;
                    case R.id.option2_radio_button:
                        answerInt = 2;
                        break;
                    case R.id.option3_radio_button:
                        answerInt = 3;
                        break;
                     case R.id.option4_radio_button:
                        answerInt = 4;
                        break;
                }
            }
        });

        addAnotherQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question = questionEditText.getText().toString().trim();
                option1 = option1EditText.getText().toString().trim();
                option2 = option2EditText.getText().toString().trim();
                option3 = option3EditText.getText().toString().trim();
                option4 = option4EditText.getText().toString().trim();
                switch(answerInt){
                    case 1:
                        answer = option1;
                        break;
                    case 2:
                        answer = option2;
                        break;
                    case 3:
                        answer = option3;
                        break;
                    case 4:
                        answer = option4;
                        break;
                }
                if (!TextUtils.isEmpty(question) && !TextUtils.isEmpty(option1) && !TextUtils.isEmpty(option2)
                        && !TextUtils.isEmpty(option3) && !TextUtils.isEmpty(option4) && !TextUtils.isEmpty(answer)){
                    addQuestion();
                    questionEditText.setText("");
                    option1EditText.setText("");
                    option2EditText.setText("");
                    option3EditText.setText("");
                    option4EditText.setText("");
                    radioGroup.clearCheck();
                    questionNumberTextView.setText(String.valueOf(questionNumber));
                }
                else
                    Toast.makeText(AddQuizActivity.this, R.string.add_quiz_empty_fields_toast, Toast.LENGTH_SHORT).show();

            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question = questionEditText.getText().toString().trim();
                option1 = option1EditText.getText().toString().trim();
                option2 = option2EditText.getText().toString().trim();
                option3 = option3EditText.getText().toString().trim();
                option4 = option4EditText.getText().toString().trim();
                if (!TextUtils.isEmpty(question) && !TextUtils.isEmpty(option1) && !TextUtils.isEmpty(option2)
                        && !TextUtils.isEmpty(option3) && !TextUtils.isEmpty(option4))
                    addQuestion();
                finish();
            }
        });
    }

    private void addQuestion(){
        Question question1 = new Question(questionNumber, question, option1, option2, option3, option4, answer);
        quizReference.child(dateString).child("Questions").child(String.valueOf(questionNumber)).setValue(question1);
        questionNumber++;
        answerInt = 0;
    }
}
