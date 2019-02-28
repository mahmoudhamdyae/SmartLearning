package com.project.csed.smartlearning;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CourseSearchActivity extends AppCompatActivity {
    ListView listView;
    SearchView searchView;
    ArrayAdapter<String> adapter;
    List<String> list;

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
                list.add(courseModel.getCourseName());
            }
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        mCourseDatabaseReference.addChildEventListener(mChildEventListener);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
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
//                String s = adapter.getItem(position);
                // Create an AlertDialog.Builder and set the message, and click listeners
                // for the positive and negative buttons on the dialog.
                AlertDialog.Builder builder = new AlertDialog.Builder(CourseSearchActivity.this);
                builder.setMessage(R.string.course_search_dialog_confirm_msg);
                builder.setPositiveButton(R.string.course_search_dialog_enroll_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked the "Enroll" button, so enroll the Course.
                        // todo enroll the course
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