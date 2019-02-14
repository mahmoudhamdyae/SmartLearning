package com.project.csed.smartlearning;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText emailTextView, passwordTextView;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        Button login = findViewById(R.id.login);
        Button signUp = findViewById(R.id.signup);
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.password);

        // If the user pressed login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailTextView.getText().toString().trim();
                String password = passwordTextView.getText().toString().trim();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
                    logIn(email, password);
                else
                    Toast.makeText(LoginActivity.this, R.string.login_empty_edit_text_toast, Toast.LENGTH_SHORT).show();
            }
        });

        // If the user pressed signUp button
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, SignUpActivity.class);
                finish();
                startActivity(intent);
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
}
