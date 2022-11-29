package com.project.csed.smartlearning.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.csed.smartlearning.ui.course.quiz.QuizAnswerActivity;
import com.project.csed.smartlearning.R;
import com.project.csed.smartlearning.models.Quiz;

import java.util.List;
import java.util.Objects;

public class QuizAdapterForStudent extends RecyclerView.Adapter<QuizAdapterForStudent.QuizHolder> {

    private final List<Quiz> quizList;
    private final Context context;
    private final String courseName;

    public QuizAdapterForStudent(List<Quiz> quizList, Context context, String courseName) {
        this.quizList = quizList;
        this.context = context;
        this.courseName = courseName;
    }

    @NonNull
    @Override
    public QuizHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View quizRow = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.quiz_row_for_student,viewGroup,false);
        return new QuizHolder(quizRow);
    }

    @Override
    public void onBindViewHolder(@NonNull final QuizHolder quizHolder, int i) {
        final Quiz quiz = quizList.get(i);
        quizHolder.quizNumber.setText(String.valueOf(quiz.getNumber()));
        quizHolder.quizDate.setText(quiz.getDate());

        // make quiz unclickable if the student already answered it
        quizHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Quizzes")
                        .child(courseName).child(quiz.getDate()).child("Students");

                ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                            Toast.makeText(context, R.string.you_already_solved_this_quiz_toast, Toast.LENGTH_SHORT).show();
                        else {
                            Intent intent = new Intent(context, QuizAnswerActivity.class);
                            intent.putExtra("quizDate", quiz.getDate());
                            intent.putExtra("courseName", courseName);
                            intent.putExtra("quizNumber", String.valueOf(quiz.getNumber()));
                            context.startActivity(intent);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

        // Find if this quiz is solved or not
        final String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Quizzes")
                .child(courseName).child(quiz.getDate()).child("Students");

        ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    quizHolder.solvedOrNot.setText(R.string.quiz_solved);
                    quizHolder.solvedOrNot.setTextColor(ContextCompat.getColor(context, R.color.text_black));
                    quizHolder.degree.setText(Objects.requireNonNull(dataSnapshot.getValue()).toString());
                    quizHolder.degree.setVisibility(View.VISIBLE);
                    quizHolder.degreeText.setVisibility(View.VISIBLE);
                    quizHolder.checkSign.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public static class  QuizHolder extends RecyclerView.ViewHolder{
        TextView quizNumber, quizDate, solvedOrNot, degree, degreeText;
        CardView cardView;
        ImageView checkSign;
        public QuizHolder(@NonNull View itemView) {
            super(itemView);
            quizNumber = itemView.findViewById(R.id.quiz_name);
            quizDate = itemView.findViewById(R.id.quiz_date);
            solvedOrNot = itemView.findViewById(R.id.solved_or_not);
            degree = itemView.findViewById(R.id.degree);
            degreeText = itemView.findViewById(R.id.degree_text);
            cardView = itemView.findViewById(R.id.cardView);
            checkSign=itemView.findViewById(R.id.checkImage);
        }
    }
}
