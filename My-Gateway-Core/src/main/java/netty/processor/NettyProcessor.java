package netty.processor;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;


/**
 * Netty处理器接口
 * 定义了处理Netty HTTP请求的标准接口
 * 所有Netty请求处理器都应实现此接口
 * 负责接收和处理来自Netty服务器的HTTP请求
 */
public interface NettyProcessor {

    /**
     * 处理HTTP请求
     * 接收Netty通道上下文和HTTP请求，执行请求处理逻辑
     * 实现类应该在此方法中完成请求的解析、路由匹配、过滤器执行等核心处理流程
     *
     * @param ctx Netty的通道处理上下文，提供与通道交互的能力，如写回响应
     * @param request 完整的HTTP请求，包含请求的所有信息，如URI、方法、头信息、内容等
     */
    void process(ChannelHandlerContext ctx, FullHttpRequest request);

}
