package com.project.csed.smartlearning;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CourseActivity extends AppCompatActivity {
    CardView quizzes, addStudent, materials, chat,privateChat;
    String courseName, userType;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    User user ;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        //To Only Get The UserName
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                if (user != null) {
                    Toast.makeText(CourseActivity.this, user.getUserName(), Toast.LENGTH_SHORT).show();
                    sharedPreferences= getSharedPreferences("userinfo",MODE_PRIVATE);
                    SharedPreferences.Editor editor =sharedPreferences.edit();
                    editor.putString("username",user.getUserName());
                    editor.commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        // Examine the intent that was used to launch this activity,
        // in order to get course name.
        Intent intent = getIntent();
        courseName = intent.getStringExtra("course_name");

        // Change the app bar to show course name
        setTitle(courseName);

        quizzes = findViewById(R.id.quizzes);
        addStudent = findViewById(R.id.add_student);
        materials = findViewById(R.id.materials);
        chat = findViewById(R.id.chat);
        privateChat=findViewById(R.id.private_chat);

        userType = intent.getStringExtra("user_type");
        if (userType.equals("Student"))
            addStudent.setVisibility(View.GONE);

        quizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Quiz Activity
                Intent intent = new Intent(CourseActivity.this, QuizActivity.class);
                intent.putExtra("course_name", courseName);
                intent.putExtra("userType", userType);
                startActivity(intent);
            }
        });

        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open add student activity
                Intent activ = new Intent(CourseActivity.this,AddStudentActivity.class);
                activ.putExtra("name", courseName);
                startActivity(activ);
            }
        });

        materials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open materials activity
                Intent intent = new Intent(CourseActivity.this, MaterialActivity.class);
                intent.putExtra("course_name", courseName);
                intent.putExtra("userType", userType);
                startActivity(intent);
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open chat activity
                Intent intent1=new Intent(CourseActivity.this,ChatActivity.class);
                intent1.putExtra("name",user.getUserName());
                intent1.putExtra("coursename",courseName);
                intent1.putExtra("userType",userType);
                startActivity(intent1);
            }
        });

        privateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open private chat activity with list of only course members and pass course name to next activity
                Intent i=new Intent(CourseActivity.this,AllUsersList.class);
           //     i.putExtra("name",user.getUserName());
                i.putExtra("courseName",courseName);

                startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.course_details) {
            // Open course details dialog
            courseDetailsDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void courseDetailsDialog() {
        // Create AlertDialog and show.
        LayoutInflater layoutInflater = LayoutInflater.from(CourseActivity.this);
        View popupInputDialogView = layoutInflater.inflate(R.layout.course_details_dialog, null);
        // Create a AlertDialog Builder.
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CourseActivity.this);
        // Set title, icon, can not cancel properties.
        alertDialogBuilder.setTitle(R.string.action_course_details);
        alertDialogBuilder.setCancelable(true);
        // Init popup dialog view and it's ui controls.

        // Set the inflated layout view object to the AlertDialog builder.
        alertDialogBuilder.setView(popupInputDialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialogBuilder.setView(popupInputDialogView);

        final TextView courseNameText = popupInputDialogView.findViewById(R.id.course_name);
        final TextView teacherNameText = popupInputDialogView.findViewById(R.id.teacher_name);
        final TextView yearText = popupInputDialogView.findViewById(R.id.year);
        final TextView numberOfStudentsText = popupInputDialogView.findViewById(R.id.number_of_students);

        DatabaseReference courseRef = FirebaseDatabase.getInstance().getReference().child("Courses").child(courseName);
        courseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CourseModel courseModel = dataSnapshot.getValue(CourseModel.class);
                courseNameText.setText(courseModel.getCourseName());
                teacherNameText.setText(courseModel.getTeacherName());
                yearText.setText(courseModel.getYearDate());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        courseRef.child("Students").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Snapshot) {
                numberOfStudentsText.setText(String.valueOf(Snapshot.getChildrenCount()));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
