package service.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;
import service.watcher.Watcher;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.UUID;

public class MqttManager {
    private IMqttClient publisher;

    String[] topicToSubcribe = {"service.setState", "service.getState", "alarm.set.state"};
    String mqttBrokerUrl = "";
    Watcher watcher = Watcher.getInstance();

    private MqttManager() {
        initVarFromEnv();

        String publisherId = UUID.randomUUID().toString();

        try {
            System.out.println("Trying to instanciate MQTT client with uri= " + mqttBrokerUrl + " and publisher id=" + publisherId);
            publisher = new MqttClient(mqttBrokerUrl, publisherId);
            publisher.connect();
            subscribe(topicToSubcribe);
        } catch (Exception e) {
            System.out.println("Error while instanciating MQTT client: " + e);
        }
    }

    private static MqttManager INSTANCE = new MqttManager();

    public static MqttManager getInstance() {
        return INSTANCE;
    }

    private void initVarFromEnv() {
        if (System.getenv("LOCAL").equals("true")) {
            mqttBrokerUrl = "tcp://127.0.0.1:1883";
        } else {
            mqttBrokerUrl = "tcp://" + System.getenv("BROKER_URL") + ":1883";
        }
    }

    public boolean subscribe(String[] topics) {
        try {
            setCallBacks();
            publisher.subscribe(topics);
            return true;
        } catch (MqttException e) {
            return false;
        }
    }

    private void onStateChanged(Boolean state) {
        System.out.println("State changed to " + state.toString() );

        if (state){
            watcher.start();
        }
        else
            watcher.stop();
    }

    public void setCallBacks() {
        publisher.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("Inbound MQTT message on topic=" + topic);

                switch (topic) {
                    case "alarm.set.state":
                        System.out.println("alarm.set.state has been triggered");
                        JSONObject o = new JSONObject(new String(message.getPayload()));
                        onStateChanged(o.getBoolean("state"));

                        new Thread(() -> {
                            try {
                                System.out.println("Publishing event on topic notification.sms.outgoing");
                                publish(buildAlertNotificationMessage(o.getBoolean("state")), "notification.sms.outgoing");
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                        }).start();
                        break;
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private String buildAlertNotificationMessage(Boolean bool) {
        String s;

        if (bool)
            s = "INFO: Alarm has been turned ON";
        else
            s = "INFO: Alarm has been turned OFF";

        return stripAccents(s);
    }

    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
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
        try {
            publisher.publish(topic, buildMqttMessage(message));
            return true;
        } catch (MqttException e) {
            return false;
        }
    }

    private MqttMessage buildMqttMessage(String s) {
        return new MqttMessage(s.getBytes(StandardCharsets.UTF_8));
    }
}