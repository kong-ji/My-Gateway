

import config.Config;
import loader.ConfigLoader;
import org.junit.Test;
import service.impl.nacos.NacosRegisterCenter;

public class TestRegister {

    @Test
    public void testRegister() {
        Config config = ConfigLoader.load(null);
        new NacosRegisterCenter(config);
        while(true) {}
    }

    @Test
    public void testRegisterSub() {
        Config config = ConfigLoader.load(null);
        NacosRegisterCenter center = new NacosRegisterCenter(config);
        center.subscribeServiceChange(((serviceDefinition, newInstances) -> {
            System.out.println(serviceDefinition);
            System.out.println(newInstances);
        }));
        while(true) {}
    }

}
