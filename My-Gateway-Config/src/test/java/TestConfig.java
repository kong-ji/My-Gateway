import config.Config;
import org.junit.Before;
import org.junit.Test;
import service.ConfigCenterProcessor;
import service.impl.nacos.NacosConfigCenter;
import util.ConfigUtil;

import static constant.ConfigConstant.*;

public class TestConfig {

    Config config;

    @Before
    public void before() {
        this.config = ConfigUtil.loadConfigFromYaml(CONFIG_PATH, Config.class, CONFIG_PREFIX);
    }

    @Test
    public void testConfigLoad() {
        Config config = ConfigUtil.loadConfigFromYaml("gateway.yaml", Config.class, "my.gateway");
        System.out.println(config);
    }

    @Test
    public void testNacosConfig() {
        ConfigCenterProcessor processor = NacosConfigCenter.builder()
                                            .configCenter(config.getConfigCenter())
                                            .build();
        processor.subscribeRoutesChange(i -> {});
    }
}
