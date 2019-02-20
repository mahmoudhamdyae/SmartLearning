package com.project.csed.smartlearning;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import android.support.v7.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Button Addbutton = findViewById(R.id.addbtn);
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            // Reading Data Once
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getType().equals("Teacher")) {

                        // todo this is a teacher activity
                        Toast.makeText(MainActivity.this, "This is Teacher Main Activity", Toast.LENGTH_SHORT).show();

                        // RecyclerView

                        final List<CourseModel> courseModelList = new ArrayList<>();
                        final CourseAdapter courseAdapter = new CourseAdapter(courseModelList, MainActivity.this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        recyclerView.setAdapter(courseAdapter);


                        //todo delete this 5 lines when we finish
                        //this is the first row in the teacher RecyclerView --test--
                        final CourseModel courseModel = new CourseModel();
                        courseModel.setCourseName("MathCourse");
                        courseModel.setStudentNo("StudentNo");
                        courseModel.setYearDate("YearDate");
                        courseModelList.add(courseModel);

                        // Add Button OnClick

                        Addbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                // Create AlertDialog and show.
                                LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
                                View popupInputDialogView = layoutInflater.inflate(R.layout.course_dialog, null);
                                // Create a AlertDialog Builder.
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                // Set title, icon, can not cancel properties.
                                alertDialogBuilder.setTitle("Enter The Course Name");
                                alertDialogBuilder.setIcon(R.drawable.ic_launcher_background);
                                alertDialogBuilder.setCancelable(true);
                                // Init popup dialog view and it's ui controls.

                                // Set the inflated layout view object to the AlertDialog builder.
                                alertDialogBuilder.setView(popupInputDialogView);
                                final AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                                alertDialogBuilder.setView(popupInputDialogView);

                                // this part for save or cancel the coursename from the dialog
                                Button savebtn = popupInputDialogView.findViewById(R.id.savebtn);
                                Button cancelbtn = popupInputDialogView.findViewById(R.id.cancelbtn);
                                final EditText coursename = popupInputDialogView.findViewById(R.id.coursename);
                                final EditText courseyear = popupInputDialogView.findViewById(R.id.year);
                                savebtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String name, year;
                                        name = coursename.getText().toString();
                                        year = courseyear.getText().toString();

                                        if (name != "" && !name.isEmpty() && year != "" && !year.isEmpty()) {

                                            CourseModel courseModel1 = new CourseModel();
                                            courseModel1.setCourseName(name);
                                            //todo we should put her the student number when we finsh
                                            courseModel1.setStudentNo("StudentNo");
                                            courseModel1.setYearDate(year);
                                            courseModelList.add(courseModel1);
                                            courseAdapter.notifyDataSetChanged();
                                            alertDialog.dismiss();

                                        } else {
                                            Toast.makeText(MainActivity.this, "Write the Course name and Course year", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                cancelbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });
                            }
                        });


                    } else if (user.getType().equals("Student")) {
                        Toast.makeText(MainActivity.this, "This is Student Main Activity", Toast.LENGTH_SHORT).show();
                        // todo this is a  student activity
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_item:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
