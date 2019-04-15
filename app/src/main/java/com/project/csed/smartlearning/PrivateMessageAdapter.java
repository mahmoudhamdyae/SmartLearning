package com.project.csed.smartlearning;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PrivateMessageAdapter extends ArrayAdapter<PrivateMessagePOJO> {



    public PrivateMessageAdapter(Activity context, ArrayList<PrivateMessagePOJO> messageList) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, messageList);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the list of data that should be displayed in the
     *                 list item view.
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.row_design_of_private_message, parent, false);



        }

        // Get the {@link AndroidFlavor} object located at this position in the list
        PrivateMessagePOJO currentMessage = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView senderTextView =  listItemView.findViewById(R.id.private_chat_sender);
        // Get the version name from the current AndroidFlavor object and
        // set this text on the name TextView
        senderTextView.setText(currentMessage.getSenderName());

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView messageTextView =  listItemView.findViewById(R.id.private_chat_message);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        messageTextView.setText(currentMessage.getMessage());


        TextView dateTextView =  listItemView.findViewById(R.id.private_chat_time);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        dateTextView.setText(currentMessage.getDate());

//        // Find the ImageView in the list_item.xml layout with the ID list_item_icon

        // Return the whole list item layout (containing 2 TextViews and an ImageView)
        // so that it can be shown in the ListView
        return listItemView;
    }

}
