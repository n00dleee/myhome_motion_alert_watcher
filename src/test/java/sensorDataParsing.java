import org.junit.Assert;
import org.junit.Test;
import service.watcher.Watcher;

public class sensorDataParsing {
    @Test
    public void sensorDataParsing() throws Exception {
        Watcher watcher = new Watcher();

        Assert.assertTrue(watcher.checkSensorState(18));
    }
}