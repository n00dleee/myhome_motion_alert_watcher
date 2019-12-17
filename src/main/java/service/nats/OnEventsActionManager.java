package service.nats;

import io.nats.client.Message;
import org.json.JSONObject;
import service.watcher.Watcher;

import java.text.Normalizer;

public class OnEventsActionManager {
    private String[] topicToSubcribe = {"service.setState", "service.getState", "alarm.set.state"};
    private NatsManager natsManager;
    private Watcher watcher;

    private OnEventsActionManager(Watcher watcher) throws Exception {
        natsManager = new NatsManager() {
            @Override
            public void handleIncomingMessage(Message msg) {
                incomingMessageHandler(msg);
            }
        };

        subscribe();

        this.watcher = watcher;
    }

    private static OnEventsActionManager INSTANCE;
    static {
        try {
            INSTANCE = new OnEventsActionManager(Watcher.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OnEventsActionManager getInstance() {
        return INSTANCE;
    }

    private void subscribe(){
        for (String topic : topicToSubcribe){
            natsManager.asyncSubscribe(topic);
        }
    }


    private void incomingMessageHandler(Message msg){
        System.out.println("Message has been received:\r\ntopic->" + msg.getSubject() + "\r\nmessage->" + new String(msg.getData()));

        switch (msg.getSubject()) {
            case "alarm.set.state":
                System.out.println("alarm.set.state has been triggered");
                JSONObject o = new JSONObject(new String(msg.getData()));
                onStateChanged(o.getBoolean("state"));

                new Thread(() -> {
                    try {
                        System.out.println("Publishing event on topic notification.sms.outgoing");
                        natsManager.publish("notification.sms.outgoing", buildAlertNotificationMessage(o.getBoolean("state")));
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }).start();
                break;
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
}
