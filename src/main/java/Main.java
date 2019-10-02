import org.eclipse.paho.client.mqttv3.MqttException;
import service.mqtt.MqttManager;
import service.watcher.Watcher;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, MqttException {
        MqttManager mqttManager = MqttManager.getInstance();
        Watcher watcher = Watcher.getInstance();

        watcher.addSensorToWatch(18);
    }
}