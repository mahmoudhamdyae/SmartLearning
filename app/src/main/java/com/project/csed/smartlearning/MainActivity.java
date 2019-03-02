package com.project.csed.smartlearning;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import android.support.v7.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button Addbutton;
    RecyclerView recyclerView;
    public String year = "1";

    private DatabaseReference mCourseDatabaseReference;
    private DatabaseReference usersReference;
    private ChildEventListener mChildEventListener;

    List<courserForStudentPOJO> coursesOfStudent=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Addbutton = findViewById(R.id.addbtn);
        recyclerView = findViewById(R.id.recyclerView);
        //Hide the add button and the action bar on scrolling
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int mLastFirstVisibleItem = 0;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        Addbutton.setVisibility(View.VISIBLE);
                        break;
                    default:
                        Addbutton.setVisibility(View.GONE);
                        break;
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                final int currentFirstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                super.onScrolled(recyclerView, dx, dy);
                if (currentFirstVisibleItem > this.mLastFirstVisibleItem) {
                    MainActivity.this.getSupportActionBar().hide();
                } else if (currentFirstVisibleItem < this.mLastFirstVisibleItem) {
                    MainActivity.this.getSupportActionBar().show();
                }

                this.mLastFirstVisibleItem = currentFirstVisibleItem;
            }
        });

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            mCourseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Courses");
            usersReference = FirebaseDatabase.getInstance().getReference().child("Users");
            // Reading Data Once
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getType().equals("Teacher")) {
                        Toast.makeText(MainActivity.this, "This is Teacher Main Activity", Toast.LENGTH_SHORT).show();
                        teacherActivity(user);
                    } else if (user.getType().equals("Student")) {
                        Toast.makeText(MainActivity.this, "This is Student Main Activity", Toast.LENGTH_SHORT).show();
                        studentActivity(user);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    private void teacherActivity (final User user){
        // RecyclerView

        final List<CourseModel> courseModelList = new ArrayList<>();
        final CourseAdapter courseAdapter = new CourseAdapter(courseModelList, MainActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(courseAdapter);

        //todo delete this 5 lines when we finish
        //this is the first row in the teacher RecyclerView --test--
        CourseModel courseModel2 = new CourseModel();
        courseModel2.setCourseName("MathCourse");
        courseModel2.setStudentNo("StudentNo");
        courseModel2.setYearDate("YearDate");
        courseModelList.add(courseModel2);
////
        // Read courses from database
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                CourseModel course = dataSnapshot.getValue(CourseModel.class);
                if (user.getUserName().equals(course.getTeacherName())){
                    courseModelList.add(course);
                    courseAdapter.notifyDataSetChanged();
                }
            }
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        mCourseDatabaseReference.addChildEventListener(mChildEventListener);

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
                final Spinner courseyear = popupInputDialogView.findViewById(R.id.year);
                List<String> spinnerList = new ArrayList<>();
                spinnerList.add("1");
                spinnerList.add("2");
                spinnerList.add("3");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, spinnerList);
                courseyear.setAdapter(adapter);
                savebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String name = coursename.getText().toString();
                        final String year = courseyear.getSelectedItem().toString();
                        if (!name.isEmpty()) {

                            // Write course information on database
                            Thread mainThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // For unique user name
                                    Query userNameQuery = FirebaseDatabase.getInstance().getReference().child("Courses")
                                            .orderByChild("courseName").equalTo(name);
                                    userNameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.getChildrenCount() > 0)
                                                // The course name existed
                                                Toast.makeText(MainActivity.this, R.string.main_activity_choose_another_course_name_toast
                                                        , Toast.LENGTH_SHORT).show();
                                            else{
                                                // The course name does not exist
                                                //todo we should put here the student number when we finish
                                                final CourseModel courseModel = new CourseModel(name,"StudentNo", year, user.getUserName());
                                                mCourseDatabaseReference.child(name).setValue(courseModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            alertDialog.dismiss();
                                                            Toast.makeText(MainActivity.this, R.string.main_activity_course_created_successfully_toast, Toast.LENGTH_SHORT).show();
                                                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                                            String userkey = currentUser.getUid();
                                                            usersReference.child(userkey).child("Courses").child(name).setValue(courseModel);
                                                        }
                                                        else
                                                            Toast.makeText(MainActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                }
                            });
                            mainThread.start();

                        } else {
                            Toast.makeText(MainActivity.this, R.string.main_activity_empty_course_name_toast, Toast.LENGTH_SHORT).show();
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
    }

    private void studentActivity(User user){
        // todo this is a  student activity
        courserForStudentPOJO myfirstcourse = new courserForStudentPOJO("amr","al course al 7lw");



        coursesOfStudent.add(myfirstcourse);



        Context context2;

        CourseAdapterForStudent courseAdapterForStudent=new CourseAdapterForStudent(coursesOfStudent,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(courseAdapterForStudent);
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
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
