package helper;

import manager.DynamicConfigManager;
import pojo.RouteDefinition;

/**
 * 路由解析器
 * 负责根据URI匹配对应的路由定义
 */
public class RouteResolver {

    private static final DynamicConfigManager manager = DynamicConfigManager.getInstance();

    /**
     * 根据uri解析出对应的路由
     */
    public static RouteDefinition matchingRouteByUri(String uri) {
        return null;
    }

}