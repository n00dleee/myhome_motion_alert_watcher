package service;

import io.nats.client.Message;
import model.responses.ResponsesJson;
import org.json.JSONException;
import org.json.JSONObject;

public class Controler {
    public NatsManager natsManager;
    private Watcher watcher;

    public Controler() throws Exception {
        this.natsManager = new NatsManager() {
            @Override
            public void handleIncomingMessage(Message msg) {
                handleMessage(msg);
            }
        };

        this.watcher = new Watcher(natsManager);
        this.watcher.start();

        natsManager.asyncSubscribe("alert.motion.watcher.set");
    }

    private void handleMessage(Message msg) {
        System.out.println("Message on topic " + msg.getSubject() + " received: " + new String(msg.getData()));
        try {
            String rsp;
            switch (msg.getSubject()) {
                case "alert.motion.watcher.set":
                    String incomingJson = new String(msg.getData());
                    try {
                        JSONObject jsonObject = new JSONObject(incomingJson);
                        setWatcherState(jsonObject.getString("id"), jsonObject.getString("state"));
                        natsManager.publish(msg.getReplyTo(), ResponsesJson.okResponse);
                    } catch (JSONException err) {
                        System.out.println(err.toString());
                    }
                    break;
                default:
                    System.out.println("Message subject handling not implemented yet");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setWatcherState(String id, String state) {
        if (state.equals("true"))
            watcher.addSensorToWatch(Integer.parseInt(id));
        else
            watcher.removeSensorToWatch(Integer.parseInt(id));
    }
}
