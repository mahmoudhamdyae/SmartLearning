package com.project.csed.smartlearning.adapters;

import android.app.Activity;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.csed.smartlearning.R;
import com.project.csed.smartlearning.models.usersListPOJO;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends ArrayAdapter<usersListPOJO> {



    public UserListAdapter(Activity context, ArrayList<usersListPOJO> userList) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, userList);
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
                    R.layout.row_design_of_users_list, parent, false);
        }



        // Get the {@link AndroidFlavor} object located at this position in the list
        final usersListPOJO currentUser = getItem(position);

        // Find the TextView in the row_design_of_users_list.xml layout with the ID version_name
        TextView nameTextView =  listItemView.findViewById(R.id.textView100);
        // Get the version name from the current AndroidFlavor object and
        // set this text on the name TextView
        nameTextView.setText(currentUser.getName());

        // Find the TextView in the list_item.xml layout with the ID version_number
        TextView emailTextView =  listItemView.findViewById(R.id.textView101);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        emailTextView.setText(currentUser.getEmail());

        // Find the ImageView in the list_item.xml layout with the ID list_item_icon
        final CircleImageView profileImage = listItemView.findViewById(R.id.profile_image);
        // Get the image resource ID from the current AndroidFlavor object and
        // set the image to profileImage
        Thread mainThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String userId = currentUser.getUserId();
                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference()
                        .child("images/" + userId + ".jpg");
                mStorageRef.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                RequestOptions placeholderOption = new RequestOptions();
                                Glide.with(getContext()).setDefaultRequestOptions(placeholderOption).load(uri).into(profileImage);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
            }
        });
        mainThread.start();

        // Return the whole list item layout (containing 2 TextViews and an ImageView)
        // so that it can be shown in the ListView
        return listItemView;
    }

}
