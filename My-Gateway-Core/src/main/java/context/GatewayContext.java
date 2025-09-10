package context;

import filter.FilterChain;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import pojo.RouteDefinition;
import request.GatewayRequest;
import response.GatewayResponse;

@Data
public class GatewayContext {

    /**
     * Netty上下文
     */
    private ChannelHandlerContext nettyCtx;

    /**
     * 请求过程中发生的异常
     * 如果请求处理过程中发生异常，会被记录在这里
     */
    private Throwable throwable;

    /**
     * 网关请求
     * 包含请求的所有信息，如URI、方法、头信息等
     */
    private GatewayRequest request;

    /**
     * 网关响应
     * 包含响应的所有信息，如状态码、头信息、内容等
     */
    private GatewayResponse response;

    /**
     * 路由定义
     * 包含路由的配置信息，如服务名、URI模式等
     */
    private RouteDefinition route;

    /**
     * 是否保持连接
     * 如果为true，则连接会被保持，否则会在响应后关闭
     */
    private boolean keepAlive;

    /**
     * 过滤器链
     * 包含所有要执行的过滤器
     */
    private FilterChain filterChain;

    /**
     * 初始化上下文
     * @param nettyCtx Netty上下文
     * @param request 网关请求
     * @param route 网关请求
     * @param keepAlive 是否保持连接
     */
    public GatewayContext(ChannelHandlerContext nettyCtx, GatewayRequest request,
                          RouteDefinition route, boolean keepAlive) {
        this.nettyCtx = nettyCtx;
        this.request = request;
        this.route = route;
        this.keepAlive = keepAlive;
    }
}