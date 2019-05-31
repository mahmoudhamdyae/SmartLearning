package com.project.csed.smartlearning;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class QuizAdapterForTeacher extends RecyclerView.Adapter<QuizAdapterForTeacher.QuizHolder> {

    private List<Quiz> quizList;
    private Context context;
    private String courseName;

    public QuizAdapterForTeacher(List<Quiz> quizList, Context context, String courseName) {
        this.quizList = quizList;
        this.context = context;
        this.courseName = courseName;
    }

    @NonNull
    @Override
    public QuizHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View quizRow = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.quiz_row_for_teacher,viewGroup,false);
        QuizHolder holder = new QuizHolder(quizRow);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull QuizAdapterForTeacher.QuizHolder quizHolder, int i) {
        final Quiz quiz = quizList.get(i);
        quizHolder.quizName.setText(String.valueOf(quiz.getNumber()));
        quizHolder.quizDate.setText(quiz.getDate());
        quizHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open quiz details activity
                Intent intent = new Intent(context, QuizDetailsActivity.class);
                intent.putExtra("quizDate", quiz.getDate());
                intent.putExtra("courseName", courseName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public class  QuizHolder extends RecyclerView.ViewHolder{
        TextView quizName, quizDate;
        ImageView delete;
        LinearLayout linearLayout;

        public QuizHolder(@NonNull View itemView) {
            super(itemView);
            quizName = itemView.findViewById(R.id.quiz_name);
            quizDate = itemView.findViewById(R.id.quiz_date);
            delete = itemView.findViewById(R.id.quiz_delete);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create an AlertDialog.Builder and set the message, and click listeners
                    // for the positive and negative buttons on the dialog.
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(R.string.quiz_delete_dialog_msg);
                    builder.setPositiveButton(R.string.quiz_delete_dialog_delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked the "Delete" button, so delete the Quiz.
                            deleteQuiz(getAdapterPosition());
                        }
                    });
                    builder.setNegativeButton(R.string.quiz_delete_dialog_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked the "Cancel" button, so dismiss the dialog
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        }
                    });
                    // Create and show the AlertDialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }
    }

    private void deleteQuiz(int position){
        //removes the row from UI
        final Quiz quiz1 = quizList.get(position);
        quizList.remove(position);
        notifyItemRemoved(position);

        // todo there is a problem here
        // Fix quizzes numbers
        final int numberOfDeletedQuiz = quiz1.getNumber();

        final DatabaseReference quizReference = FirebaseDatabase.getInstance().getReference().child("Quizzes").child(courseName);
//        quizReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
//                    Quiz quiz2 = dataSnapshot1.getValue(Quiz.class);
//
//                    int number = quiz2.getNumber();
//                    if (number > numberOfDeletedQuiz){
//                        quizReference.child(quiz2.getDate()).child("number").setValue(String.valueOf(number - 1));
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });

        // Remove the quiz from database
        quizReference.child(quiz1.getDate()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, R.string.quiz_deleted_successfully_toast, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
