
import config.Config;
import helper.RouteResolver;
import loader.ConfigLoader;
import manager.DynamicConfigManager;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class TestResolveUri {

    @Test
    public void test() {
        Map<String, String> map = new HashMap<>();
        map.put("/api/user/**", "user");
        map.put("/api/user/register/**", "user.register");
        map.put("/api/user/login/**", "user.login");
        map.put("/api/order/**", "order");

        String uri = "/api/user/register/hello";

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String regex = entry.getKey().replace("**", ".*");

            if (Pattern.matches(regex, uri)) {
                System.out.println(entry.getValue());
            }
        }

    }

    @Test
    public void testApi() {
        Config config = ConfigLoader.load(null);
        DynamicConfigManager.getInstance().updateRoutes(config.getRoutes());
        //失败Case
//        System.out.println(RouteResolver.matchingRouteByUri("/user/register"));
//        System.out.println(RouteResolver.matchingRouteByUri("/order1/hello"));
//        System.out.println(RouteResolver.matchingRouteByUri("/order2/cancel/hello"));

        //成功Case
        System.out.println(RouteResolver.matchingRouteByUri("/user/register"));
        System.out.println(RouteResolver.matchingRouteByUri("/order/hello"));
        System.out.println(RouteResolver.matchingRouteByUri("/order/cancel/hello"));
    }

}
