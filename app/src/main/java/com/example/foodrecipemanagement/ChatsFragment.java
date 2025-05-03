package com.example.foodrecipemanagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatsFragment extends Fragment {
    private RecyclerView recyclerQuestions;
    private QuestionAdapter questionAdapter;
    private TextView emptyView;
    private CommunityRepository communityRepo;
    private List<Question> allQuestions;
    private FirebaseFirestore db;
    private int communityId;
    private boolean isFirebaseUpdating = false;

    public static ChatsFragment newInstance(int communityId) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        args.putInt("community_id", communityId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        communityRepo = CommunityRepository.getInstance(requireContext());
        recyclerQuestions = view.findViewById(R.id.recycler_questions);
        emptyView = view.findViewById(R.id.empty_view);

        if (getArguments() != null) {
            communityId = getArguments().getInt("community_id");
        }

        setupRecyclerView();
        boolean useFirebase = true;
        setupFirebaseListener();

        return view;
    }

    private void setupRecyclerView() {
        recyclerQuestions.setLayoutManager(new LinearLayoutManager(requireContext()));
        allQuestions = new ArrayList<>();
        questionAdapter = new QuestionAdapter(communityRepo, allQuestions,
                question -> new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Question")
                        .setMessage("Are you sure you want to delete this question?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            Question deletedQuestion = new Question(question.getId(), question.getContent(), question.getUserName(), question.getCommunityId());
                            communityRepo.deleteQuestion(question.getId());
                            allQuestions.remove(question);
                            questionAdapter.updateData(allQuestions);
                            updateEmptyView(allQuestions);
                            Snackbar snackbar = Snackbar.make(recyclerQuestions, "Question deleted", Snackbar.LENGTH_LONG);
                            snackbar.setAction("Undo", v -> {
                                communityRepo.addQuestion(deletedQuestion);
                                allQuestions.add(deletedQuestion);
                                questionAdapter.updateData(allQuestions);
                                updateEmptyView(allQuestions);
                            });
                            snackbar.show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show(),
                question -> {
                    AddReplyDialog dialog = new AddReplyDialog();
                    dialog.setQuestionId(question.getId());
                    dialog.setOnReplyAddedListener(reply -> {
                        communityRepo.addReply(reply);
                        questionAdapter.notifyRepliesChanged(question.getId());
                    });
                    dialog.show(getParentFragmentManager(), "add_reply");
                },
                reply -> {
                    EditReplyDialog dialog = new EditReplyDialog();
                    dialog.setReply(reply);
                    dialog.setOnReplyEditedListener(editedReply -> {
                        communityRepo.updateReply(editedReply);
                        questionAdapter.notifyRepliesChanged(reply.getQuestionId());
                    });
                    dialog.show(getParentFragmentManager(), "edit_reply");
                },
                reply -> new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Reply")
                        .setMessage("Are you sure you want to delete this reply?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            Reply deletedReply = new Reply(reply.getId(), reply.getContent(), reply.getUserName(), reply.getQuestionId());
                            communityRepo.deleteReply(reply.getId());
                            questionAdapter.notifyRepliesChanged(reply.getQuestionId());
                            Snackbar snackbar = Snackbar.make(recyclerQuestions, "Reply deleted", Snackbar.LENGTH_LONG);
                            snackbar.setAction("Undo", v -> {
                                communityRepo.addReply(deletedReply);
                                questionAdapter.notifyRepliesChanged(deletedReply.getQuestionId());
                            });
                            snackbar.show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show(),
                question -> {
                    EditQuestionDialog dialog = new EditQuestionDialog();
                    dialog.setQuestion(question);
                    dialog.setOnQuestionEditedListener(editedQuestion -> {
                        communityRepo.updateQuestion(editedQuestion);
                        int index = allQuestions.indexOf(question);
                        if (index != -1) {
                            allQuestions.set(index, editedQuestion);
                            questionAdapter.updateData(allQuestions);
                        }
                    });
                    dialog.show(getParentFragmentManager(), "edit_question");
                });
        recyclerQuestions.setAdapter(questionAdapter);
        updateEmptyView(allQuestions);
    }

    private void loadQuestionsFromRepository() {
        allQuestions = communityRepo.getQuestions(communityId);
        questionAdapter.updateData(allQuestions);
        recyclerQuestions.invalidate();
        updateEmptyView(allQuestions);
    }

    private void setupFirebaseListener() {
        try {
            db = FirebaseFirestore.getInstance();
            db.collection("questions")
                    .whereEqualTo("communityId", communityId)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            loadQuestionsFromRepository();
                            return;
                        }
                        if (isFirebaseUpdating) {
                            return;
                        }
                        isFirebaseUpdating = true;
                        allQuestions.clear();
                        assert value != null;
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            int id = Integer.parseInt(doc.getId());
                            String content = doc.getString("content");
                            String userName = doc.getString("userName");
                            int commId = Objects.requireNonNull(doc.getLong("communityId")).intValue();
                            allQuestions.add(new Question(id, content, userName, commId));
                        }
                        questionAdapter.updateData(allQuestions);
                        recyclerQuestions.invalidate();
                        updateEmptyView(allQuestions);

                        for (Question question : allQuestions) {
                            db.collection("replies")
                                    .whereEqualTo("questionId", question.getId())
                                    .addSnapshotListener((replyValue, replyError) -> {
                                        if (replyError != null) {
                                            return;
                                        }
                                        questionAdapter.notifyRepliesChanged(question.getId());
                                    });
                        }
                        isFirebaseUpdating = false;
                    });
        } catch (Exception e) {
            loadQuestionsFromRepository();
        }
    }

    private void updateEmptyView(List<Question> questions) {
        if (questions.isEmpty()) {
            recyclerQuestions.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerQuestions.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    public void addNewQuestion(Question question) {
        communityRepo.addQuestion(question);
        allQuestions.add(question);
        questionAdapter.updateData(allQuestions);
        updateEmptyView(allQuestions);
    }
}