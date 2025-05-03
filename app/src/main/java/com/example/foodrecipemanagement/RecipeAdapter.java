package com.example.foodrecipemanagement;

import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private final List<Recipe> recipes;
    private final CollectionRepository collectionRepo;
    private final OnCollectionButtonClickListener listener;

    public interface OnCollectionButtonClickListener {
        void onCollectionButtonClick(Recipe recipe, int position);
    }

    public RecipeAdapter(List<Recipe> recipes, CollectionRepository collectionRepo,
                         OnCollectionButtonClickListener listener) {
        this.recipes = recipes;
        this.collectionRepo = collectionRepo;
        this.listener = listener;
    }

    public void updateData(List<Recipe> newRecipes) {
        recipes.clear();
        recipes.addAll(newRecipes);
        notifyDataSetChanged();
        Log.d("RECIPE_ADAPTER", "Updated with " + newRecipes.size() + " items");
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        boolean isSaved = collectionRepo.isRecipeSaved(recipe);
        holder.btnCollection.setImageResource(isSaved ?
                R.drawable.ic_star_filled : R.drawable.ic_star_outline);

        holder.btnCollection.setOnClickListener(v -> {
            if (collectionRepo.isRecipeSaved(recipe)) {
                collectionRepo.removeRecipe(recipe);
            } else {
                collectionRepo.saveRecipe(recipe);
            }
            notifyItemChanged(position);
        });

        holder.recipeName.setText(recipe.getTitle());

        if (recipe.getSummary() != null && !recipe.getSummary().isEmpty()) {
            holder.recipeSummary.setText(Html.fromHtml(recipe.getSummary()));
            holder.recipeSummary.setVisibility(View.VISIBLE);
        } else {
            holder.recipeSummary.setVisibility(View.GONE);
        }

        Glide.with(holder.itemView.getContext())
                .load(recipe.getImage())
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error_image)
                .into(holder.recipeImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RecipeDetailActivity.class);

            if (recipe.isUserCreated()) {
                intent.putExtra("recipe", recipe);
            } else {
                intent.putExtra("recipe_id", recipe.getId());
            }

            v.getContext().startActivity(intent);
        });

        if (recipe.getImage().startsWith("content://") || recipe.getImage().startsWith("file://")) {
            holder.recipeType.setVisibility(View.VISIBLE);
        } else {
            holder.recipeType.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        TextView recipeName;
        TextView recipeSummary;
        ImageButton btnCollection;
        TextView recipeType;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            recipeName = itemView.findViewById(R.id.recipe_name);
            recipeSummary = itemView.findViewById(R.id.recipe_summary);
            btnCollection = itemView.findViewById(R.id.btn_collection);
            recipeType = itemView.findViewById(R.id.recipe_type);
        }
    }
}