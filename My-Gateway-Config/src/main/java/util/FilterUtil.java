package util;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import pojo.RouteDefinition;

import java.util.Collection;

import static constant.FilterConstant.GRAY_FILTER_NAME;
import static constant.FilterConstant.LOAD_BALANCE_FILTER_NAME;

/**
 * 过滤器工具类
 * 提供处理过滤器配置的工具方法
 * 包括查找过滤器配置、转换过滤器配置和构建默认过滤器配置
 */
public class FilterUtil {

    /**
     * 根据名称查找过滤器配置
     * 在过滤器配置集合中查找指定名称的过滤器配置
     *
     * @param filterConfigs 过滤器配置集合
     * @param name 过滤器名称
     * @return 找到的过滤器配置，如果未找到则返回null
     */
    public static RouteDefinition.FilterConfig findFilterConfigByName(Collection<RouteDefinition.FilterConfig> filterConfigs, String name) {
        // 参数校验，如果任何参数为空则返回null
        if (name == null || name.isEmpty() || filterConfigs == null || filterConfigs.isEmpty()) return null;
        // 遍历过滤器配置集合
        for (RouteDefinition.FilterConfig filterConfig : filterConfigs) {
            // 跳过空配置或名称为空的配置
            if (filterConfig == null || filterConfig.getName() == null) continue;
            // 比较名称，如果匹配则返回
            if (filterConfig.getName().equals(name)) {
                return filterConfig;
            }
        }
        // 未找到匹配的配置，返回null
        return null;
    }

    /**
     * 根据名称和类型查找并转换过滤器配置
     * 先查找指定名称的过滤器配置，然后将其配置转换为指定类型
     *
     * @param filterConfigs 过滤器配置集合
     * @param name 过滤器名称
     * @param clazz 目标类型
     * @param <T> 目标类型参数
     * @return 转换后的过滤器配置，如果未找到或转换失败则返回null
     */
    public static <T> T findFilterConfigByClass(Collection<RouteDefinition.FilterConfig> filterConfigs, String name, Class<T> clazz) {
        // 查找指定名称的过滤器配置
        RouteDefinition.FilterConfig filterConfig = findFilterConfigByName(filterConfigs, name);
        // 如果未找到，返回null
        if (filterConfig == null) return null;
        // 将配置转换为指定类型并返回
        return BeanUtil.toBean(filterConfig.getConfig(), clazz);
    }

    /**
     * 构建默认的灰度过滤器配置
     * 用于在没有配置灰度过滤器时提供默认配置
     *
     * @return 默认的灰度过滤器配置
     */
    public static RouteDefinition.FilterConfig buildDefaultGrayFilterConfig() {
        // 创建过滤器配置
        RouteDefinition.FilterConfig filterConfig = new RouteDefinition.FilterConfig();
        // 设置过滤器名称
        filterConfig.setName(GRAY_FILTER_NAME);
        // 启用过滤器
        filterConfig.setEnable(true);
        // 设置过滤器配置，使用默认的灰度过滤器配置
        filterConfig.setConfig(JSONUtil.toJsonStr(new RouteDefinition.GrayFilterConfig()));
        return filterConfig;
    }



}
