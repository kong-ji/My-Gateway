package filter.loadbalance.strategy;



import algorithm.ConsistentHashing;
import context.GatewayContext;
import pojo.RouteDefinition;
import pojo.ServiceInstance;
import util.FilterUtil;

import java.util.List;

import static constant.FilterConstant.LOAD_BALANCE_FILTER_NAME;
import static constant.LoadBalanceConstant.CLIENT_IP_CONSISTENT_HASH_LOAD_BALANCE_STRATEGY;


/**
 * 客户端IP一致性哈希负载均衡策略
 *
 */
public class ClientIpConsistentHashLoadBalanceStrategy implements LoadBalanceStrategy {

    @Override
    public ServiceInstance selectInstance(GatewayContext context, List<ServiceInstance> instances) {
        // 获取负载均衡配置，提取虚拟节点数量
        RouteDefinition.LoadBalanceFilterConfig loadBalanceFilterConfig = FilterUtil.findFilterConfigByClass(context.getRoute().getFilterConfigs(), LOAD_BALANCE_FILTER_NAME, RouteDefinition.LoadBalanceFilterConfig.class);
        int virtualNodeNum = 1;
        if (loadBalanceFilterConfig != null && loadBalanceFilterConfig.getVirtualNodeNum() > 0) {
            virtualNodeNum = loadBalanceFilterConfig.getVirtualNodeNum();
        }

        // 提取所有实例ID作为哈希环上的节点
        List<String> nodes = instances.stream().map(ServiceInstance::getInstanceId).toList();
        // 创建一致性哈希环
        ConsistentHashing consistentHashing = new ConsistentHashing(nodes, virtualNodeNum);
        // 根据客户端IP哈希值选择节点
        String selectedNode = consistentHashing.getNode(String.valueOf(context.getRequest().getHost().hashCode()));

        // 查找选中节点对应的服务实例
        for (ServiceInstance instance : instances) {
            if (instance.getInstanceId().equals(selectedNode)) {
                return instance;
            }
        }

        // 如果没有找到匹配的实例（理论上不应该发生），返回第一个实例
        return instances.get(0);
    }

    @Override
    public String mark() {
        return CLIENT_IP_CONSISTENT_HASH_LOAD_BALANCE_STRATEGY;
    }

}
