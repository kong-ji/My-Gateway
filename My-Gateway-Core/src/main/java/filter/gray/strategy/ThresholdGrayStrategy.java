package filter.gray.strategy;

import context.GatewayContext;
import pojo.RouteDefinition;
import pojo.ServiceInstance;
import util.FilterUtil;

import java.util.List;

import static constant.FilterConstant.GRAY_FILTER_NAME;
import static constant.GrayConstant.MAX_GRAY_THRESHOLD;
import static constant.GrayConstant.THRESHOLD_GRAY_STRATEGY;

/**
 * 基于阈值的灰度策略实现
 * 根据随机数和配置的灰度阈值决定请求是否应该路由到灰度实例
 */
public class ThresholdGrayStrategy implements GrayStrategy {

    /**
     * 判断请求是否应该路由到灰度实例
     * 基于随机数和灰度阈值进行判断
     *
     * @param context 网关上下文，包含请求的所有信息
     * @param instances 可用的服务实例列表
     * @return 如果应该路由到灰度实例则返回true，否则返回false
     */
    @Override
    public boolean shouldRoute2Gray(GatewayContext context, List<ServiceInstance> instances) {
        // 检查是否存在非灰度实例，如果没有，则所有请求都路由到灰度实例
        if (instances.stream().anyMatch(instance -> instance.isEnabled() && !instance.isGray())) {
            // 获取灰度过滤器配置
            RouteDefinition.GrayFilterConfig grayFilterConfig = FilterUtil.findFilterConfigByClass(context.getRoute().getFilterConfigs(), GRAY_FILTER_NAME, RouteDefinition.GrayFilterConfig.class);
            
            // 获取最大灰度阈值，如果配置不存在，则使用默认值
            double maxGrayThreshold = grayFilterConfig == null ? MAX_GRAY_THRESHOLD : grayFilterConfig.getMaxGrayThreshold();
            
            // 计算灰度阈值，即所有灰度实例的阈值总和
            double grayThreshold = instances.stream().mapToDouble(ServiceInstance::getThreshold).sum();
            
            // 确保灰度阈值不超过配置的最大灰度阈值
            grayThreshold = Math.min(grayThreshold, maxGrayThreshold);
            
            // 生成0-1之间的随机数，如果该值小于等于灰度阈值，则路由到灰度实例
            return Math.abs(Math.random() - 1) <= grayThreshold;
        }
        // 如果没有非灰度实例，则所有请求都路由到灰度实例
        return true;
    }

    /**
     * 获取策略的标识
     * 
     * @return 阈值灰度策略的唯一标识
     */
    @Override
    public String mark() {
        return THRESHOLD_GRAY_STRATEGY;
    }

}
