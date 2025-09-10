package filter.loadbalance.strategy;

import context.GatewayContext;
import pojo.ServiceInstance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static constant.LoadBalanceConstant.WEIGHT_LOAD_BALANCE_STRATEGY;

/**
 * 权重负载均衡策略
 * 该策略基于服务实例的权重值进行选择，权重越高的实例被选中的概率越大。
 * 采用随机权重算法实现，确保实例被选中的概率与其权重成正比。
 */
public class WeightLoadBalanceStrategy implements LoadBalanceStrategy {

    @Override
    public ServiceInstance selectInstance(GatewayContext context, List<ServiceInstance> instances) {
        // 计算所有实例的总权重
        int totalWeight = instances.stream().mapToInt(ServiceInstance::getWeight).sum();
        if (totalWeight <= 0) return null;
        
        // 在总权重范围内随机选择一个点
        int randomWeight = ThreadLocalRandom.current().nextInt(totalWeight);
        
        // 遍历所有实例，按权重递减，当随机点落在某个实例的权重区间内时选中该实例
        for (ServiceInstance instance : instances) {
            randomWeight -= instance.getWeight();
            if (randomWeight < 0) return instance;
        }
        return null;
    }

    @Override
    public String mark() {
        return WEIGHT_LOAD_BALANCE_STRATEGY;
    }

}
