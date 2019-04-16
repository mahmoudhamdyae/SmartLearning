package com.project.csed.smartlearning;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class OneToOneChatActivity extends AppCompatActivity {

    DatabaseReference root= FirebaseDatabase.getInstance().getReference();
    DatabaseReference child1=root.child("Users").child(getUserId());
    DatabaseReference child2,child3,child4;
    String senderName="medhat shalaby";
    String senderEmail,date;
    FloatingActionButton send;
    ListView listView;
    ArrayList<PrivateMessagePOJO> messageList=new ArrayList<>();
    TextInputEditText message;
    String ReceiverName;
    PrivateMessageAdapter myPrivateMessageAdapter;
    private  ChildEventListener childEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_to_one_chat);

        //Send Button
        send=findViewById(R.id.fab);
        // edit text where message been typed
        message=findViewById(R.id.privateMessageEditText);

        //initialize adapter and connect to list
        myPrivateMessageAdapter = new PrivateMessageAdapter(this, messageList);
        listView =findViewById(R.id.list_of_one_on_one_messages);
        listView.setAdapter(myPrivateMessageAdapter);

        //Retreive receiver data from previous activity
        Intent i=getIntent();
         ReceiverName=i.getStringExtra("ReceiverName");
        //String ReceiverEmail=i.getStringExtra("ReceiverEmail");
        setTitle("You are now chatting with "+ReceiverName);


        //Get sender data (the logged in user)
        GetsenderUserName(new FirebaseCallback() {
            @Override
            public void OnCallback(String interfaceSenderName) {
                senderName=interfaceSenderName;




                //loop through message need and add messages to the adapter
                child2=  root.child("PrivateMessage").child(senderName+"+"+ReceiverName);
                childEventListener=new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        {




                                PrivateMessagePOJO privateMessagePOJO = (dataSnapshot.getValue(PrivateMessagePOJO.class));
                                 myPrivateMessageAdapter.add(privateMessagePOJO);


                        }

                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };
                child2.addChildEventListener(childEventListener);


            }
        });




        //get right date
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        date = dateFormat.format(new Date());

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //make send button unclickable of no text in chat edittext
                if( message.length() == 0 || message.equals("") || message == null)
                {
                    //EditText is empty
                    send.setEnabled(false);
                    Toast.makeText(OneToOneChatActivity.this, "Type something to send", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //EditText is not empty
                    send.setEnabled(true);
                    //push message to DB
                    PrivateMessagePOJO messagePOJO=new PrivateMessagePOJO(date,message.getText().toString(),senderName);
                    root.child("PrivateMessage").child(senderName+"+"+ReceiverName).push().setValue(messagePOJO);
                    root.child("PrivateMessage").child(ReceiverName+"+"+senderName).push().setValue(messagePOJO);


                    message.setText("");
                    Toast.makeText(OneToOneChatActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                }

                send.setEnabled(true);



            }
        });



    }


    private void GetsenderUserName(final FirebaseCallback firebaseCallback)
    {
        child1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                senderName=dataSnapshot.child("userName").getValue().toString();
              //  senderEmail=dataSnapshot.child("email").getValue().toString();

                firebaseCallback.OnCallback(senderName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
    //because on data change executes after the return and returns null
    private interface FirebaseCallback {
    void OnCallback(String interfaceSenderName);
    }


    private String getUserId()
    {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        return currentFirebaseUser.getUid();
    }



}
