package filter.loadbalance.strategy;

import context.GatewayContext;
import pojo.ServiceInstance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static constant.LoadBalanceConstant.RANDOM_LOAD_BALANCE_STRATEGY;

/**
 * 随机负载均衡策略
 * 该策略从可用的服务实例列表中随机选择一个实例，每个实例被选中的概率相等。
 *
 */
public class RandomLoadBalanceStrategy implements LoadBalanceStrategy {

    @Override
    public ServiceInstance selectInstance(GatewayContext context, List<ServiceInstance> instances) {
        return instances.get(ThreadLocalRandom.current().nextInt(instances.size()));
    }

    @Override
    public String mark() {
        return RANDOM_LOAD_BALANCE_STRATEGY;
    }

}