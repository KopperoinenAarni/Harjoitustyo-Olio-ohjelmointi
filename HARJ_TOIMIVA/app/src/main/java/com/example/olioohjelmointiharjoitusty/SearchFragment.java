//Read me
//This class for searching citys

package com.example.olioohjelmointiharjoitusty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olioohjelmointiharjoitusty.history.SearchHistoryAdapter;
import com.example.olioohjelmointiharjoitusty.history.SearchHistoryStorage;

public class SearchFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private SearchHistoryAdapter historyAdapter;

    public SearchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        EditText cityNameInput = view.findViewById(R.id.cityNameInput);
        Button searchCity = view.findViewById(R.id.searchCity);
        RecyclerView recyclerView = view.findViewById(R.id.searchHistoryRecycler);
        TextView searchStatus = view.findViewById(R.id.searchStatus);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new SearchHistoryAdapter(SearchHistoryStorage.getInstance().getSearchHistory());
        recyclerView.setAdapter(historyAdapter);

        searchCity.setOnClickListener(v -> {
            String city = cityNameInput.getText().toString().trim();
            if (!city.isEmpty()) {

                // Validation of city name
                SearchValidity searchValidity = new SearchValidity();
                searchValidity.checkCityValidity(getContext(), city, searchStatus);

                sharedViewModel.setCityName(city);
                Toast.makeText(getContext(), "Tietoja haetaan...", Toast.LENGTH_SHORT).show();

                SearchHistoryStorage.getInstance().addSearchEntry(city);
                historyAdapter.updateData(SearchHistoryStorage.getInstance().getSearchHistory());

            } else {
                Toast.makeText(getContext(), "Syötä kunnan nimi", Toast.LENGTH_SHORT).show();
            }
        });

        // Click this for history items
        historyAdapter.setOnItemClickListener(city -> {
            cityNameInput.setText(city);
            sharedViewModel.setCityName(city);
            Toast.makeText(getContext(), "Tietoja haetaan: " + city, Toast.LENGTH_SHORT).show();

            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToTab(1);
            }
        });
    }
}
