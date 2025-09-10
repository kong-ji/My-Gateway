package netty.processor;

import context.GatewayContext;
import enums.ResponseCode;
import exception.GatewayException;
import filter.FilterChainFactory;
import helper.ContextHelper;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import helper.ResponseHelper;
/**
 * Netty核心处理器
 * 负责处理HTTP请求的核心逻辑，包括构建上下文、执行过滤器链、处理异常等
 */
@Slf4j
public class NettyCoreProcessor implements NettyProcessor {

    @Override
    public void process(ChannelHandlerContext ctx, FullHttpRequest request) {
        try {
            GatewayContext gatewayContext = ContextHelper.buildGatewayContext(request, ctx);
            FilterChainFactory.buildFilterChain(gatewayContext);
            gatewayContext.getFilterChain().doPostFilter(gatewayContext);
        } catch (GatewayException e) {
            log.error("处理错误 {} {}", e.getCode(), e.getCode().getMessage());
            FullHttpResponse httpResponse = ResponseHelper.buildHttpResponse(e.getCode());
            doWriteAndRelease(ctx, request, httpResponse);
        } catch (Throwable t) {
            log.error("处理未知错误", t);
            FullHttpResponse httpResponse = ResponseHelper.buildHttpResponse(ResponseCode.INTERNAL_ERROR);
            doWriteAndRelease(ctx, request, httpResponse);
        }
    }
    /**
     * 写回响应并释放资源
     * @param ctx Netty的通道处理上下文
     * @param request HTTP请求
     * @param httpResponse HTTP响应
     */
    private void doWriteAndRelease(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse httpResponse) {
        // 写回响应并添加监听器，在写完后关闭通道
        ctx.writeAndFlush(httpResponse)
                .addListener(ChannelFutureListener.CLOSE); // 发送响应后关闭通道
        // 释放与请求相关联的资源，防止内存泄漏
        ReferenceCountUtil.release(request); // 释放与请求相关联的资源
    }
}