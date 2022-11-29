package com.project.csed.smartlearning.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.project.csed.smartlearning.ItemClickListener;
import com.project.csed.smartlearning.R;
import com.project.csed.smartlearning.models.User;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddStudentAdapter extends RecyclerView.Adapter<AddStudentAdapter.AddStudentHolder> {
    private List<User> StudentList;
    private Context context;
    public ArrayList<User> CheckStudentList =new ArrayList<>();
    public AddStudentAdapter(List<User> StudentList, Context context) {
        this.StudentList = StudentList;
        this.context = context;

    }

    @NonNull
    @Override
    public AddStudentHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View studentRow = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.addstudent_row_design, viewGroup, false);
        AddStudentHolder studentHolder = new AddStudentHolder(studentRow);
        return studentHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final AddStudentHolder studentHolder, int position) {
        final User user = StudentList.get(position);
        studentHolder.StudentName.setText(user.getUserName());
        studentHolder.emailAddress.setText(user.getEmail());

        Thread mainThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String userId = user.getUserId();
                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference()
                        .child("images/" + userId + ".jpg");
                mStorageRef.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                RequestOptions placeholderOption = new RequestOptions();
                                Glide.with(context).setDefaultRequestOptions(placeholderOption).load(uri).into(studentHolder.profileImage);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
            }
        });
        mainThread.start();

        studentHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(studentHolder.SelectStudent.isChecked())
                {
                    CheckStudentList.remove(user);
                    studentHolder.SelectStudent.setChecked(false);
                }
                else
                {
                    CheckStudentList.add(user);
                    studentHolder.SelectStudent.setChecked(true);
                }
            }
        });
        studentHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                final CheckBox chk = (CheckBox) v;
                if(chk.isChecked())
                {
                    CheckStudentList.add(StudentList.get(pos));
                }
                else
                {
                    CheckStudentList.remove(StudentList.get(pos));
                }
            }
        });
    }

    @Override
    public int getItemCount() { return StudentList.size();}
    public class AddStudentHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView StudentName,emailAddress, quizDegree, degreeText;
        CheckBox SelectStudent;
        LinearLayout linearLayout;
        ItemClickListener itemClickListener;
        CircleImageView profileImage;

        public AddStudentHolder(@NonNull View itemView) {
            super(itemView);
            StudentName = itemView.findViewById(R.id.StudentName);
            emailAddress = itemView.findViewById(R.id.emailAddress);
            SelectStudent = itemView.findViewById(R.id.SelectStudent);
            linearLayout = itemView.findViewById(R.id.AddStudentrow);
            profileImage = itemView.findViewById(R.id.profile_image);

            degreeText = itemView.findViewById(R.id.degree_text);
            degreeText.setVisibility(View.GONE);

            quizDegree = itemView.findViewById(R.id.quiz_degree);
            quizDegree.setVisibility(View.GONE);

            SelectStudent.setOnClickListener(this);
        }
        public void setItemClickListener(ItemClickListener ic)
        {
            this.itemClickListener= ic;
        }
        @Override
        public void onClick(View v)
        {
        this.itemClickListener.onItemClick(v,getLayoutPosition());
        }
    }
}

