package service.nats;

import io.nats.client.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class NatsManager {
    Connection nc;
    Options o;
    Map<String, Dispatcher> dispatcherMap;

    public NatsManager() throws Exception {
        o = buildOptions();
        nc = Nats.connect("nats://" + System.getenv("NATS_BROKER_URL") + ":" + System.getenv("NATS_BROKER_PORT"));
        dispatcherMap = new HashMap<>();

        if(nc.getStatus() != Connection.Status.CONNECTED){
            throw new Exception("Error while connecting to nats broker. Broker status: " + nc.getStatus().toString());
        }
    }

    public Options buildOptions() {
        return new Options.Builder().server("nats://" + System.getenv("NATS_BROKER_URL") + ":" + System.getenv("NATS_BROKER_PORT")).build();
    }

    public boolean publish(String topic, String msg) {
        try {
            nc.publish(topic, msg.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean publishWithReplyTo(String topic, String topicToReplyTo, String msg) {
        try {
            nc.publish(topic, topicToReplyTo, msg.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String publishRequestAndWaitForResponse(String topic, String msg) throws InterruptedException, ExecutionException, TimeoutException {
        Future<Message> incoming = nc.request(topic, msg.getBytes(StandardCharsets.UTF_8));
        Message rsp = incoming.get(2000, TimeUnit.MILLISECONDS);
        return new String(rsp.getData(), StandardCharsets.UTF_8);
    }

    public boolean asyncSubscribe(String topic) {
        if (!dispatcherMap.containsKey(topic)) {
            try {
                Dispatcher d = nc.createDispatcher(this::handleIncomingMessage);

                d.subscribe(topic);

                dispatcherMap.put(topic, d);
            } catch (Exception e) {
                System.out.println("Error while trying to subscribe to topic " + topic + "\r\nError:" + e.getMessage());
                return false;
            }
        }
        return true;
    }

    public abstract void handleIncomingMessage(Message msg);

//    Msg data management example
//    String rspString = new String(msg.getData(), StandardCharsets.UTF_8);
//    String topic = msg.getSubject();
}
