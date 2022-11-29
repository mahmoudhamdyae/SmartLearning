package com.project.csed.smartlearning;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChatMessagesViewHolder extends RecyclerView.ViewHolder {
    public TextView userName, message_text, message_time;
    public RelativeLayout relativeLayout;
    public TextView userNamesender, message_textsender, message_timesender;
    public RelativeLayout relativeLayoutsender;
    String name;

    public ChatMessagesViewHolder(@NonNull View itemView) {
        super(itemView);



        // for other people holders
        userName = itemView.findViewById(R.id.message_user);
        message_text = itemView.findViewById(R.id.message_text);
        message_time = itemView.findViewById(R.id.message_time);
        relativeLayout = itemView.findViewById(R.id.messageid);


        //for me
        userNamesender = itemView.findViewById(R.id.message_usersender);
        message_textsender = itemView.findViewById(R.id.message_textsender);
        message_timesender = itemView.findViewById(R.id.message_timesender);
        relativeLayoutsender = itemView.findViewById(R.id.messageidsender);
    }
}