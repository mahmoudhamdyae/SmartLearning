package com.project.csed.smartlearning;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class AllUsersList extends AppCompatActivity {
    DatabaseReference root= FirebaseDatabase.getInstance().getReference();
    DatabaseReference child1=root.child("Users").child(getUserId());
    DatabaseReference child2=root.child("Users");

    ArrayList<usersListPOJO> users = new ArrayList<>();
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users_list);

        getAllUsersUserNameAndEmail();



    }

    private String getUserId()
    {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        return currentFirebaseUser.getUid();
    }



    private void showList()
    {
        userListAdapter myListAdapter = new userListAdapter(this, users);

        // Get a reference to the ListView, and attach the adapter to the listView.
         listView = (ListView) findViewById(R.id.fullUsersList);
        listView.setAdapter(myListAdapter);
    }



    private void getReceiverUserAndEmail()
    {

    }

    private void getAllUsersUserNameAndEmail()
    {

        //get all users from DB to show them in list
        child2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {

                        users.add(new usersListPOJO(ds.child("userName").getValue().toString()
                                ,ds.child("email").getValue().toString(),ds.child("userId").getValue().toString()));


                    //  loop users list to remove loged in user
                    //TODO fix it
                    for (int i=0 ;i<users.size();i++)
                    {
                        if (users.get(i).getUserId()==getUserId())
                        {
                            users.remove(i);
                        }
                    }



                }

                //showing the data in the list
                showList();

                //open chat activity when user is selected from list
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent i=new Intent(AllUsersList.this,OneToOneChatActivity.class);

                        i.putExtra("ReceiverName",  users.get(position).getName());
                        i.putExtra("ReceiverEmail",  users.get(position).getEmail());
                        AllUsersList.this.startActivity(i);

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
