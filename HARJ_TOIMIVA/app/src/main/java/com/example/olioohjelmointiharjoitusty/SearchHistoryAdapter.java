//Read me
//THis class is made for showing search history
package com.example.olioohjelmointiharjoitusty.history;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olioohjelmointiharjoitusty.R;

import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryViewHolder> {

    private List<String> data;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String item);
    }

    public SearchHistoryAdapter(List<String> data) {
        this.data = data;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<String> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchHistoryViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.history_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHistoryViewHolder holder, int position) {
        String item = data.get(position);
        holder.bind(item);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
