package com.example.foodrecipemanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {
    private final List<Community> communities;
    private final OnCommunityClickListener clickListener;
    private final OnCommunityEditListener editListener;
    private final OnCommunityDeleteListener deleteListener;

    public interface OnCommunityClickListener {
        void onCommunityClick(Community community);
    }

    public interface OnCommunityEditListener {
        void onCommunityEdit(Community community);
    }

    public interface OnCommunityDeleteListener {
        void onCommunityDelete(Community community);
    }

    public CommunityAdapter(List<Community> communities, OnCommunityClickListener clickListener,
                            OnCommunityEditListener editListener, OnCommunityDeleteListener deleteListener) {
        this.communities = new ArrayList<>(communities);
        this.clickListener = clickListener;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    public void updateData(List<Community> newCommunities) {
        this.communities.clear();
        this.communities.addAll(newCommunities);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community, parent, false);
        return new CommunityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        Community community = communities.get(position);
        holder.bind(community, clickListener, editListener, deleteListener);
    }

    @Override
    public int getItemCount() {
        return communities.size();
    }

    static class CommunityViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final TextView textViewDescription;
        private final TextView textViewDetails;
        private final ImageButton editButton;
        private final ImageButton deleteButton;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textView_community_name);
            textViewDescription = itemView.findViewById(R.id.textView_community_description);
            textViewDetails = itemView.findViewById(R.id.textView_community_details);
            editButton = itemView.findViewById(R.id.button_edit_community);
            deleteButton = itemView.findViewById(R.id.button_delete_community);
        }

        public void bind(Community community, OnCommunityClickListener clickListener,
                         OnCommunityEditListener editListener, OnCommunityDeleteListener deleteListener) {
            textViewName.setText(community.getName());
            textViewDescription.setText(community.getDescription());
            textViewDetails.setText(community.getRecipeCount() + " Recipes | " + community.getQuestionCount() + " Questions");
            itemView.setOnClickListener(v -> clickListener.onCommunityClick(community));
            editButton.setOnClickListener(v -> editListener.onCommunityEdit(community));
            deleteButton.setOnClickListener(v -> deleteListener.onCommunityDelete(community));
        }
    }
}