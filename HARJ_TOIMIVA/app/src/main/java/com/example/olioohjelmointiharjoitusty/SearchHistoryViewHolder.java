//Read this!
//this class is made for showing search history


package com.example.olioohjelmointiharjoitusty.history;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.olioohjelmointiharjoitusty.R;

public class SearchHistoryViewHolder extends RecyclerView.ViewHolder {

    private final TextView textView;

    public SearchHistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.searchHistoryText);
    }

    public void bind(String text) {
        textView.setText(text);
    }
}
