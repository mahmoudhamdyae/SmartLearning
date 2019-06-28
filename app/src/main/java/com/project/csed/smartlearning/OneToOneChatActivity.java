package com.project.csed.smartlearning;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OneToOneChatActivity extends AppCompatActivity {

    private static final String TAG = "saxx";
    DatabaseReference root= FirebaseDatabase.getInstance().getReference();
    DatabaseReference child2;
     String senderName;
    String senderEmail,date;
    FloatingActionButton send;
    ArrayList<PrivateMessagePOJO> messageList=new ArrayList<>();
    TextInputEditText message;
    String ReceiverName;
    private  ChildEventListener childEventListener;
     PrivateMessageAdapter adapter;
    private RecyclerView privateMessageRecyclerView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_to_one_chat);


        //get right date
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy hh:mm");
        date = dateFormat.format(new Date());

        //Send Button
        send=findViewById(R.id.fab);
        // edit text where message been typed
        message=findViewById(R.id.privateMessageEditText);



        //Retreive receiver data from previous activity
        Intent i=getIntent();
         ReceiverName=i.getStringExtra("ReceiverName");
        senderName=i.getStringExtra("senderName");

        initRecyclerView();

        Log.i(TAG,senderName);

        //String ReceiverEmail=i.getStringExtra("ReceiverEmail");
        setTitle("You are now chatting with "+ReceiverName);


        //Get sender data (the logged in user)





                //loop through message need and add messages to the adapter
                child2=  root.child("PrivateMessage").child(senderName+"+"+ReceiverName);
                childEventListener=new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        {
                                messageList.add(dataSnapshot.getValue(PrivateMessagePOJO.class));
                                 adapter.notifyDataSetChanged();
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


            //save and show message when send is pressed
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //make send button unclickable of no text in chat edittext
                if( message.length() == 0 || message.equals("") || message == null)
                {
                    //EditText is empty
                    send.setEnabled(false);
                    Toast.makeText(OneToOneChatActivity.this, R.string.type_something_to_send_toast, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //EditText is not empty
                    send.setEnabled(true);
                    //push message to DB
                    PrivateMessagePOJO messagePOJO=new PrivateMessagePOJO(senderName,message.getText().toString(),date);
                    root.child("PrivateMessage").child(senderName+"+"+ReceiverName).push().setValue(messagePOJO);
                    root.child("PrivateMessage").child(ReceiverName+"+"+senderName).push().setValue(messagePOJO);


                    message.setText("");
                    Toast.makeText(OneToOneChatActivity.this, R.string.message_sent_toast, Toast.LENGTH_SHORT).show();
                }

                send.setEnabled(true);
            }
        });

            }

    private void initRecyclerView() {
        //initialize adapter and connect to RecyclerView
        privateMessageRecyclerView = findViewById(R.id.one_to_one_chat_recyclerView);

        //override onBindViewHolder to  change view color depending on sender and receiver
        adapter=new PrivateMessageAdapter(getApplicationContext(),messageList){
            @Override
            public void onBindViewHolder(@NonNull PrivateMessageViewHolder privateMessageViewHolder, int i) {
                messageList.get(i);

                //if the sender of message is the logged in user make the view white and else blue
            if (  messageList.get(i).senderName.equals(senderName))
            {
              // privateMessageViewHolder.itemView.setBackgroundColor(Color.parseColor("#f5f2d0"));
                //
               // privateMessageViewHolder.privateSenderRight.setTextColor(getResources().getColor(android.R.color.holo_purple));
                privateMessageViewHolder.otherUserLayout.setVisibility(View.GONE);
                privateMessageViewHolder.logedInuserLayout.setVisibility(View.VISIBLE);
                //
            }
            else
            {
               // privateMessageViewHolder.itemView.setBackgroundColor(Color.parseColor("#8cb8ff"));
             //   privateMessageViewHolder.privateSender.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                privateMessageViewHolder.otherUserLayout.setVisibility(View.VISIBLE);
                privateMessageViewHolder.logedInuserLayout.setVisibility(View.GONE);
            }
                privateMessageViewHolder.privateMessage.setText(  messageList.get(i).getMessage());
                privateMessageViewHolder.privateSender.setText(  messageList.get(i).getSenderName());
                privateMessageViewHolder.privateDate.setText(  messageList.get(i).getDate());
                //
                privateMessageViewHolder.privateMessageRight.setText(  messageList.get(i).getMessage());
                privateMessageViewHolder.privateSenderRight.setText(  messageList.get(i).getSenderName());
                privateMessageViewHolder.privateDateRight.setText(  messageList.get(i).getDate());
                //

            }
        };

        //test
        privateMessageRecyclerView.setAdapter(adapter);
        privateMessageRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }
}
