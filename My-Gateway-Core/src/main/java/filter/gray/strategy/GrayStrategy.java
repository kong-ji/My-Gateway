package filter.gray.strategy;

import context.GatewayContext;
import pojo.ServiceInstance;
import java.util.List;

/**
 * 灰度策略接口
 * 定义了灰度发布的策略行为
 * 不同的灰度策略决定请求是否应该被路由到灰度实例
 */
public interface GrayStrategy {

    /**
     * 判断请求是否应该路由到灰度实例
     * 根据请求上下文和可用的服务实例，决定是否使用灰度实例处理请求
     * 
     * @param context 网关上下文，包含请求的所有信息
     * @param instances 可用的服务实例列表
     * @return 如果应该路由到灰度实例则返回true，否则返回false
     */
    boolean shouldRoute2Gray(GatewayContext context, List<ServiceInstance> instances);

    /**
     * 获取策略的标识
     * 用于唯一标识一个灰度策略
     * 
     * @return 策略的唯一标识字符串
     */
    String mark();

}
