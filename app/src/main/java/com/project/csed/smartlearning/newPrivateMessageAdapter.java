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
import android.widget.RelativeLayout;
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
        View view=LayoutInflater.from(context).inflate( R.layout.message,viewGroup,false);
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
        public RelativeLayout logedInuserLayout,otherUserLayout;

        public PrivateMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            //view on left side for other person and view on right side for loged in user
            //view for other person
            privateMessage=itemView.findViewById(R.id.message_text);
            privateSender=itemView.findViewById(R.id.message_user);
            privateDate=itemView.findViewById(R.id.message_time);
            otherUserLayout=itemView.findViewById(R.id.messageid);

            //view for loged in user
            privateMessageRight=itemView.findViewById(R.id.message_textsender);
            privateSenderRight=itemView.findViewById(R.id.message_usersender);
            privateDateRight=itemView.findViewById(R.id.message_timesender);
            logedInuserLayout=itemView.findViewById(R.id.messageidsender);
            //test

        }

    }

}
