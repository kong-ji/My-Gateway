package filter.gray;

import cn.hutool.json.JSONUtil;
import context.GatewayContext;
import filter.Filter;
import filter.gray.strategy.GrayStrategy;
import manager.DynamicConfigManager;
import pojo.RouteDefinition;
import pojo.ServiceInstance;
import util.FilterUtil;

import java.util.List;

import static constant.FilterConstant.GRAY_FILTER_NAME;
import static constant.FilterConstant.GRAY_FILTER_ORDER;

public class GrayFilter implements Filter {

    @Override
    public void doPreFilter(GatewayContext context) {
        RouteDefinition.FilterConfig filterConfig = FilterUtil.findFilterConfigByName(context.getRoute().getFilterConfigs(), GRAY_FILTER_NAME);
        if (filterConfig == null) {
            filterConfig = FilterUtil.buildDefaultGrayFilterConfig();
        }
        if (!filterConfig.isEnable()) {
            return;
        }

        // 获取服务所有实例
        List<ServiceInstance> instances = DynamicConfigManager.getInstance()
                .getInstancesByServiceName(context.getRequest().getServiceDefinition().getServiceName())
                .values().stream().toList();

        if (instances.stream().anyMatch(instance -> instance.isEnabled() && instance.isGray())) {
            // 存在灰度实例
            GrayStrategy strategy = selectGrayStrategy(JSONUtil.toBean(filterConfig.getConfig(), RouteDefinition.GrayFilterConfig.class));
            context.getRequest().setGray(strategy.shouldRoute2Gray(context, instances));
        } else {
            // 灰度实例都没，不走灰度
            context.getRequest().setGray(false);
        }
        //执行下一个过滤器... TODO
    }

    @Override
    public void doPostFilter(GatewayContext context) {

    }


    @Override
    public String mark() {
        return GRAY_FILTER_NAME;
    }

    @Override
    public int getOrder() {
        return GRAY_FILTER_ORDER;
    }

    private GrayStrategy selectGrayStrategy(RouteDefinition.GrayFilterConfig grayFilterConfig) {
        return GrayStrategyManager.getStrategy(grayFilterConfig.getStrategyName());
    }

}
