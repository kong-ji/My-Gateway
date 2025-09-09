package filter;

import context.GatewayContext;

/**
 * 过滤器接口
 */
public interface Filter {

    void doFilter(GatewayContext context);

    String mark(); // 标识唯一的过滤器

    int getOrder();

}
