package com.thena3ik.weatherbot.parsers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class WeatherData {
    private String city;
    private String date;
    private String time;
    private Long weatherCode = 0L;
    private double temperature = 0;
    private Long humidity = 0L;
    private double windSpeed = 0;

    public WeatherData(String city) {
        this.city = city;

        try {
            JSONObject cityLocationData = getLocationData(city);
            assert cityLocationData != null;
            double latitude = (double) cityLocationData.get("latitude");
            double longitude = (double) cityLocationData.get("longitude");

            getWeatherData(latitude,longitude);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONObject getLocationData(String city){
        city = city.replaceAll(" ", "+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                city + "&count=1&language=en&format=json";

        try{
            // 1. Fetch the API response based on API Link
            HttpURLConnection apiConnection = fetchApiResponse(urlString);

            // check for response status
            // 200 - means that the connection was a success
            assert apiConnection != null;
            if(apiConnection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }

            // 2. Read the response and convert store String type
            String jsonResponse = readApiResponse(apiConnection);

            // 3. Parse the string into a JSON Object
            JSONParser parser = new JSONParser();
            JSONObject resultsJsonObj = (JSONObject) parser.parse(jsonResponse);

            // 4. Retrieve Location Data
            JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
            return (JSONObject) locationData.getFirst();
        }

        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getWeatherData(double latitude, double longitude){
        try{
            // 1. Fetch the API response based on API Link
            String url = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude +
                    "&longitude=" + longitude + "&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";
            HttpURLConnection apiConnection = fetchApiResponse(url);

            // check for response status
            // 200 - means that the connection was a success
            assert apiConnection != null;
            if(apiConnection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
            }

            // 2. Read the response and convert store String type
            String jsonResponse = readApiResponse(apiConnection);

            // 3. Parse the string into a JSON Object
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonResponse);
            JSONObject currentWeatherJson = (JSONObject) jsonObject.get("current");

            // Console out for debugging
            //System.out.println(currentWeatherJson.toJSONString());

            // Old format
            //String date = (String) currentWeatherJson.get("time");
            //String[] dateParts = date.split("T");

            // 4. Fill the fields from JSON Object
            this.date = String.valueOf(LocalDate.now());
            this.time = String.valueOf(LocalTime.now()).substring(0, 5);
            this.weatherCode = (Long) currentWeatherJson.get("weather_code");
            this.temperature = (Double) currentWeatherJson.get("temperature_2m");
            this.humidity = (Long) currentWeatherJson.get("relative_humidity_2m");
            this.windSpeed = (Double) currentWeatherJson.get("wind_speed_10m");
        }

        catch(Exception e){
            e.printStackTrace();
        }
    }

    private String readApiResponse(HttpURLConnection apiConnection) {
        try {
            // Create a StringBuilder to store the resulting JSON data
            StringBuilder resultJson = new StringBuilder();

            // Create a Scanner to read from the InputStream of the HttpURLConnection
            Scanner scanner = new Scanner(apiConnection.getInputStream());

            // Loop through each line in the response and append it to the StringBuilder
            while (scanner.hasNext()) {
                // Read and append the current line to the StringBuilder
                resultJson.append(scanner.nextLine());
            }

            // Close the Scanner to release resources associated with it
            scanner.close();

            // Return the JSON data as a String
            return resultJson.toString();
        }

        catch (IOException e) {
            // Print the exception details in case of an IOException
            e.printStackTrace();
        }

        // Return null if there was an issue reading the response
        return null;
    }

    private HttpURLConnection fetchApiResponse(String urlString){
        try{
            // attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // set request method to get
            conn.setRequestMethod("GET");

            return conn;
        }

        catch(IOException e){
            e.printStackTrace();
        }

        // could not make connection
        return null;
    }

    public String[] getAsStringArray() {
        return new String[] {city, time,
                String.valueOf(weatherCode),
                String.valueOf(temperature),
                String.valueOf(humidity),
                String.valueOf(windSpeed)
        };
    }

    public String getCity() {
        return city;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Long getWeatherCode() {
        return weatherCode;
    }

    public double getTemperature() {
        return temperature;
    }

    public Long getHumidity() {
        return humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }
}
