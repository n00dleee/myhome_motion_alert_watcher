package service.watcher;

import com.google.gson.Gson;
import model.huesensordata.HueIndividualSensorData;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;
import service.api.HueApi;
import service.mqtt.MqttManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Watcher {
    HueApi hueApi;
    MqttManager mqttManager;
    List<Integer> listOfSensorsIdToWatch;
    private boolean state;

    public Watcher() throws IOException, MqttException {
        listOfSensorsIdToWatch = new ArrayList<>();
        hueApi = new HueApi();
        mqttManager = new MqttManager();
        state = false;

    }

    public boolean start() {
        state = true;


        return state;
    }

    public boolean stop() {
        state = false;


        return state;
    }

    public boolean checkSensorsState() throws IOException {
        try {
            //get sensors state
            String jsonString = hueApi.getSensorsState();
            JSONObject jsonObject;
            JSONObject sensorJsonObject;
            String name;
            Boolean presence;
            String producName;

            for (Integer id : listOfSensorsIdToWatch) {
                jsonObject = new JSONObject(jsonString);
                sensorJsonObject = jsonObject.getJSONObject(id.toString());

                System.out.println("Sensor data string retrieved for id=" + id.toString() + "\r\n" + sensorJsonObject.toString());

                Gson g = new Gson();
                HueIndividualSensorData sensorData = g.fromJson(sensorJsonObject.toString(), HueIndividualSensorData.class);

                System.out.println("Sensor data converted to object" + sensorData.toString());
            }
            return true;
        } catch (
                Exception e) {
            System.out.println("Error while parsing sensor data:" + e.getMessage());
            return false;
        }
    }

    public boolean checkSensorState(Integer id) throws IOException {
        try {
            //get sensors state
            String jsonString = hueApi.getSensorsState();
            JSONObject jsonObject;
            JSONObject sensorJsonObject;
            String name;
            Boolean presence;
            String producName;

            jsonObject = new JSONObject(jsonString);
            sensorJsonObject = jsonObject.getJSONObject(id.toString());

            System.out.println("Sensor data string retrieved for id=" + id.toString() + "\r\n" + sensorJsonObject.toString());

            Gson g = new Gson();
            HueIndividualSensorData sensorData = g.fromJson(sensorJsonObject.toString(), HueIndividualSensorData.class);

            System.out.println("Sensor presence state=" +  sensorData.state.presence);
            System.out.println("Sensor name=" +  sensorData.name);
            System.out.println("Sensor productName=" +  sensorData.productname);

            return true;
        } catch (
                Exception e) {
            System.out.println("Error while parsing sensor data:" + e.getMessage());
            return false;
        }
    }
}
