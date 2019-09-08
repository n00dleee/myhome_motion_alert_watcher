package service.watcher;

import com.google.gson.Gson;
import model.huesensordata.HueIndividualSensorData;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;
import service.api.Api;
import service.mqtt.MqttManager;

import java.io.IOException;
import java.util.*;

public class Watcher {
    Api api;
    MqttManager mqttManager;
    List<Integer> listOfSensorsIdToWatch;
    public Map<Integer, Date> alertMap;
    public List<Thread> watcherThreadPool;
    private boolean state = false;

    public Watcher() throws IOException, MqttException {
        listOfSensorsIdToWatch = new ArrayList<>();
        api = new Api();
        mqttManager = new MqttManager();

        alertMap = new HashMap<>();
        watcherThreadPool = new ArrayList<>();
    }

    public void addSensorToWatch(List<Integer> listOfId) {
        listOfSensorsIdToWatch.addAll(listOfId);
    }

    public boolean start() {
        state = true;

        System.out.println("In watcher.start() method...");

        //creating threads
        for (Integer id : listOfSensorsIdToWatch) {
            watcherThreadPool.add(new Thread(() -> {
                try {
                    SensorWatcherThreadLoop(id);
                } catch (InterruptedException e) {
                    System.out.println("Sensor alert watcher for Id=" + id + " terminated");
                }
            }, "Watcher for sensor id -> " + id.toString()));
        }

        //starting thread
        for (Thread thread : watcherThreadPool) {
            System.out.println("Starting watcher thread for id=" + thread.getName());
            thread.start();
        }


        return state;
    }

    public boolean stop() {
        state = false;

        return state;
    }

    public Map<Integer, Date> getAlertMap() {
        return alertMap;
    }

    private void SensorWatcherThreadLoop(Integer id) throws InterruptedException {
        while (true) {
            try {
                Map<Integer, Date> newAlertMap = checkSensorsState();

                if (newAlertMap != null)
                    alertMap.putAll(newAlertMap);

                System.out.println("Alertmap size:" + alertMap.size());
                if (alertMap.size() > 0) {
                    JSONObject json = new JSONObject();

                    for (Map.Entry<Integer, Date> entry : alertMap.entrySet()) {
                        json.put(entry.getKey().toString(), entry.getValue());
                    }
                    api.pushNotification(json.toString());
                }
                Thread.sleep(2000);
            } catch (InterruptedException | IOException e) {
                System.out.println("Thread loop crashed: " + e.getMessage());
            }
        }
    }

    private Map<Integer, Date> checkSensorsState() throws IOException {
        try {
            //get sensors state
            String jsonString = api.getSensorsState();
            JSONObject jsonObject;
            JSONObject sensorJsonObject;

            Map<Integer, Date> newAlertMap = new HashMap<>();

            for (Integer id : listOfSensorsIdToWatch) {
                jsonObject = new JSONObject(jsonString);
                sensorJsonObject = jsonObject.getJSONObject(id.toString());

                System.out.println("Sensor data string retrieved for id=" + id.toString() + "\r\n" + sensorJsonObject.toString());

                Gson g = new Gson();
                HueIndividualSensorData sensorData = g.fromJson(sensorJsonObject.toString(), HueIndividualSensorData.class);

                if (sensorData.state.presence) {
                    alertMap.put(id, new Date());
                }
            }
            return newAlertMap;
        } catch (
                Exception e) {
            System.out.println("Error while parsing sensor data:" + e.getMessage());
            return null;
        }
    }

    public boolean checkSensorState(Integer id) throws IOException {
        try {
            //get sensors state
            String jsonString = api.getSensorsState();
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

            System.out.println("Sensor presence state=" + sensorData.state.presence);
            System.out.println("Sensor name=" + sensorData.name);
            System.out.println("Sensor productName=" + sensorData.productname);

            return true;
        } catch (
                Exception e) {
            System.out.println("Error while parsing sensor data:" + e.getMessage());
            return false;
        }
    }
}