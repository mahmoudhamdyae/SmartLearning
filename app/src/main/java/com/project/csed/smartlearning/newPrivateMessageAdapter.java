package com.project.csed.smartlearning;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class newPrivateMessageAdapter extends RecyclerView.Adapter<newPrivateMessageAdapter.PrivateMessageViewHolder> {

    private Context context;
    private ArrayList<PrivateMessagePOJO> privateMessageList =new ArrayList<>();
    private static final String TAG = "newPrivateMessageAdapte";

    public newPrivateMessageAdapter(Context context, ArrayList<PrivateMessagePOJO> privateMessageList) {
        this.context = context;
        this.privateMessageList = privateMessageList;
    }

    @NonNull
    @Override
    public PrivateMessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(context).inflate( R.layout.row_design_of_private_message,viewGroup,false);
        PrivateMessageViewHolder privateMessageViewHolder=new PrivateMessageViewHolder(view);
        return privateMessageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PrivateMessageViewHolder privateMessageViewHolder, int i) {
        privateMessageList.get(i);

        privateMessageViewHolder.privateMessage.setText(  privateMessageList.get(i).getMessage());
        privateMessageViewHolder.privateSender.setText(  privateMessageList.get(i).getSenderName());
        privateMessageViewHolder.privateDate.setText(  privateMessageList.get(i).getDate());

    }

    @Override
    public int getItemCount() {
        return privateMessageList.size();
    }

    public class    PrivateMessageViewHolder extends RecyclerView.ViewHolder{
        public TextView privateSender,privateMessage,privateDate;
        public TextView privateSenderRight,privateMessageRight,privateDateRight;
        public LinearLayout logedInuserLayout,otherUserLayout;

        public PrivateMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            //view on left side for other person and view on right side for loged in user
            //view for other person
            privateMessage=itemView.findViewById(R.id.private_chat_message);
            privateSender=itemView.findViewById(R.id.private_chat_sender);
            privateDate=itemView.findViewById(R.id.private_chat_time);
            otherUserLayout=itemView.findViewById(R.id.otherUserLinearLayour);

            //view for loged in user
            privateMessageRight=itemView.findViewById(R.id.private_chat_message_right);
            privateSenderRight=itemView.findViewById(R.id.private_chat_sender_right);
            privateDateRight=itemView.findViewById(R.id.private_chat_time_right);
            logedInuserLayout=itemView.findViewById(R.id.logedInUserLinearLayour);
            //test

        }

    }

}
