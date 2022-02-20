import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class elevationTool {
static double squareSide = 0.01;
static double latitude;
static double longitude;
static String apiKey = "AIzaSyDvwxHWtxixS7JVMGzRGGoJZu0JtT-uYxc";
static Scanner sc;
static gpsCoordinate[] coordinates = new gpsCoordinate[512];
static double[] latitudes = new double[512];
static double[] longitudes = new double[512];
static double[] elevations = new double[512];

    public static void main(String[] args) throws IOException {
        //Taking user input
        sc = new Scanner(System.in);
        System.out.println("This program finds the highest elevation within 0.01 decimal degrees" +
                "of a supplied point.");
        System.out.println("Enter latitude: ");
        latitude = sc.nextFloat();

        if (latitude < -89.99 || latitude > 89.99) {
            System.err.println("Invalid latitude. Must be between -89.99 and 89.99.");
            System.exit(1);
        }

        System.out.println("Enter longitude: ");
        longitude = sc.nextFloat();
        if (longitude < -179.99 || longitude > 179.99) {
            System.err.println("Invalid longitude. Must be between -179.99 and 179.99");
            System.exit(1);
        }

        //Populating coordinate arrays
        for (int i = 1; i < 256; i++) {
            longitudes[256 + i] = longitude + i *  0.005 / 256;
            latitudes[256 + i] = latitude + i * 0.005 / 256;
        }

        for (int i = 0; i <= 256; i++) {
            longitudes[256 - i] = longitude - i * 0.005 / 256;
            latitudes[256 - i] = latitude - i * 0.005 / 256;
        }
        //Single point google cloud API call
        //URL url = new URL("https://maps.googleapis.com/maps/api/elevation/json?locations=" + latitude + "%2C" + longitude + "&key=" + apiKey);

        //Forming coordinate string for API call
        String coordinateString = "";
        for (int i = 0; i < 512; i++) {
            coordinateString += latitudes[i];
            coordinateString += ",";
            coordinateString += longitudes[i];
            coordinateString += "|";
        }
        coordinateString = coordinateString.substring(0, coordinateString.length() - 1);

        //array API call
        URL url = new URL("https://maps.googleapis.com/maps/api/elevation/json?locations=" + coordinateString + "&key=" + apiKey);

        //Processing API data returned
        String[] stringData;
        String json = new Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next();
        JsonParser jsonParser = new JsonParser();
        String data = jsonParser.parse(json).toString();

        stringData = data.split("elevation\":");
        String[] stringDataFixed = new String[512];
        for (int i = 1; i < 513; i++) {
            stringDataFixed[i - 1] = stringData[i];
        }

        int counter = 0;
        char[] temp;
        for (String s : stringDataFixed) {
            temp = s.toCharArray();
            for (int i = 0; i < temp.length; i++) {
                if (temp[i] == ',') {
                    s = new String(temp);
                    s = s.substring(0, i);
                    stringDataFixed[counter] = s;
                    break;
                }
            }
            counter++;
        }

        for (int i = 0; i < 512; i++) {
            elevations[i] = Double.parseDouble(stringDataFixed[i]);
        }

        //Filling array of gpsCoordinate objects, printing for diagnostics
        for (int i = 0; i < 512; i++) {
            coordinates[i] = new gpsCoordinate(latitudes[i], longitudes[i], elevations[i]);
        }

        //finds maximum elevation
        double largest = -11035; //marianas trench
        double smallest = 8849; //mount everest
        int indexL = -1;
        int indexS = -1;
        for (int i = 0; i < 512; i++) {
            if (elevations[i] > largest) {
                largest = elevations[i];
                indexL = i;
            } else if (elevations[i] < smallest) {
                smallest = elevations[i];
                indexS = i;
            }
        }

        if (largest > -11035) {
            System.out.println("\nThe highest elevation within the region is: " + largest + " meters at location " + latitudes[indexL] + ", " + longitudes[indexL]);
        }
        if (smallest < 8849) {
            System.out.println("The lowest elevation within the region is: " + smallest + " meters at location " + latitudes[indexS] + ", " + longitudes[indexS]);
        }
    }
}
