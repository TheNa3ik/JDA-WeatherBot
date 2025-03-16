package com.thena3ik.weatherbot.parsers;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;

public class WeatherData {

    private static final String BASE_WEATHER_API_URL = "https://api.open-meteo.com/v1/forecast";
    private JSONObject weatherData;

    public WeatherData (String city) {
        weatherData = new JSONObject();
        collectWeatherData(city);
    }

    private void collectWeatherData(String city) {
        LocationData locationData = new LocationData();
        TimeData timeData = new TimeData();

        try {
            JSONObject cityLocation = locationData.getLocationData(city);
            double latitude = (double) cityLocation.get("latitude");
            double longitude = (double) cityLocation.get("longitude");

            JSONObject cityTime = timeData.getTimeData(latitude, longitude);
            JSONObject cityCurrentWeather = getCurrentWeatherData(latitude,longitude);

            if (cityCurrentWeather != null) {
                fillJsonObject(city, latitude, longitude, cityTime, cityCurrentWeather);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private JSONObject getCurrentWeatherData(double latitude, double longitude){

        HTTPParser httpParser = new HTTPParser();

        try{
            // 1. Fetch the API response based on API Link
            String url = String.format("%s?latitude=%f&longitude=%f&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m",
                    BASE_WEATHER_API_URL, latitude, longitude);
            HttpURLConnection apiConnection = httpParser.fetchApiResponse(url);

            // check for response status
            // 200 - means that the connection was a success
            assert apiConnection != null;
            if(apiConnection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to api.open-meteo.com");
            }

            // 2. Read the response and convert store String type
            String jsonResponse = httpParser.readApiResponse(apiConnection);

            // 3. Parse the string into a JSON Object
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);

            // Console out for debugging
            // System.out.println(currentWeatherJson.toJSONString());

            return (JSONObject) jsonObject.get("current");
        }

        catch(Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }


    private void fillJsonObject (String city, double latitude, double longitude, JSONObject cityTime,
                                 JSONObject cityCurrentWeather) {
        weatherData.put("city", city);
        weatherData.put("time", cityTime.get("time"));
        weatherData.put("day", cityTime.get("day"));
        weatherData.put("month", cityTime.get("month"));
        weatherData.put("weather_code", (cityCurrentWeather).get("weather_code"));
        weatherData.put("temperature_2m", cityCurrentWeather.get("temperature_2m"));
        weatherData.put("relative_humidity_2m", cityCurrentWeather.get("relative_humidity_2m"));
        weatherData.put("wind_speed_10m", cityCurrentWeather.get("wind_speed_10m"));

        //Method that do console output for debugging
        debugOutput(city, latitude, longitude, cityTime, cityCurrentWeather);
    }

    private void debugOutput(String city, double latitude, double longitude, JSONObject cityTime,
                             JSONObject cityCurrentWeather) {
        System.out.println("\ncity: " + city);

        System.out.print("lat: " + latitude);
        System.out.println("\tlong: " + longitude);

        System.out.print("time: " + cityTime.get("time"));
        System.out.print("\tday: " + cityTime.get("day"));
        System.out.println("\tmonth: " + cityTime.get("month"));

        System.out.print("weather code: " + cityCurrentWeather.get("weather_code"));
        System.out.print("\ttemperature: " + cityCurrentWeather.get("temperature_2m"));
        System.out.print("\thumidity: " + cityCurrentWeather.get("relative_humidity_2m"));
        System.out.println("\twind speed: " + cityCurrentWeather.get("wind_speed_10m"));
    }

    public JSONObject getWeatherDataAsJson() {
        return weatherData;
    }
}
