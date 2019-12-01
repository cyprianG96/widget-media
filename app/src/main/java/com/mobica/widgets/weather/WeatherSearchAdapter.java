package com.mobica.widgets.weather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.mobica.widgets.R;

import java.util.ArrayList;

class WeatherSearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<WeatherSearchItem> searchItems = new ArrayList<>();
    private int selectedIndex = -1;

    void updateData(ArrayList<WeatherSearchItem> searchItems) {
        this.searchItems = searchItems;
        notifyDataSetChanged();
    }

    public WeatherSearchItem getSelected() {
        if (0 <= selectedIndex && selectedIndex < searchItems.size()) {
            return searchItems.get(selectedIndex);
        } else {
            return null;
        }
    }

    public void clearData() {
        searchItems.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WeatherSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WeatherSearchViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_search_weather_location, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final WeatherSearchItem item = searchItems.get(position);
        WeatherSearchViewHolder viewHolder = (WeatherSearchViewHolder) holder;
        viewHolder.text.setText(item.location);
        if (selectedIndex == position) {
            viewHolder.background.setBackgroundResource(R.drawable.location_row_background);
        } else {
            viewHolder.background.setBackground(null);
        }
        viewHolder.background.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (0 <= selectedIndex && selectedIndex < searchItems.size()) {
                    notifyItemChanged(selectedIndex);
                }
                if (selectedIndex != position) {
                    selectedIndex = position;
                    notifyItemChanged(position);
                } else {
                    selectedIndex = -1;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    static class WeatherSearchViewHolder extends RecyclerView.ViewHolder {
        View background;
        AppCompatTextView text;
        WeatherSearchViewHolder(@NonNull View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.background);
            text = itemView.findViewById(R.id.searchWeatherName);
        }
    }
}
