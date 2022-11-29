package com.project.csed.smartlearning.adapters;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.csed.smartlearning.R;
import com.project.csed.smartlearning.models.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuizStudentsAdapter extends RecyclerView.Adapter<QuizStudentsAdapter.QuizStudentsHolder> {
    private List<User> users;
    private Context context;

    public QuizStudentsAdapter (Context context, List<User> users){
        this.context = context;
        this.users = users;
    }
    @NonNull
    @Override
    public QuizStudentsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.addstudent_row_design,viewGroup,false);
        QuizStudentsHolder holder = new QuizStudentsHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final QuizStudentsHolder quizStudentsHolder, int position) {
        User user = users.get(position);
        quizStudentsHolder.StudentName.setText(user.getUserName());
        quizStudentsHolder.emailAddress.setText(user.getEmail());
        quizStudentsHolder.quizDegree.setText(user.getDegree());

        String userId = user.getUserId();
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference()
                .child("images/" + userId + ".jpg");
        mStorageRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        RequestOptions placeholderOption = new RequestOptions();
                        placeholderOption.placeholder(R.drawable.default_image);
                        Glide.with(context).setDefaultRequestOptions(placeholderOption).load(uri).into(quizStudentsHolder.profileImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class QuizStudentsHolder extends RecyclerView.ViewHolder{
        TextView StudentName,emailAddress, quizDegree;
        CheckBox SelectStudent;
        LinearLayout linearLayout;
        CircleImageView profileImage;

        public QuizStudentsHolder(@NonNull View itemView) {
            super(itemView);
            StudentName = itemView.findViewById(R.id.StudentName);
            emailAddress = itemView.findViewById(R.id.emailAddress);
            linearLayout = itemView.findViewById(R.id.AddStudentrow);
            profileImage = itemView.findViewById(R.id.profile_image);
            quizDegree = itemView.findViewById(R.id.quiz_degree);

            SelectStudent = itemView.findViewById(R.id.SelectStudent);
            SelectStudent.setVisibility(View.GONE);
        }
    }
}
