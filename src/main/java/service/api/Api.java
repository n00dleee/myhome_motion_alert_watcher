package service.api;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Api {
    URL url;
    HttpURLConnection connection;
    final String notificationUrl = "http://127.0.0.1:8080/hue-notification";

    final String getSensorsUrl = "http://192.168.1.8/api/wPBddzjMrQtcIU1Z3NVRb9WbupKmXlht4S8HMMlI/sensors";

    public Api() throws IOException {

    }

    private String get(String uri) throws IOException {
        url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        StringBuilder response = new StringBuilder();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {

            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());

            int responseCode = connection.getResponseCode();
            System.out.println("Response code=" + responseCode);
        } catch (IOException e) {
            return "error: " + e.getMessage();
        }
        return response.toString();
    }

    private boolean post(String uri, String payloadJson) throws IOException {
        System.out.println(("HTTP POST on URL=" + uri + "\r\nbody=" + payloadJson));
        url = new URL(uri);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = payloadJson.getBytes("utf-8");
            os.write(input, 0, input.length);
        } catch (IOException e) {
            return false;
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println(response.toString());

            int responseCode = con.getResponseCode();
            System.out.println("Response code=" + responseCode);

            return responseCode == 200;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean pushNotification(String payload) throws IOException {
        return post(notificationUrl, payload);
    }

    public String getSensorsState() throws IOException {
        String json = get(getSensorsUrl);
        System.out.println("SENSORS=" + json);
        return json;
    }

    public void getSpecificSensorState(){

    }
}