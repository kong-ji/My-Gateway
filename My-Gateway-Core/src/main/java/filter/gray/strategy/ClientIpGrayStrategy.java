package filter.gray.strategy;

import context.GatewayContext;
import pojo.RouteDefinition;
import pojo.ServiceInstance;
import util.FilterUtil;

import java.util.List;

import static constant.FilterConstant.GRAY_FILTER_NAME;
import static constant.GrayConstant.CLIENT_IP_GRAY_STRATEGY;

/**
 * 基于客户端IP的灰度策略实现
 *
 */
public class ClientIpGrayStrategy implements GrayStrategy {

    /**
     * 判断请求是否应该路由到灰度实例
     * 基于客户端IP的哈希值和灰度阈值进行判断
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
            
            // 计算灰度阈值，即所有灰度实例的阈值总和
            double grayThreshold = instances.stream().mapToDouble(ServiceInstance::getThreshold).sum();
            
            // 确保灰度阈值不超过配置的最大灰度阈值
            grayThreshold = Math.min(grayThreshold, grayFilterConfig.getMaxGrayThreshold());
            
            // 将客户端主机的哈希值取模100得到0-99的值，如果该值小于等于阈值的百分比，则路由到灰度实例
            return Math.abs(context.getRequest().getHost().hashCode()) % 100 <= grayThreshold * 100;
        }
        // 如果没有非灰度实例，则所有请求都路由到灰度实例
        return true;
    }

    /**
     * 获取策略的标识
     * 
     * @return 客户端IP灰度策略的唯一标识
     */
    @Override
    public String mark() {
        return CLIENT_IP_GRAY_STRATEGY;
    }

}
