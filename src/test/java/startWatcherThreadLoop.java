import org.junit.Assert;
import org.junit.Test;
import service.watcher.Watcher;

import java.util.ArrayList;

public class startWatcherThreadLoop {
    @Test
    public void startWatcherThreadLoop() throws Exception {
        System.out.println("TEST-instanciating watcher");
        Watcher watcher = new Watcher();

        System.out.println("TEST-adding watching id=18 to list");
        ArrayList<Integer> sensorsToWatch = new ArrayList<Integer>();
        sensorsToWatch.add(18);
        watcher.addSensorToWatch(sensorsToWatch);

        System.out.println("TEST-starting watcher");
        watcher.start();

        System.out.println("TEST-sleeping");
        Thread.sleep(60000);

        System.out.println("TEST-assert");
        Assert.assertTrue(true);
    }
}