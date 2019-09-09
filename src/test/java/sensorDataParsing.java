import org.junit.Assert;
import org.junit.Test;
import service.mqtt.MqttManager;
import service.watcher.Watcher;

public class sensorDataParsing {
    @Test
    public void sensorDataParsing() throws Exception {
        MqttManager mqttManager = new MqttManager();
        Watcher watcher = new Watcher(mqttManager);

        Assert.assertTrue(watcher.checkSensorState(18));
    }
}