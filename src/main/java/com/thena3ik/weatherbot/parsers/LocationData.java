package com.thena3ik.weatherbot.parsers;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;

public class LocationData {
    private static final String BASE_LOCATION_API_URL = "https://geocoding-api.open-meteo.com/v1/search";

    public JSONObject getLocationData(String city){

        HTTPParser httpParser = new HTTPParser();

        city = city.replaceAll(" ", "+");           //??????

        String urlString = String.format("%s?name=%s&count=1&language=en&format=json", BASE_LOCATION_API_URL, city);

        try{
            // 1. Fetch the API response based on API Link
            HttpURLConnection apiConnection = httpParser.fetchApiResponse(urlString);

            // check for response status
            // 200 - means that the connection was a success
            assert apiConnection != null;
            if(apiConnection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to geocoding-api.open-meteo.com    ");
                return null;
            }

            // 2. Read the response and convert store String type
            String jsonResponse = httpParser.readApiResponse(apiConnection);

            // 3. Parse the string into a JSON Object
            JSONParser jsonParser = new JSONParser();
            JSONObject resultsJsonObj = (JSONObject) jsonParser.parse(jsonResponse);

            // 4. Retrieve Location Data
            JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
            return (JSONObject) locationData.getFirst();
        }

        catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
