package com.project.csed.smartlearning;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {
    private TextInputLayout userNameEditText, emailEditText, passwordEditText;
    private String type;
    private ProgressDialog progressDialog;


    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        userNameEditText = findViewById(R.id.user_name);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.teacherrb:
                        type = "Teacher";
                        break;
                    case R.id.studentrb:
                        type = "Student";
                        break;
                }
            }
        });
        Button signUp = findViewById(R.id.signup);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = userNameEditText.getEditText().getText().toString().trim();
                final String email = emailEditText.getEditText().getText().toString().trim();
                final String password = passwordEditText.getEditText().getText().toString().trim();

                String fillhere = getResources().getString((R.string.fillhere_signuplogin_error));
                if (userName.isEmpty() || userName.equals(" "))
                    userNameEditText.setError(fillhere);
                else if (email.isEmpty() || email.equals(" "))
                    emailEditText.setError(fillhere);
                else if(password.isEmpty()||password.equals(" "))
                    passwordEditText.setError(fillhere);
                else if (TextUtils.isEmpty(type))
                    Toast.makeText(SignUpActivity.this, R.string.sign_up_type_not_choosed_text_toast, Toast.LENGTH_SHORT).show();
                else {
                    // For unique user name
                    Query userNameQuery = FirebaseDatabase.getInstance().getReference().child("Users")
                            .orderByChild("userName").equalTo(userName);
                    userNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() > 0)
                                Toast.makeText(SignUpActivity.this, R.string.login_choose_another_user_name_toast
                                        , Toast.LENGTH_SHORT).show();
                            else
                                newUserSignUp(userName, email, password, type);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }
        });

    }

    public void newUserSignUp(final String userName, final String email, final String password, final String type){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing your request, please wait...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success
                                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    User user = new User(userName, email, password, type, userId);
                                    databaseReference.child(mAuth.getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            if (task.isSuccessful()){
                                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }else
                                                Toast.makeText(SignUpActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignUpActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
    }
}
