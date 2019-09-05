package service.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.UUID;

public class MqttManager {
    final String publisherId = UUID.randomUUID().toString();
    final String mqttBrokerUri = "localhost:1883";

    IMqttClient publisher;

    public MqttManager() throws MqttException {
        publisher = new MqttClient(mqttBrokerUri, publisherId);
    }

    public boolean connect() throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
//        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        publisher.connect(options);
    }


}
