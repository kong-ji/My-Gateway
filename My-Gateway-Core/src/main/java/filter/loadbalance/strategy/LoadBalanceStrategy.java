package filter.loadbalance.strategy;

import context.GatewayContext;
import pojo.ServiceInstance;

import java.util.List;

/**
 * 负载均衡策略接口
 * 该接口的实现类提供了不同的算法，用于根据各种标准从可用实例列表中选择一个服务实例。
 */
public interface LoadBalanceStrategy {

    /**
     * 根据策略算法从提供的列表中选择一个服务实例
     *
     * @param context   包含请求信息的网关上下文
     * @param instances 可供选择的服务实例列表
     * @return 选中的服务实例
     */
    ServiceInstance selectInstance(GatewayContext context, List<ServiceInstance> instances);

    /**
     * 返回此负载均衡策略的唯一标识符
     *
     * @return 策略的字符串标识符
     */
    String mark();

}