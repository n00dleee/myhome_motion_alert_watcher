import io.nats.client.Message;
import org.junit.Assert;
import org.junit.Test;
import service.Controler;
import service.NatsManager;

public class startWatcherThreadLoop {
    @Test
    public void startWatcherThreadLoop() throws Exception {
        final Integer[] nbDetections = {0};
        Controler controler = new Controler();
        NatsManager natsManager = new NatsManager() {
            @Override
            public void handleIncomingMessage(Message msg) {
                System.out.println("TEST NATS MNGR: receiving incoming msg: " + new String(msg.getData()));
                nbDetections[0]++;
            }
        };

        natsManager.asyncSubscribe("hue.alert.motion.new");

        natsManager.publish("alert.motion.watcher.set", "{\"id\":\"18\", \"state\":\"true\"}");

        Thread.sleep(60000);

        System.out.println("Number of detections: " + nbDetections[0]);
        Assert.assertTrue(nbDetections[0] > 0);
    }
}