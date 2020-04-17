import io.nats.client.Message;
import org.junit.Test;
import service.NatsManager;

public class pushNotification {
    @Test
    public void pushNotification() throws Exception {
        NatsManager natsManager = new NatsManager() {
            @Override
            public void handleIncomingMessage(Message msg) {

            }
        };

        natsManager.publish("hue.alert.motion.new", "{\"myArrayList\":[{\"sensorId\":18,\"name\":\"capteur Entree\",\"productName\":\"Hue motion sensor\",\"manufacturerName\":\"Signify Netherlands B.V.\",\"date\":\"Apr 17, 2020, 12:11:39 AM\",\"type\":\"presence\",\"value\":true}]}\n");
    }
}