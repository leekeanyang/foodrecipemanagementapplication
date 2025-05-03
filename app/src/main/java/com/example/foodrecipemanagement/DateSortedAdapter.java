package com.example.foodrecipemanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodrecipemanagement.database.CartItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DateSortedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_MONTH_HEADER = 0;
    private static final int TYPE_DAY_GROUP    = 1;

    private final List<Object> displayList = new ArrayList<>();
    private final SimpleDateFormat monthFmt =
            new SimpleDateFormat("MMMM", Locale.getDefault());
    private final ItemActionListener listener;

    public DateSortedAdapter(ItemActionListener listener) {
        this.listener = listener;
    }

    /** Call this with your full list of CartItem to rebuild the view. */
    public void setData(List<CartItem> items) {
        displayList.clear();
        if (items != null) {
            Map<String, Map<Integer, List<CartItem>>> map = new LinkedHashMap<>();
            for (CartItem ci : items) {
                Date d = new Date(ci.getTargetDate());
                String month = monthFmt.format(d);
                int day = d.getDate();
                map.computeIfAbsent(month, m -> new LinkedHashMap<>());
                Map<Integer, List<CartItem>> days = map.get(month);
                days.computeIfAbsent(day, k -> new ArrayList<>());
                days.get(day).add(ci);
            }
            for (Map.Entry<String, Map<Integer, List<CartItem>>> monthEntry : map.entrySet()) {
                displayList.add(monthEntry.getKey());
                for (Map.Entry<Integer, List<CartItem>> dayEntry : monthEntry.getValue().entrySet()) {
                    displayList.add(new DayGroup(dayEntry.getKey(), dayEntry.getValue()));
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override public int getItemCount()          { return displayList.size(); }
    @Override public int getItemViewType(int pos) {
        return displayList.get(pos) instanceof String
                ? TYPE_MONTH_HEADER
                : TYPE_DAY_GROUP;
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType
    ) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_MONTH_HEADER) {
            return new MonthHolder(
                    inf.inflate(R.layout.item_month_header, parent, false));
        } else {
            return new DayGroupHolder(
                    inf.inflate(R.layout.item_day_group, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecyclerView.ViewHolder holder, int pos
    ) {
        if (getItemViewType(pos) == TYPE_MONTH_HEADER) {
            ((MonthHolder) holder).month
                    .setText((String) displayList.get(pos));
        } else {
            DayGroup dg = (DayGroup) displayList.get(pos);
            DayGroupHolder h = (DayGroupHolder) holder;
            h.day.setText(String.valueOf(dg.day));

            DayItemAdapter dayAdapter =
                    new DayItemAdapter(dg.items, listener);
            h.recycler.setLayoutManager(
                    new LinearLayoutManager(h.recycler.getContext()));
            h.recycler.setAdapter(dayAdapter);
        }
    }

    static class MonthHolder extends RecyclerView.ViewHolder {
        TextView month;
        MonthHolder(@NonNull View v) {
            super(v);
            month = v.findViewById(R.id.textMonthHeader);
        }
    }

    static class DayGroupHolder extends RecyclerView.ViewHolder {
        TextView day;
        RecyclerView recycler;
        DayGroupHolder(@NonNull View v) {
            super(v);
            day      = v.findViewById(R.id.textDay);
            recycler = v.findViewById(R.id.recyclerDayItems);
        }
    }

    private static class DayGroup {
        final int day;
        final List<CartItem> items;
        DayGroup(int day, List<CartItem> items) {
            this.day = day;
            this.items = items;
        }
    }

    /** Nested adapter to render each day's items with edit/tick buttons */
    private static class DayItemAdapter
            extends RecyclerView.Adapter<DayItemAdapter.ItemHolder> {

        private final List<CartItem> items;
        private final ItemActionListener listener;

        DayItemAdapter(List<CartItem> items, ItemActionListener listener) {
            this.items = items != null ? items : new ArrayList<>();
            this.listener = listener;
        }

        @NonNull @Override
        public ItemHolder onCreateViewHolder(
                @NonNull ViewGroup parent, int viewType
        ) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_cart_fullwidth, parent, false);
            return new ItemHolder(v);
        }

        @Override
        public void onBindViewHolder(
                @NonNull ItemHolder h, int position
        ) {
            CartItem ci = items.get(position);
            h.name.setText(ci.getName());
            h.amount.setText(ci.getAmount() + " " + ci.getMetric());
            if (ci.getNotes() != null && !ci.getNotes().isEmpty()) {
                h.note.setVisibility(View.VISIBLE);
                h.note.setText(ci.getNotes());
            } else {
                h.note.setVisibility(View.GONE);
            }

            // TINT THE TICK ICON
            int tickColor = ci.isPurchased()
                    ? ContextCompat.getColor(h.buttonTick.getContext(),
                    R.color.tick_selected)
                    : ContextCompat.getColor(h.buttonTick.getContext(),
                    R.color.tick_default);
            h.buttonTick.setColorFilter(tickColor);

            // CLICK HANDLERS
            h.buttonTick.setOnClickListener(v ->
                    listener.onTogglePurchased(ci));
            h.buttonEdit.setOnClickListener(v ->
                    listener.onEditItem(ci));

            // IMAGE
            if (ci.getImagePath() != null && !ci.getImagePath().isEmpty()) {
                Glide.with(h.image.getContext())
                        .load(ci.getImagePath())
                        .into(h.image);
            } else {
                h.image.setImageResource(R.drawable.ic_placeholder_image);
            }
        }

        @Override public int getItemCount() { return items.size(); }

        static class ItemHolder extends RecyclerView.ViewHolder {
            ImageView image;
            TextView name, amount, note;
            ImageButton buttonTick, buttonEdit;

            ItemHolder(@NonNull View v) {
                super(v);
                image      = v.findViewById(R.id.imageItem);
                name       = v.findViewById(R.id.textItemName);
                amount     = v.findViewById(R.id.textItemAmount);
                note       = v.findViewById(R.id.textItemNote);
                buttonTick = v.findViewById(R.id.buttonTick);
                buttonEdit = v.findViewById(R.id.buttonEdit);
            }
        }
    }
}
