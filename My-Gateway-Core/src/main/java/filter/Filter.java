package filter;

import context.GatewayContext;

public interface Filter {

    void doFilter(GatewayContext context);

    String mark(); // 标识唯一的过滤器

    int getOrder();

}
