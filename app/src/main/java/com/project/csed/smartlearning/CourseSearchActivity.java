package com.project.csed.smartlearning;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CourseSearchActivity extends AppCompatActivity {
    ListView listView;
    SearchView searchView;
    CourseSearchAdapter adapter;
    ArrayList<CourseModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_search);

        searchView = findViewById(R.id.search_course);
        listView = findViewById(R.id.courses_list_view);
        list = new ArrayList<>();

        DatabaseReference mCourseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Courses");

        ChildEventListener mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                CourseModel courseModel = dataSnapshot.getValue(CourseModel.class);
                list.add(courseModel);
            }
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        mCourseDatabaseReference.addChildEventListener(mChildEventListener);

        adapter = new CourseSearchAdapter(this, list);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(list.contains(query)){
                    adapter.getFilter().filter(query);
                }else{
                    Toast.makeText(CourseSearchActivity.this, R.string.course_search_not_found_toast,Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final CourseModel courseModel = adapter.getItem(position);
                // Create an AlertDialog.Builder and set the message, and click listeners
                // for the positive and negative buttons on the dialog.
                AlertDialog.Builder builder = new AlertDialog.Builder(CourseSearchActivity.this);
                builder.setMessage(R.string.course_search_dialog_confirm_msg);
                builder.setPositiveButton(R.string.course_search_dialog_enroll_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked the "Enroll" button, so enroll the Course.
                        final String userKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        // Write in courses table
                        final DatabaseReference courseReference = FirebaseDatabase.getInstance().getReference().child("Courses").child(courseModel.getCourseName()).child("Students");
                        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userKey);
                        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                courseReference.child(user.getUserName()).setValue(user);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                        // Write in users table
                        DatabaseReference userReference1 = FirebaseDatabase.getInstance().getReference()
                                .child("Users").child(userKey).child("Courses");
                        userReference1.child(courseModel.getCourseName()).setValue(courseModel);
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.course_search_dialog_cancel_button, new DialogInterface.OnClickListener() {
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
        });
    }
}