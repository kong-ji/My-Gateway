package filter.loadbalance.strategy;

import context.GatewayContext;
import pojo.ServiceInstance;

import java.util.List;

import static constant.LoadBalanceConstant.GRAY_LOAD_BALANCE_STRATEGY;


/**
 * 灰度发布负载均衡策略
 * 确保同一客户端的请求被路由到相同版本的服务实例
 */
public class GrayLoadBalanceStrategy implements LoadBalanceStrategy {

    @Override
    public ServiceInstance selectInstance(GatewayContext context, List<ServiceInstance> instances) {
        int totalThreshold = (int) (instances.stream().mapToDouble(ServiceInstance::getThreshold).sum() * 100);
        if (totalThreshold <= 0) return null;

        int randomThreshold = Math.abs(context.getRequest().getHost().hashCode()) % totalThreshold;
        for (ServiceInstance instance : instances) {
            randomThreshold -= instance.getThreshold();
            if (randomThreshold < 0) return instance;
        }
        return null;
    }

    @Override
    public String mark() {
        return GRAY_LOAD_BALANCE_STRATEGY;
    }

}