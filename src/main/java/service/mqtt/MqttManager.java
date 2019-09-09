package service.mqtt;

import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MqttManager {
    private IMqttClient publisher;

    String[] topicToSubcribe = {"service.setState", "service.getState"};
    String mqttBrokerUrl = "tcp://127.0.0.1:1883";

    public MqttManager() {
        String publisherId = UUID.randomUUID().toString();

        try{
            System.out.println("Trying to instanciate MQTT client with uri= " + mqttBrokerUrl + " and publisher id=" + publisherId);
            publisher = new MqttClient(mqttBrokerUrl, publisherId);
            publisher.connect();
            subscribe(topicToSubcribe);
        }catch (Exception e){
            System.out.println("Error while instanciating MQTT client: " + e);
        }
    }

    public boolean subscribe(String[] topics) {
        try {
            publisher.subscribe(topics);
            return true;
        } catch (MqttException e) {
            return false;
        }
    }

    public boolean connect() throws MqttException {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        publisher.connect(options);

        return publisher.isConnected();
    }

    public boolean disconnect() throws MqttException {
        try {
            publisher.disconnect();
            return true;
        } catch (MqttException e) {
            return false;
        }
    }

    public boolean publish(String message, String topic) throws MqttException {
        System.out.println("Publishing message with content=" + message + "\r\non topic=" + topic);
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