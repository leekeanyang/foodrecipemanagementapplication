package com.example.foodrecipemanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {
    private final List<Question> questions;
    private final List<List<Reply>> repliesList;
    private final OnQuestionDeleteListener deleteListener;
    private final OnAddReplyListener addReplyListener;
    private final OnEditReplyListener editReplyListener;
    private final OnDeleteReplyListener deleteReplyListener;
    private final OnEditQuestionListener editQuestionListener;
    private CommunityRepository communityRepo;

    public interface OnQuestionDeleteListener {
        void onQuestionDelete(Question question);
    }

    public interface OnAddReplyListener {
        void onAddReply(Question question);
    }

    public interface OnEditReplyListener {
        void onEditReply(Reply reply);
    }

    public interface OnDeleteReplyListener {
        void onDeleteReply(Reply reply);
    }

    public interface OnEditQuestionListener {
        void onEditQuestion(Question question);
    }

    public QuestionAdapter(CommunityRepository communityRepo, List<Question> questions,
                           OnQuestionDeleteListener deleteListener, OnAddReplyListener addReplyListener,
                           OnEditReplyListener editReplyListener, OnDeleteReplyListener deleteReplyListener,
                           OnEditQuestionListener editQuestionListener) {
        this.communityRepo = communityRepo;
        this.questions = new ArrayList<>(questions);
        this.repliesList = new ArrayList<>();
        for (Question question : questions) {
            repliesList.add(communityRepo.getReplies(question.getId()));
        }
        this.deleteListener = deleteListener;
        this.addReplyListener = addReplyListener;
        this.editReplyListener = editReplyListener;
        this.deleteReplyListener = deleteReplyListener;
        this.editQuestionListener = editQuestionListener;
    }

    public void updateData(List<Question> newQuestions) {
        this.questions.clear();
        this.repliesList.clear();
        this.questions.addAll(newQuestions);
        for (Question question : newQuestions) {
            repliesList.add(communityRepo.getReplies(question.getId()));
        }
        notifyDataSetChanged();
    }

    public void notifyRepliesChanged(int questionId) {
        int position = -1;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getId() == questionId) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            repliesList.set(position, communityRepo.getReplies(questionId));
            notifyItemChanged(position);
        }
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questions.get(position);
        List<Reply> replies = repliesList.get(position);
        holder.bind(question, replies, deleteListener, addReplyListener, editReplyListener, deleteReplyListener, editQuestionListener);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewUserName;
        private final TextView textViewQuestion;
        private final ImageButton editButton;
        private final Button deleteButton;
        private final TextView replyLabel;
        private final LinearLayout repliesContainer;
        private final Button addReplyButton;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserName = itemView.findViewById(R.id.textView_user_name);
            textViewQuestion = itemView.findViewById(R.id.textView_question);
            editButton = itemView.findViewById(R.id.button_edit_question);
            deleteButton = itemView.findViewById(R.id.button_delete_question);
            replyLabel = itemView.findViewById(R.id.textView_reply_label);
            repliesContainer = itemView.findViewById(R.id.replies_container);
            addReplyButton = itemView.findViewById(R.id.button_add_reply);
        }

        public void bind(Question question, List<Reply> replies, OnQuestionDeleteListener deleteListener,
                         OnAddReplyListener addReplyListener, OnEditReplyListener editReplyListener,
                         OnDeleteReplyListener deleteReplyListener, OnEditQuestionListener editQuestionListener) {
            textViewUserName.setText(question.getUserName());
            textViewQuestion.setText(question.getContent());
            editButton.setOnClickListener(v -> editQuestionListener.onEditQuestion(question));
            deleteButton.setOnClickListener(v -> deleteListener.onQuestionDelete(question));
            addReplyButton.setOnClickListener(v -> addReplyListener.onAddReply(question));

            repliesContainer.removeAllViews();
            if (replies.isEmpty()) {
                replyLabel.setVisibility(View.GONE);
            } else {
                replyLabel.setVisibility(View.VISIBLE);
                for (Reply reply : replies) {
                    View replyView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_reply, repliesContainer, false);
                    TextView replyUserName = replyView.findViewById(R.id.textView_reply_user_name);
                    TextView replyContent = replyView.findViewById(R.id.textView_reply_content);
                    ImageButton editButton = replyView.findViewById(R.id.button_edit_reply);
                    ImageButton deleteButton = replyView.findViewById(R.id.button_delete_reply);
                    replyUserName.setText(reply.getUserName());
                    replyContent.setText(reply.getContent());
                    editButton.setOnClickListener(v -> editReplyListener.onEditReply(reply));
                    deleteButton.setOnClickListener(v -> deleteReplyListener.onDeleteReply(reply));
                    repliesContainer.addView(replyView);
                }
            }
        }
    }
}