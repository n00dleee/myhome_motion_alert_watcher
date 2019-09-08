import org.junit.Assert;
import org.junit.Test;
import service.watcher.Watcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class startWatcherThreadLoop {
    @Test
    public void startWatcherThreadLoop() throws Exception {
        System.out.println("TEST-instanciating watcher");
        Watcher watcher = new Watcher();

        System.out.println("TEST-adding watching id=18 to list");
        ArrayList<Integer> sensorsToWatch = new ArrayList<>();
        sensorsToWatch.add(18);
        watcher.addSensorToWatch(sensorsToWatch);

        System.out.println("TEST-starting watcher");
        watcher.start();

        System.out.println("TEST-GO MOVE IN FRONT OF THE SENSOR(S) !!!!!!");

        System.out.println("TEST-sleeping");
        Thread.sleep(30000);

        System.out.println("TEST-get alert map");
        Map<Integer, Date> alertMap = watcher.getAlertMap();

        System.out.println("TEST-assert alert map is not empty");
        Assert.assertTrue(alertMap.size() > 0);
    }
}