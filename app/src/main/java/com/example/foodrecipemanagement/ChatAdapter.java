package com.example.foodrecipemanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private final List<Question> questions;
    private final OnQuestionClickListener listener;

    public interface OnQuestionClickListener {
        void onQuestionClick(Question question);
    }

    public ChatAdapter(OnQuestionClickListener listener) {
        this.questions = new ArrayList<>();
        this.listener = listener;
    }

    public void updateData(List<Question> newQuestions) {
        this.questions.clear();
        this.questions.addAll(newQuestions);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.bind(question, listener);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewUserName;
        private final TextView textViewQuestion;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.textView_user_name);
            textViewQuestion = itemView.findViewById(R.id.textView_question);
        }

        public void bind(Question question, OnQuestionClickListener listener) {
            textViewUserName.setText(question.getUserName());
            textViewQuestion.setText(question.getContent());
            itemView.setOnClickListener(v -> listener.onQuestionClick(question));
        }
    }
}