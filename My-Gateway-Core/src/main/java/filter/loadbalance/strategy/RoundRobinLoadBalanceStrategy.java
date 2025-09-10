package filter.loadbalance.strategy;

import context.GatewayContext;
import pojo.RouteDefinition;
import pojo.ServiceInstance;
import util.FilterUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static constant.FilterConstant.LOAD_BALANCE_FILTER_NAME;
import static constant.LoadBalanceConstant.ROUND_ROBIN_LOAD_BALANCE_STRATEGY;

/**
 * 轮询负载均衡策略
 * 该策略按顺序依次选择服务实例，确保请求均匀分布到所有可用实例。
 *
 */
public class RoundRobinLoadBalanceStrategy implements LoadBalanceStrategy {

    /**
     * 严格轮询模式的位置映射，使用AtomicInteger保证线程安全
     */
    Map<String /*服务名*/, AtomicInteger> strictPositionMap = new ConcurrentHashMap<>();

    /**
     * 非严格轮询模式的位置映射
     */
    Map<String /*服务名*/, Integer> positionMap = new ConcurrentHashMap<>();

    /**
     * 阈值常量，用于防止计数器溢出
     */
    private final int THRESHOLD = Integer.MAX_VALUE >> 2; // 预防移除的安全阈值

    @Override
    public ServiceInstance selectInstance(GatewayContext context, List<ServiceInstance> instances) {
        boolean isStrictRoundRobin = true;
        RouteDefinition.LoadBalanceFilterConfig loadBalanceFilterConfig = FilterUtil.findFilterConfigByClass(context.getRoute().getFilterConfigs(), LOAD_BALANCE_FILTER_NAME, RouteDefinition.LoadBalanceFilterConfig.class);

        //加载配置文件的严格开启开关
        if (loadBalanceFilterConfig != null) {
            isStrictRoundRobin = loadBalanceFilterConfig.isStrictRoundRobin();
        }

        String serviceName = context.getRequest().getServiceDefinition().getServiceName();
        ServiceInstance serviceInstance;
        if (isStrictRoundRobin) {
            // 严格轮询模式，使用AtomicInteger保证线程安全
            AtomicInteger strictPosition = strictPositionMap.computeIfAbsent(serviceName, k -> new AtomicInteger(0));
            int index = Math.abs(strictPosition.getAndIncrement());
            serviceInstance = instances.get(index % instances.size());
            if (index >= THRESHOLD) {
                strictPosition.set((index + 1) % instances.size());
            }
        } else {
            // 非严格轮询模式，性能更好但不保证线程安全
            int position = positionMap.getOrDefault(serviceName, 0);
            int index = Math.abs(position++);
            serviceInstance = instances.get(index % instances.size());
            if (position >= THRESHOLD) {
                positionMap.put(serviceName, (position + 1) % instances.size());
            }
        }
        return serviceInstance;
    }

    @Override
    public String mark() {
        return ROUND_ROBIN_LOAD_BALANCE_STRATEGY;
    }

}
