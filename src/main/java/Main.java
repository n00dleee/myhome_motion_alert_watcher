import org.eclipse.paho.client.mqttv3.MqttException;
import service.mqtt.MqttManager;
import service.watcher.Watcher;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, MqttException {
        MqttManager mqttManager = new MqttManager();
        Watcher watcher = new Watcher(mqttManager);
        watcher.addSensorToWatch(18);
        watcher.start();
    }
}