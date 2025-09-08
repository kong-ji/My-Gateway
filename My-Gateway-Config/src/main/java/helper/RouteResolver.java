package helper;

import enums.ResponseCode;
import exception.NotFoundException;
import manager.DynamicConfigManager;
import pojo.RouteDefinition;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 路由解析器
 * 负责根据URI匹配对应的路由定义
 */
public class RouteResolver {

    private static final DynamicConfigManager manager = DynamicConfigManager.getInstance();

    /**
     * 根据uri解析出对应的路由
     * 使用正则表达式匹配，支持通配符
     * 如果有多个匹配的路由，依次按照优先级和URI长度排序
     */
    public static RouteDefinition matchingRouteByUri(String uri) {
        // 获取所有路由定义
        Set<Map.Entry<String, RouteDefinition>> allUriEntry = manager.getAllUriEntry();

        // 存储匹配的路由
        List<RouteDefinition> matchedRoute = new ArrayList<>();

        for (Map.Entry<String, RouteDefinition> entry : allUriEntry) {
            // 将路由模式中的通配符转换为正则表达式
            String regex = entry.getKey().replace("**", ".*");
            // 使用正则表达式匹配URI
            if (Pattern.matches(regex, uri)) {
                matchedRoute.add(entry.getValue());
            }
        }

        // 如果没有匹配的路由，抛出未找到异常
        if (matchedRoute.isEmpty()) {
            throw new NotFoundException(ResponseCode.PATH_NO_MATCHED);
        }

        // 如果优先级相同，选择URI模式最长的路由（更具体的匹配）
        return matchedRoute.stream()
                .min(Comparator.comparingInt(RouteDefinition::getOrder)
                        .thenComparing(route -> route.getUri().length(), Comparator.reverseOrder()))
                .orElseThrow();
    }

}