import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Scanner;

public class WeatherApp {

    public static JSONObject getWeatherData(String locationName) {
        // Get location coordinates using geolocation API
        JSONArray locationData = getLocationData(locationName);
        if (locationData == null || locationData.isEmpty()) {
            System.out.println("Error: Couldn't fetch location data.");
            return null;
        }

//         For example, let's fetch the first location
        JSONObject location = (JSONObject) locationData.get(0);

//         You can now process this location data further
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

//        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude +
//                "&longitude=" + longitude +
//                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=America%2FLos_Angeles";
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relativehumidity_2m,weathercode,windspeed_10m&timezone=America%2FLos_Angeles";

        try{
            HttpURLConnection conn=fetchAPIResponse(urlString);
            if(conn==null){
                System.out.println("ERROR! couldn't connect to API");
                return null;
            }
            else if(conn.getResponseCode()!=200){
                System.err.println("ERROR! couldn't connect to API");
                return null;
            }
            else{
                //storing resultant json data
                StringBuilder result_json=new StringBuilder();
                Scanner sc=new Scanner(conn.getInputStream());
                while(sc.hasNext()){
                    //read and store into the stringBuilder
                    result_json.append(sc.nextLine());
                }
                sc.close();
                conn.disconnect();

                // parsing the data....
                JSONObject resJSONObj=(JSONObject) new JSONParser().parse(result_json.toString());

                // retrieval of hourly data
                JSONObject hourly=(JSONObject) resJSONObj.get("hourly");
                if (hourly == null) {
                    System.out.println("Error: 'hourly' data is missing in the API response.");
                    return null;
                }

                // Gotta' get the hourly data
                JSONArray time=(JSONArray) hourly.get("time");
                int time_index=findIndexOfCurrTime(time);

                // get temperature
                JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
                double temperature = (double) temperatureData.get(time_index);

                // get weather code
                JSONArray weathercode = (JSONArray) hourly.get("weathercode");
                if (weathercode == null) {
                    System.out.println("Error: 'weather_code' data is missing in the API response.");
                    return null;
                }
                String weatherCondition = convertWeatherCode((long) weathercode.get(time_index));

                // get humidity
                JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
                long humidity = (long) relativeHumidity.get(time_index);

                // get windspeed
                JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
                double windspeed = (double) windspeedData.get(time_index);

                // build the weather json data object that we are gonna' access in our frontend
                JSONObject weatherData = new JSONObject();
                weatherData.put("temperature", temperature);
                weatherData.put("weather-condition", weatherCondition);
                weatherData.put("humidity", humidity);
                weatherData.put("windspeed", windspeed);
                return weatherData;
            }

        }
        catch(Exception e){
            e.printStackTrace();
        }

//      For demonstration, let's return the location JSONObject
        return null;
    }

    public static JSONArray getLocationData(String locationName) {
        locationName = locationName.replaceAll(" ", "+"); // To follow the API's request format

        // Building API URL with location parameter
//        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName + "&count=1&language=en&format=json";
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";
        try {
            HttpURLConnection conn = fetchAPIResponse(urlString);
            if (conn == null) {
                System.out.println("ERROR: Couldn't connect to API");
                return null;
            }
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Couldn't connect to API");
                return null;
            } else {
                StringBuilder resJSON = new StringBuilder();
                Scanner sc = new Scanner(conn.getInputStream());
                while (sc.hasNext()) {
                    resJSON.append(sc.nextLine());
                }
                sc.close();
                conn.disconnect();

                // Parse the JSON string into a JSON object
                JSONObject resultJSONobj = (JSONObject) new JSONParser().parse(resJSON.toString());

                // Get location data list the API generated from the location name
                JSONArray locationData = (JSONArray) resultJSONobj.get("results");
                return locationData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchAPIResponse(String urlString) {
        try {
            // Creating the connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Setting the GET method
            conn.setRequestMethod("GET");

            // Connect to API
            conn.connect();

            // Return the connection object
            return conn;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }
    private static int findIndexOfCurrTime(JSONArray timelist){
        String currTime=getCurrentTime();
        for(int i=0;i<timelist.size();i++){
            String time=(String) timelist.get(i);
            if(time.equalsIgnoreCase(currTime)){
                return i;
            }
        }
        return 0;
    }
    private static String getCurrentTime(){
        LocalDateTime currDateTime=LocalDateTime.now();
        DateTimeFormatter dtf= DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        return currDateTime.format(dtf);
    }

    private static String convertWeatherCode(long weathercode){
        String weatherCondition="";
        if(weathercode==0L){
            weatherCondition="Clear";
        }
        else if(weathercode>0L && weathercode<=3L){
            weatherCondition="Cloudy";
        }
        else if((weathercode>=51L && weathercode<=67L) || (weathercode>=80L && weathercode<=99L)){
            weatherCondition="Rain";
        }
        else if(weathercode>=71L && weathercode<=77L){
            weatherCondition="Snow";
        }
        return weatherCondition;
    }

}
