package netty.handler;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import netty.processor.NettyProcessor;

/**
 * Netty HTTP服务器处理器
 * 负责接收HTTP请求并将其交给处理器处理
 * 处理流程：
 * 1. 接收来自客户端的HTTP请求
 * 2. 将请求交给NettyProcessor处理
 * 3. NettyProcessor负责构建网关上下文、执行过滤器链等核心处理逻辑
 */
public class NettyHttpServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * Netty处理器
     * 负责处理HTTP请求的核心逻辑
     * 通常是NettyCoreProcessor的实例
     */
    private final NettyProcessor nettyProcessor;

    public NettyHttpServerHandler(NettyProcessor nettyProcessor) {
        this.nettyProcessor = nettyProcessor;
    }

    /**
     * 当通道收到消息时调用
     *
     * @param ctx 通道处理上下文，提供与通道交互的能力，如写回响应
     * @param msg 收到的消息，这里是HTTP请求，类型为FullHttpRequest
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        nettyProcessor.process(ctx, (FullHttpRequest) msg);
    }

    /**
     * 当处理过程中发生异常时调用
     * 处理通道处理过程中的异常情况
     *
     * @param ctx 通道处理上下文，提供与通道交互的能力
     * @param cause 异常原因，包含异常的详细信息
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 调用父类的 exceptionCaught 方法，它将按照 ChannelPipeline 中的下一个处理器继续处理异常
        // 如果需要自定义异常处理逻辑，可以在这里添加，如记录日志、关闭连接等
        super.exceptionCaught(ctx, cause);
    }
}
