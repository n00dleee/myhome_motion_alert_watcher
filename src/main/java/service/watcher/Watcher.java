package watcher;

import org.eclipse.paho.client.mqttv3.MqttException;
import service.api.HueApi;
import service.mqtt.MqttManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Watcher {
    HueApi hueApi;
    MqttManager mqttManager;
    List<String> listOfSensorsIdToWatch;

    public Watcher() throws IOException, MqttException {
        listOfSensorsIdToWatch = new ArrayList<>();
        hueApi = new HueApi();
        mqttManager = new MqttManager();

    }

    public start(){

    }

    public stop(){

    }
}
