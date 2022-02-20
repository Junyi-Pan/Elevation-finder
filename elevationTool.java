import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Scanner;

public class elevationTool {
static double searchSizeLat;
static double searchSizeLong;
static double latitude;
static double longitude;
static String apiKey = "AIzaSyDvwxHWtxixS7JVMGzRGGoJZu0JtT-uYxc";
static Scanner sc;
static double[] latitudes = new double[512];
static double[] longitudes = new double[512];
static double[] elevations = new double[512];
//this truncates all doubles to 6 digits after the decimal point
static DecimalFormat df = new DecimalFormat("#.######");

    public static void main(String[] args) throws IOException {

        try {
            searchSizeLat = (Double.parseDouble(args[0]))/2;
            searchSizeLong = (Double.parseDouble(args[1]))/2;
        } catch (Exception e) {
            searchSizeLat = 0.005;
            searchSizeLong = 0.005;
        }

        //Taking user input
        sc = new Scanner(System.in);
        System.out.println("This program finds the highest and lowest elevated points within 0.01 decimal degrees of a supplied point.");
        System.out.print("Enter latitude in decimal degrees: ");
        latitude = sc.nextDouble();

        //error checking
        if (latitude < -90 + searchSizeLat || latitude > 90 - searchSizeLat) {
            System.err.println("Invalid latitude. Must be between -90 and 90 including search bounds.");
            System.exit(1);
        }

        System.out.print("Enter longitude in decimal degrees: ");
        longitude = sc.nextDouble();

        //error checking
        if (longitude < -180 + searchSizeLong || longitude > 180 - searchSizeLong) {
            System.err.println("Invalid longitude. Must be between -180 and 180 including search bounds.");
            System.exit(1);
        }

        //Populating coordinate arrays
        for (int i = 1; i < 256; i++) {
            longitudes[256 + i] = longitude + i *  searchSizeLong / 256;
            latitudes[256 + i] = latitude + i * searchSizeLat / 256;
        }
        for (int i = 0; i <= 256; i++) {
            longitudes[256 - i] = longitude - i * searchSizeLong / 256;
            latitudes[256 - i] = latitude - i * searchSizeLat / 256;
        }
        for (int i = 0; i < 512; i++) {
            longitudes[i] = Double.parseDouble(df.format(longitudes[i]));
            latitudes[i] = Double.parseDouble(df.format(latitudes[i]));

        }

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

        //Processing API data returned (this is messy but we're not good at regex :(
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

        //returning data to user
        if (largest > -11035) {
            System.out.println("\nThe highest elevation within the region is: " + largest + " meters at location " + latitudes[indexL] + ", " + longitudes[indexL]);
        }
        if (smallest < 8849) {
            System.out.println("The lowest elevation within the region is: " + smallest + " meters at location " + latitudes[indexS] + ", " + longitudes[indexS]);
        }
    }
}
