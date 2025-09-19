package com.example.olioohjelmointiharjoitusty.ShowData;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class EmploymentRateDataRetriever {

    // This retrieves the employment rate data from Tilastokeskus API
    // and updates the provided TextView (employmentRatePercentageText) on the main thread. All three data retriever classes have been done closely following
    // the materials of week 10.
    public void getData(final Context context, final String municipality, final TextView employmentRatePercentageText) {
        new Thread(() -> {
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode metadata = objectMapper.readTree(
                        new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/tyokay/statfin_tyokay_pxt_115x.px")
                );

                JsonNode variables = metadata.get("variables");
                JsonNode alueVariable = null;

                for (JsonNode variable : variables) {
                    if (variable.get("code").asText().equals("Alue")) {
                        alueVariable = variable;
                        break;
                    }
                }

                if (alueVariable == null) {
                    return;
                }


                //This creates the key and value pairs that we use to find correct data

                ArrayList<String> keys = new ArrayList<>();
                ArrayList<String> values = new ArrayList<>();

                for (JsonNode name : alueVariable.get("valueTexts")) {
                    keys.add(name.asText());
                }
                for (JsonNode code : alueVariable.get("values")) {
                    values.add(code.asText());
                }

                HashMap<String, String> municipalityCodes = new HashMap<>();
                for (int i = 0; i < keys.size(); i++) {
                    municipalityCodes.put(keys.get(i).trim(), values.get(i));
                }

                String code = municipalityCodes.get(municipality.trim());
                if (code == null) {
                    for (String key : municipalityCodes.keySet()) {
                        if (key.equalsIgnoreCase(municipality.trim())) {
                            code = municipalityCodes.get(key);
                            break;
                        }
                    }
                }

                if (code == null) {
                    return;
                }

                // This creates our JSON query to fetch employment rate
                ObjectNode queryRoot = objectMapper.createObjectNode();
                ArrayNode queryArray = objectMapper.createArrayNode();

                ObjectNode tiedot = objectMapper.createObjectNode();
                tiedot.put("code", "Tiedot");
                ObjectNode selectionTiedot = objectMapper.createObjectNode();
                selectionTiedot.put("filter", "item");
                selectionTiedot.set("values", objectMapper.createArrayNode().add("tyollisyysaste"));
                tiedot.set("selection", selectionTiedot);
                queryArray.add(tiedot);

                ObjectNode alue = objectMapper.createObjectNode();
                alue.put("code", "Alue");
                ObjectNode selectionAlue = objectMapper.createObjectNode();
                selectionAlue.put("filter", "item");
                selectionAlue.set("values", objectMapper.createArrayNode().add(code));
                alue.set("selection", selectionAlue);
                queryArray.add(alue);

                ObjectNode vuosi = objectMapper.createObjectNode();
                vuosi.put("code", "Vuosi");
                ObjectNode selectionVuosi = objectMapper.createObjectNode();
                selectionVuosi.put("filter", "top");
                selectionVuosi.set("values", objectMapper.createArrayNode().add("1"));
                vuosi.set("selection", selectionVuosi);
                queryArray.add(vuosi);

                queryRoot.set("query", queryArray);
                queryRoot.set("response", objectMapper.createObjectNode().put("format", "json-stat2"));

                // This connects to the Tilastokeskus web
                URL apiUrl = new URL("https://pxdata.stat.fi:443/PxWeb/api/v1/fi/StatFin/tyokay/statfin_tyokay_pxt_115x.px");
                HttpURLConnection con = (HttpURLConnection) apiUrl.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json; utf-8");
                con.setRequestProperty("Accept", "application/json");
                con.setDoOutput(true);

                byte[] input = objectMapper.writeValueAsBytes(queryRoot);
                OutputStream os = con.getOutputStream();
                os.write(input);
                os.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
                br.close();

                JsonNode responseJson = objectMapper.readTree(response.toString());
                JsonNode valueNode = responseJson.get("value");

                if (valueNode == null || valueNode.size() == 0) {
                    return;
                }

                double employmentRatePercentage = valueNode.get(0).asDouble();

                // This creates an EmploymentRateData instance.
                EmploymentRateData employmentRateData = new EmploymentRateData(0.0);
                employmentRateData.setEmploymentRatePercentage(employmentRatePercentage);

                String resultText = employmentRateData.getEmploymentRatePercentage() + " %";

                // This refreshes the UI in our main thread.
                new Handler(Looper.getMainLooper()).post(() -> {
                    employmentRatePercentageText.setText(resultText);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}