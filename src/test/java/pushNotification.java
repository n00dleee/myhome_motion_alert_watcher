import org.junit.Assert;
import org.junit.Test;
import service.api.Api;

public class pushNotification {
    @Test
    public void pushNotification() throws Exception {
        Api api = new Api();

        boolean result = api.pushNotification("{\"payload\":\"bonjour\"}");
        Assert.assertTrue(result);
    }
}