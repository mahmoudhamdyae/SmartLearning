package com.project.csed.smartlearning;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CourseActivity extends AppCompatActivity {
    CardView quizzes, addStudent, materials, chat;
    String courseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        // Examine the intent that was used to launch this activity,
        // in order to get course name.
        Intent intent = getIntent();
        courseName = intent.getStringExtra("name");
        // Change the app bar to show course name
        setTitle(courseName);

        quizzes = findViewById(R.id.quizzes);
        addStudent = findViewById(R.id.add_student);
        materials = findViewById(R.id.materials);
        chat = findViewById(R.id.chat);

        quizzes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo Open quizzes activity
            }
        });

        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo Open add student activity
                Intent activ = new Intent(CourseActivity.this,AddStudentActivity.class);
                activ.putExtra("name", courseName);
                startActivity(activ);
            }
        });

        materials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo open materials activity
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo open chat activity
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
        switch (item.getItemId()) {
            case R.id.delete:
                deleteCourse();
                return true;
            case R.id.students:
                // todo open students activity
                return true;
            case R.id.course_details:
                // todo open course details activity
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteCourse(){
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.course_delete_dialog_msg);
        builder.setPositiveButton(R.string.course_delete_dialog_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the Course.
                DatabaseReference mCourseDatabaseReference = FirebaseDatabase.getInstance().getReference().getRoot().child("Courses").child(courseName);
                mCourseDatabaseReference.setValue(null);
                // todo remove course from recyclerView
                finish();
            }
        });
        builder.setNegativeButton(R.string.course_delete_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
