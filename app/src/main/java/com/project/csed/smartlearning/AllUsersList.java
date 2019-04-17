package com.project.csed.smartlearning;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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
    DatabaseReference child2 = root.child("Users");
    private String senderName;

    ArrayList<usersListPOJO> users = new ArrayList<>();
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users_list);



     readData(new FirebaseCallback() {
         @Override
         public void onCallback(ArrayList<usersListPOJO> list) {

             //at this point all data is added to users list and ready to show
             showList();

             //  loop users list to remove loged in user
             for (int i = 0; i < users.size(); i++) {
                 if (users.get(i).getUserId().equals(getUserId())) {
                     //save sender name for later usage
                     senderName = users.get(i).getName();
                     users.remove(i);
                 }
             }






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
        child2.addValueEventListener(new ValueEventListener() {
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
        userListAdapter myListAdapter = new userListAdapter(this, users);

        // Get a reference to the ListView, and attach the adapter to the listView.
        listView = (ListView) findViewById(R.id.fullUsersList);
        listView.setAdapter(myListAdapter);
    }




    private interface FirebaseCallback {
        void onCallback(ArrayList<usersListPOJO> list);
    }

    private String getUserId() {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentFirebaseUser.getUid();
    }

}
