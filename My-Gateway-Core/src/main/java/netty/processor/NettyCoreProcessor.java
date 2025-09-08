package netty.processor;

import context.GatewayContext;
import helper.ContextHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Netty核心处理器
 * 负责处理HTTP请求的核心逻辑，包括构建上下文、执行过滤器链、处理异常等
 */
@Slf4j
public class NettyCoreProcessor implements NettyProcessor {

    @Override
    public void process(ChannelHandlerContext ctx, FullHttpRequest request) {
        GatewayContext gatewayContext = ContextHelper.buildGatewayContext(request, ctx);
    }

}