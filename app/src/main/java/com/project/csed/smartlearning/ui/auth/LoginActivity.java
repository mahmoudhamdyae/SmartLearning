package com.project.csed.smartlearning.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.project.csed.smartlearning.R;
import com.project.csed.smartlearning.ui.course.MainActivity;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout emailTextView, passwordTextView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        Button login = findViewById(R.id.login);
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.password);

        // If the user pressed login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailTextView.getEditText().getText().toString().trim();
                String password = passwordTextView.getEditText().getText().toString().trim();

                String fillhere = getResources().getString(R.string.fillhere_signuplogin_error);
                if(email.isEmpty()||email.equals(" "))
                    emailTextView.setError(fillhere);
                else if(password.isEmpty()||password.equals(" "))
                    passwordTextView.setError(fillhere);
                else
                    logIn(email, password);
            }
        });


    }

    private void logIn(String email, String password) {
        final ProgressDialog progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Processing your request, please wait...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                        else
                            Toast.makeText(LoginActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //when signup edit text is clicked
    public void signup(View view) {
        Intent intent=new Intent(LoginActivity.this, SignUpActivity.class);
        finish();
        startActivity(intent);

    }
}
