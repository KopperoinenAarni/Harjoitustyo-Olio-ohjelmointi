package com.example.olioohjelmointiharjoitusty.ShowData;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.example.olioohjelmointiharjoitusty.R;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class PopulationDataRetriever {

    private PopulationData populationData;
    private OnDataLoadedListener onDataLoadedListener;

    public interface OnDataLoadedListener {
        void onDataLoaded(PopulationData data);
    }
    public void setOnDataLoadedListener(OnDataLoadedListener listener) {
        this.onDataLoadedListener = listener;
    } // We had issues where quiz would crash because the data was not loaded to it at the correct time. ChatGPT helped us fix that issue with this onDataLoadedListener interface.

    // This retrieves the population and population change data from Tilastokeskus API
    // and updates the provided TextView (populationText) on the main thread.
    public void getData(final Context context,
                        final String municipality,
                        final TextView populationText) {

        new Thread(() -> {
            ObjectMapper objectMapper = new ObjectMapper();

            // This retrieves municipality codes
            JsonNode areas;
            try {
                areas = objectMapper.readTree(
                        new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/synt/statfin_synt_pxt_12dy.px")
                );
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            ArrayList<String> keys   = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();

            // This reads through the municipality codes
            for (JsonNode node : areas.get("variables").get(1).get("values")) {
                values.add(node.asText());
            }
            for (JsonNode node : areas.get("variables").get(1).get("valueTexts")) {
                keys.add(node.asText());
            }

            HashMap<String, String> municipalityCodes = new HashMap<>();
            for (int i = 0; i < keys.size(); i++) {
                municipalityCodes.put(keys.get(i), values.get(i));
            }
            String code = municipalityCodes.get(municipality);

            try {
                // This connects to the Tilastokeskus web
                URL url = new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/synt/statfin_synt_pxt_12dy.px");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                // This uses the R.raw.population_query to conduct its JSON query from Tilastokeskus
                JsonNode jsonInputString = objectMapper.readTree(
                        context.getResources().openRawResource(R.raw.population_query)
                );
                // This adds the municipality code to the "Alue" dimension
                ((ObjectNode) jsonInputString.get("query").get(0).get("selection"))
                        .putArray("values").add(code);

                byte[] input = objectMapper.writeValueAsBytes(jsonInputString);
                OutputStream os = con.getOutputStream();
                os.write(input);
                os.close();

                // This reads the API response
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
                br.close();

                // This reads through the response we got
                JsonNode municipalityData = objectMapper.readTree(response.toString());

                // This reads through the year codes from our year dimension
                ArrayList<String> years = new ArrayList<>();
                for (JsonNode node : municipalityData.get("dimension")
                        .get("Vuosi")
                        .get("category")
                        .get("label")) {
                    years.add(node.asText());
                }

                // This searches for the value table
                JsonNode valuesNode = municipalityData.get("value");
                int index = municipalityData.get("dimension").get("Tiedot").get("category").get("label").size();

                // This searches the data from the latest year
                if (!years.isEmpty()) {
                    int lastIndex = years.size() - 1;
                    int baseIndex = lastIndex * index;

                    int population = Integer.parseInt(valuesNode.get(baseIndex + 1).asText());

                    double percentChange = 0;
                    if (lastIndex > 0) {
                        int previousPopulation =
                                Integer.parseInt(valuesNode.get((lastIndex - 1) * index + 1).asText());
                        if (previousPopulation > 0) {
                            percentChange = (population - previousPopulation)
                                    / (double) previousPopulation * 100;
                            percentChange = Math.round(percentChange * 100.0) / 100.0;
                        }
                    }

                    // This creates a PopulationData instance
                    populationData = new PopulationData(population, percentChange);

                    String resultText = "Väestö: " + populationData.getPopulation()
                            + "\nVäestön muutos: " + populationData.getPopulationChangePercent() + "%";

                    // This refreshes the UI in our main thread
                    new Handler(Looper.getMainLooper()).post(() -> {
                        populationText.setText(resultText);
                        if (onDataLoadedListener != null) {
                            onDataLoadedListener.onDataLoaded(populationData);
                        }
                    });
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}