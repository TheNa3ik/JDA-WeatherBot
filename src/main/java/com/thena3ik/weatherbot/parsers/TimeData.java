package com.thena3ik.weatherbot.parsers;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;

public class TimeData {
    private static final String BASE_TIME_API_URL = "https://timeapi.io/api/Time/current/coordinate";

    public JSONObject getTimeData(double latitude, double longitude){
        try {
            HTTPParser httpParser = new HTTPParser();

            // 1. Fetch the API response based on API Link
            String url = String.format("%s?latitude=%f&longitude=%f", BASE_TIME_API_URL, latitude, longitude);
            HttpURLConnection apiConnection = httpParser.fetchApiResponse(url);

            // check for response status
            // 200 - means that the connection was a success
            assert apiConnection != null;
            if(apiConnection.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to timeapi.io");
            }

            // 2. Read the response and convert store String type
            String jsonResponse = httpParser.readApiResponse(apiConnection);

            // 3. Parse the string into a JSON Object
            JSONParser parser = new JSONParser();

            // Console out for debugging
            //System.out.println(currentTimeJson.toJSONString());

            return (JSONObject) parser.parse(jsonResponse);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
