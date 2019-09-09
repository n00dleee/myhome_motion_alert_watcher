import org.junit.Test;
import service.mqtt.MqttManager;
import service.watcher.Watcher;

import java.util.ArrayList;

public class startWatcherThreadLoop {
    @Test
    public void startWatcherThreadLoop() throws Exception {
        System.out.println("TEST-instanciating watcher");
        Watcher watcher = new Watcher(new MqttManager());

        System.out.println("TEST-adding watching id=18 to list");
        ArrayList<Integer> sensorsToWatch = new ArrayList<>();
        sensorsToWatch.add(18);
        watcher.addSensorToWatch(sensorsToWatch);

        System.out.println("TEST-starting watcher");
        watcher.start();

        Thread.sleep(60000);
    }
}