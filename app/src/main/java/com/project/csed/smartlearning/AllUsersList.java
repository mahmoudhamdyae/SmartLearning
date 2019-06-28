package com.project.csed.smartlearning;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllUsersList extends AppCompatActivity {

    DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    DatabaseReference child2,child3,child4;

    private String courseName, senderName;


    ArrayList<usersListPOJO> users = new ArrayList<>();
    ListView listView;
    private TextView HeaderText,noStudentsText;
    ImageView noStudents,chatIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users_list);



        //get senderName
        //reference to users list
        child4=root.child("Users");

        //get clicked course name from course Activity
        Intent i=getIntent();
        courseName=i.getStringExtra("courseName");
//        Log.i("saxs",senderName);

        Log.i("Clicked course is ",courseName);
        //reference to students of clicked course
        child3= root.child("Courses").child(courseName).child("Students");
        //reference to teacher of clicked course
        child2 = root.child("Courses").child(courseName);

      setTitle("Course "+courseName+"'s members");


      getSenderName(new FirebaseCallback2() {

          @Override

          public void onCallback(String sender) {

              senderName =sender;

          }
      });

        readDataTeacherName(new FirebaseCallback() {

         @Override
         public void onCallback(ArrayList<usersListPOJO> list) {

                users=list;

             readData(new FirebaseCallback() {
                 @Override
                 public void onCallback(ArrayList<usersListPOJO> list) {


                     for (int i = 0; i < list.size(); i++) {
                         users.add(list.get(i));
                     }


                        //  loop users list to remove loged in user

                     for (int i = 0; i < users.size(); i++) {
                         //remove student if it's the same loged in person
                         if (users.get(i).getUserId().equals(getUserId())) {

                             users.remove(i);
                         }
                         //remove teacher(course creator) if it's the same loged in person
                         if (users.get(i).getName().equals(senderName)) {

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
                             // i.putExtra("ReceiverEmail", users.get(position).getEmail());

                             i.putExtra("senderName", senderName);
                             AllUsersList.this.startActivity(i);
                         }
                     });



                 }
             });

         }
     });


    }




    private void readData(final FirebaseCallback firebaseCallback)
    {
       final ArrayList<usersListPOJO> myUsers=new ArrayList<>();
        //get students enrolled from DB
        child3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {


                   myUsers.add(new usersListPOJO(ds.child("userName").getValue().toString()
                            , ds.child("email").getValue().toString(), ds.child("userId").getValue().toString()));
                }
               firebaseCallback.onCallback(myUsers);
                //showing the data in the list

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void readDataTeacherName (final FirebaseCallback firebaseCallback)
    {final ArrayList<usersListPOJO> myUsers=new ArrayList<>();
        //get teacher name from DB
        child2.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                myUsers.add(new usersListPOJO(dataSnapshot.child("teacherName").getValue().toString(), "Course instructor", ""));
                firebaseCallback.onCallback(myUsers);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showList() {
        if (users.size()==0)
        {
            //Hide list and header and show text saying "no students in this course"
            noStudents=findViewById(R.id.noStudentsImage);
            noStudents.setVisibility(View.VISIBLE);
            noStudentsText=findViewById(R.id.noStudentsText);
            noStudentsText.setVisibility(View.VISIBLE);
            listView=findViewById(R.id.fullUsersList);
            listView.setVisibility(View.GONE);
            HeaderText=findViewById(R.id.textView);
            HeaderText.setVisibility(View.GONE);
            chatIcon=findViewById(R.id.chatIcon);
            chatIcon.setVisibility(View.GONE);

        }
        else {



            UserListAdapter myListAdapter = new UserListAdapter(this, users);

            // Get a reference to the ListView, and attach the adapter to the listView.
            listView = findViewById(R.id.fullUsersList);
            listView.setAdapter(myListAdapter);
        }
    }

    private interface FirebaseCallback {
        void onCallback(ArrayList<usersListPOJO> list);
    }

    private interface FirebaseCallback2 {
        void onCallback(String sender );
    }



    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void getSenderName(final FirebaseCallback2 firebaseCallback2)
    {
        child4.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot ds:dataSnapshot.getChildren())
                {


                    if (ds.child("userId").getValue().toString().equals(getUserId()))
                    {

                        String senderName=ds.child("userName").getValue().toString();
                        firebaseCallback2.onCallback(senderName);
                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

}
