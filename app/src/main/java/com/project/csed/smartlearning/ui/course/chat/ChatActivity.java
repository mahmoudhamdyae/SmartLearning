package com.project.csed.smartlearning.ui.course.chat;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.csed.smartlearning.ChatMessagesViewHolder;
import com.project.csed.smartlearning.R;
import com.project.csed.smartlearning.models.ChatMessage;

public class ChatActivity extends AppCompatActivity {
    RecyclerView messagelist;
    private FirebaseRecyclerAdapter<ChatMessage, ChatMessagesViewHolder> mFirebaseUsersAdapter;
    FirebaseDatabase database;
    DatabaseReference myRef;
    LinearLayoutManager mLinearLayoutManager;
    EditText input;
    String userType, name, Coursename,inputmessage;
    public static String usernamefromfirebase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // RecyclerView stuff
        messagelist = findViewById(R.id.list_of_messages);
        input = findViewById(R.id.input);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        messagelist.setLayoutManager(mLinearLayoutManager);

        //get the some Strings
        name = getIntent().getStringExtra("name");
        Coursename = getIntent().getStringExtra("coursename");
         userType = getIntent().getStringExtra("userType");
        //Set title
        setTitle(Coursename);
        // Firebase stuff
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Rooms").child(Coursename);

        // to get the user name
        Toast.makeText(this, "Hello " + name, Toast.LENGTH_SHORT).show();

        // send Button
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                inputmessage = input.getText().toString();
//                if message contains nothing or only spaces
                if (inputmessage.isEmpty() || inputmessage.equals(" ") || inputmessage.trim().length()==0)
                {
                    //done Nothing
                }else {
                    try {
                        myRef.push().setValue(new ChatMessage(input.getText().toString(), name));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("my tag" ,e.getMessage());
                        Toast.makeText(ChatActivity.this, "error :" +e.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                    // Clear the input
                    input.setText("");

                }
                int friendlyMessageCount = mFirebaseUsersAdapter.getItemCount();
                messagelist.scrollToPosition(friendlyMessageCount - 1);
            }
        });

        usernamefromfirebase=name;
        // call the Method that will diplayMessages
        displayChatMessages();

    }

    private void displayChatMessages() {
        final SnapshotParser<ChatMessage> parser = new SnapshotParser<ChatMessage>() {
            @Override
            public ChatMessage parseSnapshot(DataSnapshot dataSnapshot) {
                ChatMessage usersList = dataSnapshot.getValue(ChatMessage.class);
               /* if (usersList != null) {
                    usersList.setChatRoomId(dataSnapshot.getKey());
                }*/
                return usersList;
            }
        };
//        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference().child("Messages");
        FirebaseRecyclerOptions<ChatMessage> options =
                new FirebaseRecyclerOptions.Builder<ChatMessage>()
                        .setQuery(myRef, parser)
                        .build();
        mFirebaseUsersAdapter = new FirebaseRecyclerAdapter<ChatMessage, ChatMessagesViewHolder>(options) {
            @Override
            public ChatMessagesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message, viewGroup, false);
                return new ChatMessagesViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final ChatMessagesViewHolder viewHolder,
                                            int position,
                                            final ChatMessage chatMessage) {

                // message view for other users
                viewHolder.userName.setText(chatMessage.getMessageUser());
                viewHolder.message_text.setText(chatMessage.getMessageText());
                viewHolder.message_time.setText(chatMessage.getMessageTime() + "");

                // message view for logged in user
                viewHolder.userNamesender.setText(chatMessage.getMessageUser());
                viewHolder.message_textsender.setText(chatMessage.getMessageText());
                viewHolder.message_timesender.setText(chatMessage.getMessageTime() + "");

                // if iam the one who send the message take this View
                if (chatMessage.getMessageUser().endsWith(name)) {
                    viewHolder.userNamesender.setTextColor(getResources().getColor(android.R.color.holo_purple));
                    viewHolder.relativeLayout.setVisibility(View.GONE);
                    viewHolder.relativeLayoutsender.setVisibility(View.VISIBLE);
                }
                // if iam not the one who sent the message take this View
                else {
                    viewHolder.userName.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                    viewHolder.relativeLayoutsender.setVisibility(View.GONE);
                    viewHolder.relativeLayout.setVisibility(View.VISIBLE);
                }

            }
        };

        mFirebaseUsersAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseUsersAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    messagelist.scrollToPosition(positionStart);
                }
            }
        });

        messagelist.setAdapter(mFirebaseUsersAdapter);
        mFirebaseUsersAdapter.startListening();

    }
}