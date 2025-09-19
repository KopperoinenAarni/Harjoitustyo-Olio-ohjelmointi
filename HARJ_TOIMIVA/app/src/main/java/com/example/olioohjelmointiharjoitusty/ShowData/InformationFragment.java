//Read me
//This class is made for showing the data and wheather img

package com.example.olioohjelmointiharjoitusty.ShowData;



import android.os.Bundle;

import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.widget.ImageView;

import android.widget.TextView;

import android.widget.Toast;



import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProvider;



import com.example.olioohjelmointiharjoitusty.R;

import com.example.olioohjelmointiharjoitusty.SharedViewModel;





import java.util.concurrent.ExecutorService;

import java.util.concurrent.Executors;



public class InformationFragment extends Fragment {



    private TextView temperatureText, lastSearch, humidityText, windText, weatherDescription;

    private TextView populationText, employmentText, selfSufficiencyText;

    private ImageView weatherIcon;

    private SharedViewModel sharedViewModel;



    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_information, container, false);



        temperatureText = view.findViewById(R.id.temperatureText);

        lastSearch = view.findViewById(R.id.cityTxt);

        humidityText = view.findViewById(R.id.humidityTxt);

        windText = view.findViewById(R.id.windTxt);

        weatherDescription = view.findViewById(R.id.weatherDescription);

        weatherIcon = view.findViewById(R.id.weatherIcon);

        populationText = view.findViewById(R.id.populationCount);

        employmentText = view.findViewById(R.id.employmentRateValue);

        selfSufficiencyText = view.findViewById(R.id.selfSufficiencyValue);



        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sharedViewModel.getCityName().observe(getViewLifecycleOwner(), city -> {

            if (city != null && !city.isEmpty()) {

                lastSearch.setText(city);

                fetchWeatherData(city);

                getPopulationData(city);

                getEmploymentData(city);

                getSelfSufficiencyData(city);



            }

        });



        return view;

    }



    private void fetchWeatherData(String city) {

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {

            WeatherRepository repo = new WeatherRepository();

            try {

                String result = repo.fetch(city);

                if (result != null) {

                    WeatherParser.WeatherData data = WeatherParser.parse(result);

                    requireActivity().runOnUiThread(() -> updateUi(data));

                } else {

                    requireActivity().runOnUiThread(() ->

                            Toast.makeText(getActivity(), "Tyhjä vastaus palvelimelta", Toast.LENGTH_SHORT).show());

                }

            } catch (Exception e) {

                e.printStackTrace();

                requireActivity().runOnUiThread(() ->

                        Toast.makeText(getActivity(), "Virhe haettaessa säätietoja", Toast.LENGTH_SHORT).show());

            }

        });

    }



    private void updateUi(WeatherParser.WeatherData data) {

        int roundedTemp = (int) Math.round(data.temperature);

        temperatureText.setText(roundedTemp + "°C");

        humidityText.setText("Kosteus: " + data.humidity + "%");

        windText.setText("Tuuli: " + data.windSpeed + " m/s");

        weatherDescription.setText(data.description);



        String resourceName = "ic_" + data.iconCode;

        int resourceId = getResources().getIdentifier(resourceName, "drawable", requireActivity().getPackageName());

        weatherIcon.setImageResource(resourceId);

    }



    private void getPopulationData(String city) {

        new PopulationDataRetriever().getData(requireContext(), city, populationText);

    }



    private void getEmploymentData(String city) {

        new EmploymentRateDataRetriever().getData(requireContext(), city, employmentText);

    }

    private void getSelfSufficiencyData(String city) {

        new WorkSelfSufficiencyDataRetriever().getData(requireContext(), city, selfSufficiencyText);

    }

}