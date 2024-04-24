package com.thena3ik.weatherbot.image;

import com.thena3ik.weatherbot.parsers.WeatherData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ImageGenerator {
    private static String city;

    private BufferedImage resultImage;

    // 0 - black theme (by default) || 1 - white theme || 2 - season theme
    private static byte colorTheme = 0;

    private static final String ResistSansMedium = "Resist Sans Text Medium";
    private static final String ResistSansLight = "Resist Sans Text Light";

    private static String baseImgPath = "src/main/resources/img/black theme/base.png";
    private static String weatherImgPath = "src/main/resources/img/black theme/weather images/";

    public ImageGenerator() {
        // Get weather data
        WeatherData weatherData = new WeatherData(city);

        // Merge weather image and base file
        resultImage = mergeImages(weatherData.getWeatherCode());

        // Initialize custom fonts
        initializeFonts();

        // Draw all needed text on merged image
        if (resultImage != null) {
            resultImage = drawTextOnImage(resultImage, weatherData);
        }

        // Send a message to console
        System.out.println("Image successfully generated!");
    }

    public ImageGenerator(String city) {
        ImageGenerator.city = city;

        // Get weather data
        WeatherData weatherData = new WeatherData(city);

        // Merge weather image and base file
        resultImage = mergeImages(weatherData.getWeatherCode());

        // Initialize custom fonts
        initializeFonts();

        // Draw all needed text on merged image
        if (resultImage != null) {
            resultImage = drawTextOnImage(resultImage, weatherData);
        }

        // Send a message to console
        System.out.println("Image successfully generated!");
    }

    private String getWeatherImageName(Long weatherCode) {
        return switch (Math.toIntExact(weatherCode)) {
            case 0 -> "sun";
            case 1, 2 -> "partly cloudy";
            case 3 -> "clouds";
            case 45, 48 -> "fog";
            case 51, 53, 55, 56, 57 -> "drizzle";
            case 61, 63, 65, 66, 67 -> "rain";
            case 71, 73, 75 -> "snow fall;";
            case 77 -> "snow grains";
            case 80, 81, 82 -> "rain showers";
            case 85, 86 -> "snow showers";
            case 95, 96, 99 -> "thunderstorm";
            default -> null;
        };
    }

    private BufferedImage mergeImages(Long weatherCode) {
        // Initializing our path
        weatherImgPath += getWeatherImageName(weatherCode) + ".png";

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

            // Returns the merged image
            return combinedImage;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void initializeFonts() {
        final String mediumFontPath = "src/main/resources/fonts/ResistSansText-Medium.ttf";
        final String lightFontPath = "src/main/resources/fonts/ResistSansText-Light.ttf";

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
            // Use default if custom font loading fails
            System.err.println("Failed to load custom font. Using default font.");
        }
    }

    private BufferedImage drawTextOnImage(BufferedImage image, WeatherData weatherData) {

        // Get the graphics object of the new image
        Graphics2D g = image.createGraphics();

        try {
            // Set the text color
            if (colorTheme == 0) {
                g.setColor(Color.white);
            } else {
                g.setColor(new Color(68,68,68));
            }

            // Set rendering hints to use antialiasing for our text
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

            int textWidth;
            int xPos;

            // Drawing <city> with reusable xPos and fontSize calculation
            int cityFontSize = 80;
            g.setFont(new Font(ResistSansMedium, Font.PLAIN, cityFontSize));
            textWidth = g.getFontMetrics().stringWidth(weatherData.getCity());

            while (textWidth >= 320) {
                cityFontSize -= 5;
                g.setFont(new Font(ResistSansMedium, Font.PLAIN, cityFontSize));
                textWidth = g.getFontMetrics().stringWidth(weatherData.getCity());
            }

            xPos = image.getWidth() - textWidth - 55;
            g.drawString(weatherData.getCity(), xPos, 240);

            // Drawing <time> with reusable xPos calculation
            g.setFont(new Font(ResistSansLight, Font.PLAIN, 72));
            textWidth = g.getFontMetrics().stringWidth(weatherData.getTime());
            xPos = image.getWidth() - textWidth - 55;

            g.drawString(weatherData.getTime(), xPos, 110);

            // Combine xPos calculation and drawing for remaining elements
            g.setFont(new Font(ResistSansLight, Font.PLAIN, 36));
            g.drawString(weatherData.getTemperature() + " °C", 395, 98);
            g.drawString(weatherData.getHumidity() + "%", 395, 161);
            g.drawString(weatherData.getWindSpeed() + " Km/h", 395, 231);

            g.dispose();

            // Returns processed image
            return image;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void changeTheme () {
        colorTheme++;

        if (colorTheme > 1)
            colorTheme = 0;

        switch (colorTheme) {
            case 0 -> {
                weatherImgPath = "src/main/resources/img/black theme/weather images/";
                baseImgPath = "src/main/resources/img/black theme/base.png";
            }
            case 1 -> {
                weatherImgPath = "src/main/resources/img/white theme/weather images/";
                baseImgPath = "src/main/resources/img/white theme/base.png";
            }
        }
    }

    public void saveImage() {
        // Saving an image
        try {
            // Save the combined image
            ImageIO.write(resultImage, "PNG", new File("src/main/resources/img/result.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getAsFile() throws IOException {

        File tempFile = Files.createTempFile("result", ".png").toFile();
        tempFile.deleteOnExit();

        ImageIO.write(resultImage, "png", tempFile);

        return tempFile;
    }
}
