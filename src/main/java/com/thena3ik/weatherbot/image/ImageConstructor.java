package com.thena3ik.weatherbot.image;

import com.thena3ik.weatherbot.parsers.TimeData;
import com.thena3ik.weatherbot.parsers.WeatherData;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ImageConstructor {

    private BufferedImage resultImage;

    private final static String ImgPath = "src/main/resources/img";
    private final static String FontPath = "src/main/resources/fonts";

    private static final String ResistSansMedium = "Resist Sans Text Medium";
    private static final String ResistSansLight = "Resist Sans Text Light";


    public ImageConstructor(String city, String theme) {
        WeatherData weatherData = new WeatherData(city);
        JSONObject weatherDataJson = weatherData.getWeatherDataAsJson();

        System.out.println(theme + " theme");

        ConstructImage(weatherDataJson, theme);
    }

    static {

        String mediumFontPath = String.format("%s/ResistSansText-Medium.ttf", FontPath);
        String lightFontPath = String.format("%s/ResistSansText-Light.ttf", FontPath);

        // Load the font using Java Font API with error handling
        Font mediumFont = null;
        Font lightFont = null;

        try {
            mediumFont = Font.createFont(Font.PLAIN, new File(mediumFontPath));
            lightFont = Font.createFont(Font.PLAIN, new File(lightFontPath));
        } catch (FontFormatException | IOException e) {
            System.err.println("Error loading font: " + e.getMessage());
        }

        // Handle font loading error
        if (mediumFont == null || lightFont == null) {
            System.err.println("Failed to load custom font.");
        }
    }


    private void ConstructImage(JSONObject weatherData, String themeRaw) {

        // Initializing our paths
        String theme = themeRaw;
        if (themeRaw.equals("season")) {
            theme = SeasonTheme.getSeason((Long) weatherData.get("month"));
        }

        String baseImgPath = ImgPath + "/bases/" + theme + " base.png";
        //System.out.println(baseImgPath);

        String weatherImgColor;
        switch (theme) {
            case "white" -> weatherImgColor = "black";
            case "summer" -> weatherImgColor = "summer";
            default -> weatherImgColor = "white";
        }

        String weatherImgPath = ImgPath + "/weather images/" + weatherImgColor + "/"
                + WeatherCondition.getImageName((Long) weatherData.get("weather_code")) + ".png";
        //System.out.println(weatherImgPath);

        try {
            // Load the images
            BufferedImage baseImage = ImageIO.read(new File(baseImgPath));
            BufferedImage weatherImage = ImageIO.read(new File(weatherImgPath));

            // Create a new BufferedImage
            BufferedImage combinedImage = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);

            // Get the graphics object of the new image
            Graphics2D g = combinedImage.createGraphics();

            // Draw the first image onto the new image
            g.drawImage(baseImage, 0, 0, null);

            // Draw the second image onto the new image
            g.drawImage(weatherImage, 55, 35, null);

            g.dispose();

            drawTextOnImage(combinedImage, weatherData, theme);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void drawTextOnImage(BufferedImage image, JSONObject weatherData, String theme) {

        // Get the graphics object of the new image
        Graphics2D g = image.createGraphics();

        // Set rendering hints to use antialiasing for our text
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        Color whiteColor = Color.WHITE;
        Color blackColor = new Color(68,68,68);
        Color brownColor = new Color(96, 60, 6);

        g.setColor(theme.equals("white") ? blackColor : whiteColor);

        // Drawing <city> with reusable xPos and fontSize scaling
        int xPos;
        int cityFontSize = 80;
        g.setFont(new Font(ResistSansMedium, Font.PLAIN, cityFontSize));
        int textWidth = g.getFontMetrics().stringWidth((String) weatherData.get("city"));

        double scaleFactor = 320.0 / textWidth;

        if (scaleFactor < 1) {
            cityFontSize = Math.max(12, (int) (cityFontSize * scaleFactor)); // Minimum size of 12
            g.setFont(new Font(ResistSansMedium, Font.PLAIN, cityFontSize));
            textWidth = g.getFontMetrics().stringWidth((String) weatherData.get("city")); // Recalculate
        }


        xPos = image.getWidth() - textWidth - 55;
        g.drawString((String) weatherData.get("city"), xPos, 240);

        // Drawing <time> with reusable xPos calculation
        g.setFont(new Font(ResistSansLight, Font.PLAIN, 72));
        textWidth = g.getFontMetrics().stringWidth((String) weatherData.get("time"));
        xPos = image.getWidth() - textWidth - 55;

        g.drawString((String) weatherData.get("time"), xPos, 110);


        if (theme.equals("summer")) {
            g.setColor(brownColor);
        }

        // Combine xPos calculation and drawing for remaining elements
        g.setFont(new Font(ResistSansLight, Font.PLAIN, 36));
        g.drawString(weatherData.get("temperature_2m") + " °C", 395, 98);
        g.drawString(weatherData.get("relative_humidity_2m") + " %", 395, 161);
        g.drawString(weatherData.get("wind_speed_10m") + " Km/h", 395, 231);

        g.dispose();

        resultImage = image;
    }

    public void saveImage() {
        try {
            // Save the combined image
            ImageIO.write(resultImage, "PNG", new File("src/main/resources/img/result.png"));
            // ImageIO.write(resultImage, "PNG", new File("D:/my folder/programming projects/java"));

            System.out.println("Successfully saved an image!");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public File getAsFile() throws IOException {

        File tempFile = Files.createTempFile("result", ".png").toFile();
        tempFile.deleteOnExit();

        ImageIO.write(resultImage, "png", tempFile);

        System.out.println("Successfully generated an image!");
        return tempFile;
    }
}
