package com.project.csed.smartlearning.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.csed.smartlearning.R;
import com.project.csed.smartlearning.models.User;
import com.project.csed.smartlearning.ui.course.MainActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private TextInputLayout userNameEditText, emailEditText, passwordEditText;
    private CircleImageView profileImage;
    private String type;
    private ProgressDialog progressDialog;
    private Uri imageUri;


    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        imageUri = null;

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("images");
        userNameEditText = findViewById(R.id.user_name);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);

        profileImage = findViewById(R.id.profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

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
                                    final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                    if (imageUri != null){
                                        // Upload profile image
                                        StorageReference userProfile = mStorageRef.child(userId + ".jpg");
                                        userProfile.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> uploadTask) {
                                                if (!uploadTask.isSuccessful())
                                                    Toast.makeText(SignUpActivity.this, "Error : " + uploadTask.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null)
            return;
        if (requestCode == PICK_IMAGE){
                imageUri = data.getData();
                profileImage.setImageURI(imageUri);
        }
    }
}
