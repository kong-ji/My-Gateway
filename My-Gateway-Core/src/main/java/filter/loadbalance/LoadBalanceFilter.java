package filter.loadbalance;


import context.GatewayContext;
import filter.Filter;
import manager.DynamicConfigManager;
import pojo.ServiceInstance;

import java.util.List;

import static constant.FilterConstant.LOAD_BALANCE_FILTER_NAME;
import static constant.FilterConstant.LOAD_BALANCE_FILTER_ORDER;

/**
 * 负载均衡过滤器
 * 支持多种负载均衡策略，如轮询、随机、权重等
 *
 */
public class LoadBalanceFilter implements Filter {

    @Override
    public void doFilter(GatewayContext context) {
        // 获取服务所有实例
        List<ServiceInstance> instances = DynamicConfigManager.getInstance()
                .getInstancesByServiceName(context.getRequest().getServiceDefinition().getServiceName())
                .values().stream().toList();
    }

    @Override
    public String mark() {
        return LOAD_BALANCE_FILTER_NAME;
    }

    @Override
    public int getOrder() {
        return LOAD_BALANCE_FILTER_ORDER;
    }
}
