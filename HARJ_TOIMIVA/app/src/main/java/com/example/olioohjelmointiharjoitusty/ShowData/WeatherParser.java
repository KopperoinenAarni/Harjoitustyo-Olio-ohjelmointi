//Readme
//This class extracts wheatherdata from openWeatherMap

package com.example.olioohjelmointiharjoitusty.ShowData;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherParser {

    public static class WeatherData {
        public final double temperature;
        public final double humidity;
        public final double windSpeed;
        public final String description;
        public final String iconCode;

        public WeatherData(double temperature, double humidity, double windSpeed,
                           String description, String iconCode) {
            this.temperature = temperature;
            this.humidity = humidity;
            this.windSpeed = windSpeed;
            this.description = description;
            this.iconCode = iconCode;
        }

        public double getTemperature() {
            return temperature;
        }

        public double getHumidity() {
            return humidity;
        }

        public double getWindSpeed() {
            return windSpeed;
        }

        public String getDescription() {
            return description;
        }

        public String getIconCode() {
            return iconCode;
        }
    }

    public static WeatherData parse(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONObject main = jsonObject.getJSONObject("main");
        double temperature = main.getDouble("temp");
        double humidity = main.getDouble("humidity");

        JSONObject wind = jsonObject.getJSONObject("wind");
        double windSpeed = wind.getDouble("speed");

        JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);
        String description = weather.getString("description");
        String iconCode = weather.getString("icon");

        return new WeatherData(temperature, humidity, windSpeed, description, iconCode);
    }
}
