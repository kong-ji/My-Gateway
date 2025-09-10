package filter;

import context.GatewayContext;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 每个请求的过滤器链, 包含多个过滤器
 */
@Slf4j
public class FilterChain {

    private final List<Filter> filters = new ArrayList<>();

    /**
     * 为当前请求增加过滤器
     * @param filter 过滤器
     * @return 过滤器链
     */
    public FilterChain add(Filter filter) {
        filters.add(filter);
        return this;
    }

    public FilterChain add(List<Filter> filter) {
        filters.addAll(filter);
        return this;
    }

    /**
     * 执行前置过滤器链
     * @param ctx 每个请求的网关上下文
     *
     */
    public void doPreFilter(GatewayContext ctx) {
        if (!filters.isEmpty()) {
            try {
                filters.sort(Comparator.comparingInt(Filter::getOrder));
                for (Filter filter : filters) {
                    filter.doPreFilter(ctx);
                }
            } catch (Exception e) {
                log.error("执行过滤器发生异常,异常信息：{}", e.getMessage());
                throw e;
            }
        }
    }

    /**
     * 执行后置过滤器链
     * @param ctx 每个请求的网关上下文
     *
     */
    public void doPostFilter(GatewayContext ctx) {
        if (!filters.isEmpty()) {
            try {
                filters.sort(Comparator.comparing(Filter::getOrder, Comparator.reverseOrder()));
                for (Filter filter : filters) {
                    filter.doPostFilter(ctx);
                }
            } catch (Exception e) {
                log.error("执行过滤器发生异常,异常信息：{}", e.getMessage());
                throw e;
            }
        }
    }
}