package com.project.csed.smartlearning;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class QuizAdapterForStudent extends RecyclerView.Adapter<QuizAdapterForStudent.QuizHolder> {

    private List<Quiz> quizList;
    private Context context;

    public QuizAdapterForStudent(List<Quiz> quizList, Context context) {
        this.quizList = quizList;
        this.context = context;
    }

    @NonNull
    @Override
    public QuizHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View quizRow = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.quiz_row_for_student,viewGroup,false);
        QuizHolder holder = new QuizHolder(quizRow);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull QuizHolder quizHolder, int i) {
        final Quiz quiz = quizList.get(i);
        quizHolder.quizNumber.setText(String.valueOf(quiz.getNumber()));
        quizHolder.quizDate.setText(quiz.getDate());
        quizHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo launch quiz questions to answer or view previous answers
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public class  QuizHolder extends RecyclerView.ViewHolder{
        TextView quizNumber, quizDate;
        LinearLayout linearLayout;

        public QuizHolder(@NonNull View itemView) {
            super(itemView);
            quizNumber = itemView.findViewById(R.id.quiz_name);
            quizDate = itemView.findViewById(R.id.quiz_date);
            linearLayout = itemView.findViewById(R.id.linearLayout);
        }
    }
}
