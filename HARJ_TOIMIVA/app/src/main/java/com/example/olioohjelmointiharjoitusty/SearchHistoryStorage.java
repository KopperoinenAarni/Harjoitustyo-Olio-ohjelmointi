//Read me
//this class is made for storing search history
package com.example.olioohjelmointiharjoitusty.history;

import java.util.ArrayList;
import java.util.List;


//Search history storage, search history adapter and search history viewholder have been done closely following examples from week 9 and 11.
public class SearchHistoryStorage {

    private static SearchHistoryStorage instance;
    private final List<String> searchHistory = new ArrayList<>();

    private SearchHistoryStorage() {
    }

    public static SearchHistoryStorage getInstance() {
        if (instance == null) {
            instance = new SearchHistoryStorage();
        }
        return instance;
    }

    public void addSearchEntry(String entry) {
        if (!searchHistory.contains(entry)) {
            searchHistory.add(0, entry);
        }
    }

    public List<String> getSearchHistory() {
        return new ArrayList<>(searchHistory);
    }
}