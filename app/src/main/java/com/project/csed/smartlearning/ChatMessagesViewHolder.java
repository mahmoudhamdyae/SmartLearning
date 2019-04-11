package com.project.csed.smartlearning;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ChatMessagesViewHolder extends RecyclerView.ViewHolder {
    public TextView userName, message_text, message_time;
    public RelativeLayout relativeLayout;

    public ChatMessagesViewHolder(@NonNull View itemView) {
        super(itemView);
        userName = itemView.findViewById(R.id.message_user);
        message_text = itemView.findViewById(R.id.message_text);
        message_time = itemView.findViewById(R.id.message_time);
        relativeLayout = itemView.findViewById(R.id.messageid);
    }
}