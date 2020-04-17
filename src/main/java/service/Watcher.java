package service;

import com.google.gson.Gson;
import model.alertdatabaseobject.AlertDataBaseObject;
import model.huesensordata.HueIndividualSensorData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Watcher {
    private NatsManager natsManager;
    private List<Integer> listOfSensorsIdToWatch;
    private List<Thread> watcherThreadPool;
    private boolean state = false;

    public Watcher(NatsManager natsManager) {
        this.natsManager = natsManager;
        this.listOfSensorsIdToWatch = new ArrayList<>();
        this.watcherThreadPool = new ArrayList<>();
    }

    public void addSensorToWatch(List<Integer> listOfId) {
        listOfSensorsIdToWatch.addAll(listOfId);
    }

    public void addSensorToWatch(Integer id) {
        listOfSensorsIdToWatch.add(id);

        System.out.println("list of ids looks like this: " + listOfSensorsIdToWatch);

        if (listOfSensorsIdToWatch.size() == 1)
            this.start();
    }

    public void removeSensorToWatch(Integer id) {
        listOfSensorsIdToWatch.remove(id);

        if (listOfSensorsIdToWatch.size() == 0)
            this.stop();
    }

    public boolean start() {
        state = true;

        System.out.println("Starting watcher ...");

        try {
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
                System.out.println("Starting thread " + thread.getName());
                thread.start();
            }
        } catch (Exception e) {
            System.out.println("Exception while starting watcher threads " + e);
        }

        return state;
    }

    public boolean stop() {
        System.out.println("Stopping watcher ...");
        state = false;

        for (Thread thread : watcherThreadPool) {
            System.out.println("Stopping watcher thread " + thread.getName());

            try {
                thread.interrupt();
            } catch (Exception e) {
                System.out.println("Exception while trying to stop " + thread.getName());
            }

        }

        watcherThreadPool.clear();

        return state;
    }

    private void SensorWatcherThreadLoop(Integer id) throws InterruptedException {
        while (true) {
            try {

                LinkedList<AlertDataBaseObject> newAlertMap = checkSensorsState();

                if (newAlertMap != null && newAlertMap.size() > 0) {
                    System.out.println("Alertmap size:" + newAlertMap.size());

                    JSONArray jsonArray = new JSONArray();

                    for (AlertDataBaseObject alertDataBaseObject : newAlertMap) {
                        System.out.println("Putting alert in JSONARRAY:" + alertDataBaseObject.toString());
                        jsonArray.put(alertDataBaseObject);
                    }

                    String jsonString = new Gson().toJson(jsonArray);

                    notifyAlert(jsonString);
                }

                Thread.sleep(2000);
            }
            catch ( IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyAlert(String jsonString) {
        try {
            natsManager.publish("hue.alert.motion.new", jsonString);
        } catch (Exception e) {
            System.out.println("Exception while trying to publish notification event: " + e);
        }
    }

    private LinkedList<AlertDataBaseObject> checkSensorsState() throws IOException {
        try {
            //get sensors state
            String jsonString = natsManager.publishRequestAndWaitForResponse("hue.sensor.get.all", "");
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
            String jsonString = natsManager.publishRequestAndWaitForResponse("hue.sensor.get.all", "");
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