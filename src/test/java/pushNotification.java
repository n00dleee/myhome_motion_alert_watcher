import org.junit.Assert;
import org.junit.Test;
import service.api.HueApi;

public class pushNotification {
    @Test
    public void pushNotification() throws Exception {
        HueApi api = new HueApi();

        boolean result = api.pushNotification("{\"payload\":\"bonjour\"}");
        Assert.assertTrue(result);
    }
}