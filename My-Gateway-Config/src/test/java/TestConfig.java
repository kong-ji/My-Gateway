import config.Config;
import org.junit.Test;
import util.ConfigUtil;

public class TestConfig {

    @Test
    public void testConfigLoad() {
        Config config = ConfigUtil.loadConfigFromYaml("gateway.yaml", Config.class, "my.gateway");
        System.out.println(config);
    }

}
