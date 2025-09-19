//Read me!
//This class is made for handling weather data from openWheaterMap

package com.example.olioohjelmointiharjoitusty.ShowData;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherRepository {

    private static final String API_KEY = "35b1ec02c88561cae8dd2c3fd7b38e4e";

    public String fetch(String city) throws IOException {
        String url = "https://api.openweathermap.org/data/2.5/weather?q="
                + city + "&appid=" + API_KEY + "&units=metric";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();
        return response.body() != null ? response.body().string() : null;
    }
}