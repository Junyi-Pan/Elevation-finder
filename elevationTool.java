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

    public static void main(String[] args) throws MalformedURLException {
        //Taking user input
        sc = new Scanner(System.in);
        System.out.println("This program finds the highest elevation within 0.01 decimal degrees" +
                "of a supplied point.");
        System.out.println("Enter first coordinate: ");
        latitude = sc.nextFloat();

        if (latitude < -89.99 || latitude > 89.99) {
            System.err.println("Invalid latitude. Must be between -89.99 and 89.99.");
            System.exit(1);
        }

        System.out.println("Enter second coordinate: ");
        longitude = sc.nextFloat();
        if (longitude < -179.99 || longitude > 179.99) {
            System.err.println("Invalid longitude. Must be between -179.99 and 179.99");
            System.exit(1);
        }

        System.out.println("Lat: " + latitude);
        System.out.println("Long: " + longitude);

        //Populating coordinate arrays
        for (int i = 0; i < 256; i++) {
            longitudes[256 + i] = longitude + i * (0.005/256);
            longitudes[256 - i] = longitude - i * (0.005/256);
            latitudes[256 + i] = latitude + i * (0.005/256);
            latitudes[256 - i] = latitude - i * (0.005/256);
        }

        //Filling array of gpsCoordinate objects
        for (int i = 0; i < 512; i++) {
            coordinates[i] = new gpsCoordinate(latitudes[i], longitudes[i]);
            System.out.println("lat " + i + latitudes[i]);
            System.out.println("long " + i + longitudes[i]);
        }

        //Single point google cloud API call
        //URL url = new URL("https://maps.googleapis.com/maps/api/elevation/json?locations=" + latitude + "%2C" + longitude + "&key=" + apiKey);

        //Forming coordinate string for API call
        String coordinateString = "";
        for (int i = 0; i < 512; i++) {
            coordinateString += (latitudes[i] + ',' + longitudes[i] + '|');
            coordinateString = coordinateString.substring(0, coordinateString.length() - 1);
        }
        System.out.println(coordinateString);

        //array API call
        URL url = new URL("https://maps.googleapis.com/maps/api/elevation/json?locations=" + coordinateString + "&key=" + apiKey);
        System.out.println(url);

        //Processing API data returned

    }

}
