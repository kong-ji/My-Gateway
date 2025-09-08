package helper;


import context.GatewayContext;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import manager.DynamicConfigManager;
import pojo.RouteDefinition;
import request.GatewayRequest;

/**
 * 上下文辅助类
 * 负责构建和处理网关上下文，包括创建上下文和写回响应
 * 是网关请求处理流程中的核心辅助类
 */
@Slf4j
public class ContextHelper {

    /**
     * 构建网关上下文
     * 根据HTTP请求和通道上下文创建网关上下文
     *
     * @param request Netty的完整HTTP请求
     * @param ctx Netty的通道处理上下文
     * @return 创建的网关上下文
     */
    public static GatewayContext buildGatewayContext(FullHttpRequest request, ChannelHandlerContext ctx) {
        // 根据URI匹配路由定义
        RouteDefinition route = RouteResolver.matchingRouteByUri(request.uri());

        // 构建网关请求
        GatewayRequest gatewayRequest = RequestHelper.buildGatewayRequest(
                DynamicConfigManager.getInstance().getServiceByName(route.getServiceName()), request, ctx);

        // 创建并返回网关上下文，包含通道上下文、网关请求、路由定义和是否保持连接的标志
        return new GatewayContext(ctx, gatewayRequest, route, HttpUtil.isKeepAlive(request));
    }

    /**
     * 写回响应
     * 将网关响应写回客户端
     * 
     * @param context 网关上下文，包含请求和响应的所有信息
     */
    public static void writeBackResponse(GatewayContext context) {
        // 构建HTTP响应
        FullHttpResponse httpResponse = ResponseHelper.buildHttpResponse(context.getResponse());

        if (!context.isKeepAlive()) { // 短连接
            // 写回响应并关闭连接
            context.getNettyCtx().writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
        } else { // 长连接
            // 设置保持连接的头信息
            httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            // 写回响应但不关闭连接
            context.getNettyCtx().writeAndFlush(httpResponse);
        }
    }
}
