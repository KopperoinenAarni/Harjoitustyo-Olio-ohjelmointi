//Read me ;)
//this is the code for comparing cities
package com.example.olioohjelmointiharjoitusty.comparison;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.olioohjelmointiharjoitusty.R;
import com.example.olioohjelmointiharjoitusty.ShowData.EmploymentRateDataRetriever;
import com.example.olioohjelmointiharjoitusty.ShowData.PopulationDataRetriever;
import com.example.olioohjelmointiharjoitusty.ShowData.WeatherParser;
import com.example.olioohjelmointiharjoitusty.ShowData.WeatherRepository;
import com.example.olioohjelmointiharjoitusty.ShowData.WorkSelfSufficiencyDataRetriever;

public class CompareFragment extends Fragment {

    private EditText cityOneInput, cityTwoInput;
    private Button compareCitiesButton;

    private TextView firstCityName, firstCityTemperature, firstCityHumidity, firstCityWind, firstCityPopulation, firstCityEmploymentRate, firstCityWorkSelfSufficiency;
    private TextView secondCityName, secondCityTemperature, secondCityHumidity, secondCityWind, secondCityPopulation, secondCityEmploymentRate, secondCityWorkSelfSufficiency;

    private PopulationDataRetriever populationDataRetriever;
    private EmploymentRateDataRetriever employmentRateDataRetriever;
    private WorkSelfSufficiencyDataRetriever workSelfSufficiencyDataRetriever;
    private WeatherRepository weatherRepository;

    public CompareFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compare, container, false);

        cityOneInput = view.findViewById(R.id.cityOneInput);
        cityTwoInput = view.findViewById(R.id.cityTwoInput);
        compareCitiesButton = view.findViewById(R.id.compareCitiesButton);

        firstCityName = view.findViewById(R.id.cityName);
        firstCityTemperature = view.findViewById(R.id.temperature);
        firstCityHumidity = view.findViewById(R.id.humidity);
        firstCityWind = view.findViewById(R.id.windText);
        firstCityPopulation = view.findViewById(R.id.populationText);
        firstCityEmploymentRate = view.findViewById(R.id.employmentRateText);
        firstCityWorkSelfSufficiency = view.findViewById(R.id.workSelfSufficiencyText);

        secondCityName = view.findViewById(R.id.cityName2);
        secondCityTemperature = view.findViewById(R.id.temperature2);
        secondCityHumidity = view.findViewById(R.id.humidity2);
        secondCityWind = view.findViewById(R.id.windText2);
        secondCityPopulation = view.findViewById(R.id.populationText2);
        secondCityEmploymentRate = view.findViewById(R.id.employmentRateText2);
        secondCityWorkSelfSufficiency = view.findViewById(R.id.workSelfSufficiencyText2);

        populationDataRetriever = new PopulationDataRetriever();
        employmentRateDataRetriever = new EmploymentRateDataRetriever();
        workSelfSufficiencyDataRetriever = new WorkSelfSufficiencyDataRetriever();
        weatherRepository = new WeatherRepository();

        compareCitiesButton.setOnClickListener(v -> {
            String city1 = cityOneInput.getText().toString().trim();
            String city2 = cityTwoInput.getText().toString().trim();

            if (city1.isEmpty() || city2.isEmpty()) {
                Toast.makeText(getContext(), "Syötä molemmat kaupunkien nimet", Toast.LENGTH_SHORT).show();
                return;
            }

            firstCityName.setText(city1);
            secondCityName.setText(city2);

            populationDataRetriever.getData(requireContext(), city1, firstCityPopulation);
            employmentRateDataRetriever.getData(requireContext(), city1, firstCityEmploymentRate);
            workSelfSufficiencyDataRetriever.getData(requireContext(), city1, firstCityWorkSelfSufficiency);

            // doing json search for weather data and population data also updating ui
            new Thread(() -> {
                try {
                    String json = weatherRepository.fetch(city1);
                    WeatherParser.WeatherData data = WeatherParser.parse(json);
                    ((Activity) getContext()).runOnUiThread(() -> {
                        firstCityTemperature.setText(String.format("Lämpötila: %.1f°C", data.getTemperature()));
                        firstCityHumidity.setText(String.format("Kosteus: %.0f%%", data.getHumidity()));
                        firstCityWind.setText(String.format("Tuuli: %.1f m/s", data.getWindSpeed()));
                    });
                } catch (Exception e) {
                    ((Activity) getContext()).runOnUiThread(() ->
                            Toast.makeText(getContext(), "Säätietojen haku epäonnistui: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }
            }).start();

            populationDataRetriever.getData(requireContext(), city2, secondCityPopulation);
            employmentRateDataRetriever.getData(requireContext(), city2, secondCityEmploymentRate);
            workSelfSufficiencyDataRetriever.getData(requireContext(), city2, secondCityWorkSelfSufficiency);

            new Thread(() -> {
                try {
                    String json = weatherRepository.fetch(city2);
                    WeatherParser.WeatherData data = WeatherParser.parse(json);
                    ((Activity) getContext()).runOnUiThread(() -> {
                        secondCityTemperature.setText(String.format("Lämpötila: %.1f°C", data.getTemperature()));
                        secondCityHumidity.setText(String.format("Kosteus: %.0f%%", data.getHumidity()));
                        secondCityWind.setText(String.format("Tuuli: %.1f m/s", data.getWindSpeed()));
                    });
                } catch (Exception e) {
                    ((Activity) getContext()).runOnUiThread(() ->
                            Toast.makeText(getContext(), "Säätietojen haku epäonnistui: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }
            }).start();
        });

        return view;
    }
}
