package filter.loadbalance.strategy;

import context.GatewayContext;
import pojo.ServiceInstance;

import java.util.List;

import static constant.LoadBalanceConstant.CLIENT_IP_LOAD_BALANCE_STRATEGY;

/**
 * 客户端IP负载均衡策略
 * 根据客户端IP的哈希值选择服务实例，确保来自同一IP的请求总是被路由到同一个服务实例。
 *
 */
public class ClientIpLoadBalanceStrategy implements LoadBalanceStrategy{

    @Override
    public ServiceInstance selectInstance(GatewayContext context, List<ServiceInstance> instances) {
        // 使用客户端IP的哈希值对实例列表大小取模，确定选择哪个实例
        return instances.get(Math.abs(context.getRequest().getHost().hashCode()) % instances.size());
    }

    @Override
    public String mark() {
        return CLIENT_IP_LOAD_BALANCE_STRATEGY;
    }

}