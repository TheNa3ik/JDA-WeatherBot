package com.thena3ik.weatherbot.parsers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class HTTPParser {
    public String readApiResponse(HttpURLConnection apiConnection) {
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
            System.out.println(e.getMessage());
        }

        // Return null if there was an issue reading the response
        return null;
    }

    public HttpURLConnection fetchApiResponse(String urlString){
        try{
            // attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // set request method to get
            conn.setRequestMethod("GET");

            return conn;
        }

        catch(IOException e){
            System.out.println(e.getMessage());
        }

        // could not make connection
        return null;
    }
}
