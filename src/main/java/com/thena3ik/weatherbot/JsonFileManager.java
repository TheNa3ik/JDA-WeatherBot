package com.thena3ik.weatherbot;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class JsonFileManager {

    private final String filePath = "src/main/resources/db.json";

    public JsonFileManager() {
        // Create the file if it doesn't exist
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
               System.out.println(e.getMessage());
            }
        } else {
            try {
                loadJson();
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void saveTheme(long serverId, String theme) {
        try {
            JSONObject json = loadJson();
            json.put(String.valueOf(serverId), theme); // Use String.valueOf() for the serverId
            saveJson(json);

            // Debug
            System.out.println("\nserver id: " + serverId + "\ttheme: " + getTheme(serverId));

        } catch (IOException | ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getTheme(long serverId) {
        try {
            JSONObject json = loadJson();
            return (String) json.getOrDefault(String.valueOf(serverId), null);
        } catch (IOException | ParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private JSONObject loadJson() throws IOException, ParseException {
        File file = new File(filePath);
        JSONParser parser = new JSONParser();

        if (file.exists() && file.length() > 0) {
            try (FileReader reader = new FileReader(filePath)) {
                return (JSONObject) parser.parse(reader);
            }
        } else {
            return new JSONObject(); // Return a new JSONObject if file doesn't exist
        }
    }

    private void saveJson(JSONObject json) throws IOException {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(json.toJSONString());
            file.flush();
        }
    }
}