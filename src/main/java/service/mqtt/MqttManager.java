package service.mqtt;

import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MqttManager {
    private IMqttClient publisher;

    String[] topicToSubcribe = {"service.setState"};


    public MqttManager() throws MqttException {
        String mqttBrokerUri = "localhost:1883";
        String publisherId = UUID.randomUUID().toString();

        try{
            publisher = new MqttClient(mqttBrokerUri, publisherId);
        }catch (Exception e){
            System.out.println("Error while instanciating MQTT client: " + e.getMessage());
        }
    }

    public boolean subscribe(String[] topics) throws MqttException {
        try {
            publisher.subscribe(topics);
            return true;
        } catch (MqttException e) {
            return false;
        }
    }

    public boolean subscribe(String topic) throws MqttException {
        try {
            publisher.subscribe(topic);
            return true;
        } catch (MqttException e) {
            return false;
        }
    }

    public boolean connect() throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
//        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        publisher.connect(options);

        return false;
    }

    public boolean disconnect() throws MqttException {
        try {
            publisher.disconnect();
            return true;
        } catch (MqttException e) {
            return false;
        }
    }

    public boolean sendMessage(String message, String topic) throws MqttException {
        try{
            publisher.publish(topic, buildMqttMessage(message));
            return true;
        }
        catch (MqttException e){
            return false;
        }
    }

    private MqttMessage buildMqttMessage(String s) {
        return new MqttMessage(s.getBytes(StandardCharsets.UTF_8));
    }
}