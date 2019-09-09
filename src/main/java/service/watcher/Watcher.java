package service.watcher;

import com.google.gson.Gson;
import model.alertdatabaseobject.AlertDataBaseObject;
import model.huesensordata.HueIndividualSensorData;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;
import service.api.Api;
import service.mqtt.MqttManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Watcher {
    Api api;
    MqttManager mqttManager;
    List<Integer> listOfSensorsIdToWatch;
    public List<Thread> watcherThreadPool;
    private boolean state = false;

    public Watcher(MqttManager mqttManager) throws IOException, MqttException {
        listOfSensorsIdToWatch = new ArrayList<>();
        api = new Api();
        this.mqttManager = mqttManager;

        watcherThreadPool = new ArrayList<>();
    }

    public void addSensorToWatch(List<Integer> listOfId) {
        listOfSensorsIdToWatch.addAll(listOfId);
    }

    public void addSensorToWatch(Integer id) {
        listOfSensorsIdToWatch.add(id);
    }

    public boolean start() {
        state = true;

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

    private void SensorWatcherThreadLoop(Integer id) throws InterruptedException {
        while (true) {
            try {

                LinkedList<AlertDataBaseObject> newAlertMap = checkSensorsState();

                if (newAlertMap != null && newAlertMap.size() > 0) {
                    System.out.println("Alertmap size:" + newAlertMap.size());

                    Gson gson = new Gson();
                    List<String> linkedListAlertString = new ArrayList<>();
                    String temp;

                    for (int i = 0; i < newAlertMap.size(); i++) {
                        temp = gson.toJson(newAlertMap.get(0));
                        System.out.println("Putting alert in LinkedList:" + temp);
                        linkedListAlertString.add(temp);
                    }

                    String jsonString = new Gson().toJson(linkedListAlertString);

                    notifyAlert(jsonString);
                }

                Thread.sleep(2000);
            } catch (InterruptedException | IOException e) {
                System.out.println("Thread loop crashed: " + e.getMessage());
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyAlert(String jsonString) throws MqttException {
        mqttManager.publish(jsonString, "alert.motion.set.inbound");
    }

    private LinkedList<AlertDataBaseObject> checkSensorsState() throws IOException {
        try {
            //get sensors state
            String jsonString = api.getSensorsState();
            JSONObject jsonObject;
            JSONObject sensorJsonObject;

            LinkedList<AlertDataBaseObject> newAlertMap = new LinkedList<>();

            for (Integer id : listOfSensorsIdToWatch) {
                jsonObject = new JSONObject(jsonString);
                sensorJsonObject = jsonObject.getJSONObject(id.toString());

                Gson g = new Gson();
                HueIndividualSensorData sensorData = g.fromJson(sensorJsonObject.toString(), HueIndividualSensorData.class);

                if (sensorData.state.presence) {
                    newAlertMap.addLast(new AlertDataBaseObject(
                            id,
                            sensorData.name,
                            sensorData.productname,
                            sensorData.manufacturername,
                            new Date(),
                            "presence",
                            sensorData.state.presence
                    ));
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