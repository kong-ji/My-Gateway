package filter.loadbalance;


import cn.hutool.json.JSONUtil;
import context.GatewayContext;
import enums.ResponseCode;
import exception.NotFoundException;
import filter.Filter;
import filter.loadbalance.strategy.GrayLoadBalanceStrategy;
import filter.loadbalance.strategy.LoadBalanceStrategy;
import manager.DynamicConfigManager;
import pojo.RouteDefinition;
import pojo.ServiceInstance;
import util.FilterUtil;

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
    public void doPreFilter(GatewayContext context) {
        // 获取负载均衡策略
        RouteDefinition.FilterConfig filterConfig = FilterUtil.findFilterConfigByName(context.getRoute().getFilterConfigs(), LOAD_BALANCE_FILTER_NAME);
        if (filterConfig == null) {
            filterConfig = FilterUtil.buildDefaultLoadBalanceFilterConfig();
        }

        // 获取服务所有实例
        List<ServiceInstance> instances = DynamicConfigManager.getInstance()
                .getInstancesByServiceName(context.getRequest().getServiceDefinition().getServiceName())
                .values().stream().toList();

        LoadBalanceStrategy strategy;
        if (context.getRequest().isGray()) {
            strategy = new GrayLoadBalanceStrategy(); // 灰度负载均衡策略
            // 如果请求是灰度的，再进行一遍过滤
            instances = instances.stream().filter(instance -> instance.isEnabled() && instance.isGray()).toList();
        } else {
            strategy = selectLoadBalanceStrategy(JSONUtil.toBean(filterConfig.getConfig(), RouteDefinition.LoadBalanceFilterConfig.class));
        }

        if (instances.isEmpty()) {
            throw new NotFoundException(ResponseCode.SERVICE_INSTANCE_NOT_FOUND);
        }
        ServiceInstance serviceInstance = strategy.selectInstance(context, instances);
        if (null == serviceInstance) {
            throw new NotFoundException(ResponseCode.SERVICE_INSTANCE_NOT_FOUND);
        }
        context.getRequest().setModifyHost(serviceInstance.getIp() + ":" + serviceInstance.getPort());

    }

    @Override
    public void doPostFilter(GatewayContext context) {

    }

    @Override
    public String mark() {
        return LOAD_BALANCE_FILTER_NAME;
    }

    @Override
    public int getOrder() {
        return LOAD_BALANCE_FILTER_ORDER;
    }

    /**
     * 根据配置选择负载均衡策略
     *
     * @param loadBalanceFilterConfig 负载均衡过滤器配置
     * @return 负载均衡策略
     */
    private LoadBalanceStrategy selectLoadBalanceStrategy(RouteDefinition.LoadBalanceFilterConfig loadBalanceFilterConfig) {
        // 使用策略管理器获取指定名称的负载均衡策略
        return LoadBalanceStrategyManager.getStrategy(loadBalanceFilterConfig.getStrategyName());
    }
}
