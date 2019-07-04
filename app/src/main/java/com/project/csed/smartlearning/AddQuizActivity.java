package com.project.csed.smartlearning;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddQuizActivity extends AppCompatActivity {

    Button addAnotherQuestion, finish;
    TextView questionNumberTextView;
    EditText questionEditText, option1EditText, option2EditText, option3EditText, option4EditText;
    RadioGroup radioGroup;
    String question, option1, option2, option3, option4, answer;
    int answerInt = 0;
    int questionNumber = 1;
    String courseName, dateString;
    final long[] quizNumber = new long[1];
    final long[] numberOfQuestions = new long[1];

    DatabaseReference quizReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quiz);

        // Examine the intent that was used to launch this activity,
        // in order to get course name and quiz number.
        courseName = getIntent().getStringExtra("course_name");

        // Get Quizzes number
        quizReference = FirebaseDatabase.getInstance().getReference().child("Quizzes").child(courseName);
        quizReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quizNumber[0] = dataSnapshot.getChildrenCount() + 1;

                if (getIntent().getStringExtra("addQuestion").equals("addQuestion")) {
                    // Add Question
                    final String s = getIntent().getStringExtra("quizDate");
                    // Get number of questions
                    quizReference.child(s).child("Questions").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                            numberOfQuestions[0] = dataSnapshot1.getChildrenCount();
                            addAnotherQuestion.setVisibility(View.GONE);
                            finish.setText(R.string.quiz_modify_add_question_button);
                            questionNumber = (int) numberOfQuestions[0] + 1;
                            dateString = s;
                            questionNumberTextView.setText(String.valueOf(questionNumber));

                            // Change the app bar to show quiz number
                            quizReference.child(dateString).child("number").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String title = "Quiz number " + dataSnapshot.getValue();
                                    setTitle(title);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
                else{
                    // Add Quiz

                    // Get current time
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd LLL, yyyy H:mm:s", Locale.getDefault());
                    Date date = new Date();
                    dateString = dateFormat.format(date);

                    Quiz quiz = new Quiz((int) (quizNumber[0]), dateString);
                    quizReference.child(dateString).setValue(quiz);

                    // Change the app bar to show quiz number
                    String title = "Quiz number " + (quizNumber[0]);
                    setTitle(title);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

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
                        && !TextUtils.isEmpty(option3) && !TextUtils.isEmpty(option4)){
                    addQuestion();
                    finish();
                }
                else if (getIntent().getStringExtra("addQuestion").equals("addQuestion"))
                    Toast.makeText(AddQuizActivity.this, R.string.add_quiz_empty_fields_toast, Toast.LENGTH_SHORT).show();
                else
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
