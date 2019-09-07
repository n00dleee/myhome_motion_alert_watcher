import org.junit.Assert;
import org.junit.Test;
import service.watcher.Watcher;

public class startWatcherThreadLoop {
    @Test
    public void startWatcherThreadLoop() throws Exception {
        Watcher watcher = new Watcher();

         watcher.start();

         Thread.sleep(60000);

        Assert.assertTrue(true);
    }
}