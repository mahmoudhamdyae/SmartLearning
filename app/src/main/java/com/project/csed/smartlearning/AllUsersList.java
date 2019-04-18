package com.project.csed.smartlearning;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AllUsersList extends AppCompatActivity {
    private static final String TAG = "AllUsersList";
    DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    DatabaseReference child2;
    DatabaseReference child3;
    private String senderName,courseName;

    ArrayList<usersListPOJO> users = new ArrayList<>();
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users_list);


        //get clicked course name from course Activity
        Intent i=getIntent();
        courseName=i.getStringExtra("courseName");
        Log.i("Clicked course is ",courseName);
        //reference to students of clicked course
        child3= root.child("Courses").child(courseName).child("Students");
        //reference to teacher of clicked course
        child2 = root.child("Courses").child(courseName);
      setTitle("Course {"+courseName+"} members");



     readData(new FirebaseCallback() {
         @Override
         public void onCallback(ArrayList<usersListPOJO> list) {



             //  loop users list to remove loged in user
             for (int i = 0; i < users.size(); i++) {
                 if (users.get(i).getUserId().equals(getUserId())) {
                     //save sender name for later usage
                     senderName = users.get(i).getName();
                     users.remove(i);
                 }
             }


             //at this point all data is added to users list and ready to show
             showList();



             //open chat activity when user from list is clicked
             listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                 @Override
                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                     Intent i = new Intent(AllUsersList.this, OneToOneChatActivity.class);
                     i.putExtra("ReceiverName", users.get(position).getName());
                     i.putExtra("ReceiverEmail", users.get(position).getEmail());
                     i.putExtra("senderName", senderName);
                     AllUsersList.this.startActivity(i);
                 }
             });

         }
     });


    }

    private void readData(final FirebaseCallback firebaseCallback)
    {
        //get teacher name from DB
        child2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                users.add(new usersListPOJO(dataSnapshot.child("teacherName").getValue().toString(), "Course instructor", ""));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //get students enrolled from DB
        child3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    users.add(new usersListPOJO(ds.child("userName").getValue().toString()
                            , ds.child("email").getValue().toString(), ds.child("userId").getValue().toString()));
                }
                firebaseCallback.onCallback(users);
                //showing the data in the list

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }
    private void showList() {
        userListAdapter myListAdapter = new userListAdapter(this, users) ;

        // Get a reference to the ListView, and attach the adapter to the listView.
        listView = (ListView) findViewById(R.id.fullUsersList);
        listView.setAdapter(myListAdapter);


        //


    }



    private interface FirebaseCallback {
        void onCallback(ArrayList<usersListPOJO> list);
    }

    private String getUserId() {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentFirebaseUser.getUid();
    }

}
