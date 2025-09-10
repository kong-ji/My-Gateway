package netty;


import config.Config;
import config.LifeCycle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import netty.handler.NettyHttpServerHandler;
import netty.processor.NettyProcessor;
import utils.SystemUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Netty的Server端实现
 * 负责接收HTTP请求并将其交给处理器处理
 */
@Slf4j
@Data
public class NettyHttpServer implements LifeCycle {

    // 网关配置对象，包含端口、线程数等配置
    private final Config config;

    // Netty处理器，用于处理收到的HTTP请求
    private final NettyProcessor nettyProcessor;

    // 原子布尔值，表示服务器是否已启动，保证线程安全
    private final AtomicBoolean start = new AtomicBoolean(false);

    // Netty的服务器引导类，用于配置和启动服务器
    private ServerBootstrap serverBootstrap;

    // Boss事件循环组，负责接收连接
    private EventLoopGroup eventLoopGroupBoss;

    // Worker事件循环组，负责处理I/O操作
    private EventLoopGroup eventLoopGroupWorker;

    public NettyHttpServer(Config config, NettyProcessor nettyProcessor) {
        this.config = config;
        this.nettyProcessor = nettyProcessor;
        init(); // 初始化服务器
    }

    /**
     * 初始化服务器，配置事件循环组
     */
    private void init() {
        // 创建服务器引导类
        this.serverBootstrap = new ServerBootstrap();

        if (SystemUtil.useEpoll()) {
            // 创建Epoll Boss事件循环组，负责接收连接
            this.eventLoopGroupBoss = new EpollEventLoopGroup(
                    config.getNettyConfig().getEventLoopGroupBossNum(), // 线程数
                    new DefaultThreadFactory("epoll-netty-boss-nio") // 线程工厂，指定线程名前缀
            );
            // 创建Epoll Worker事件循环组，负责处理I/O操作
            this.eventLoopGroupWorker = new EpollEventLoopGroup(
                    config.getNettyConfig().getEventLoopGroupWorkerNum(), // 线程数
                    new DefaultThreadFactory("epoll-netty-worker-nio") // 线程工厂，指定线程名前缀
            );
        } else {
            // 创建NIO Boss事件循环组，负责接收连接
            this.eventLoopGroupBoss = new NioEventLoopGroup(
                    config.getNettyConfig().getEventLoopGroupBossNum(), // 线程数
                    new DefaultThreadFactory("default-netty-boss-nio") // 线程工厂，指定线程名前缀
            );
            // 创建NIO Worker事件循环组，负责处理I/O操作
            this.eventLoopGroupWorker = new NioEventLoopGroup(
                    config.getNettyConfig().getEventLoopGroupWorkerNum(), // 线程数
                    new DefaultThreadFactory("default-netty-worker-nio") // 线程工厂，指定线程名前缀
            );
        }
    }

    /**
     * 启动Netty服务器
     * @SneakyThrows 自动处理InterruptedException异常
     */
    @SneakyThrows(InterruptedException.class)
    @Override
    public void start() {
        // 如果服务器已经启动，则直接返回
        if (!start.compareAndSet(false, true)) return;

        // 配置服务器参数
        serverBootstrap
                .group(eventLoopGroupBoss, eventLoopGroupWorker)
                .channel(SystemUtil.useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)            // TCP连接的最大队列长度
                .option(ChannelOption.SO_REUSEADDR, true)          // 允许端口重用
                .option(ChannelOption.SO_KEEPALIVE, true)          // 保持连接检测
                .childOption(ChannelOption.TCP_NODELAY, true)      // 禁用Nagle算法，适用于小数据即时传输
                .childOption(ChannelOption.SO_SNDBUF, 65535)       // 设置发送缓冲区大小
                .childOption(ChannelOption.SO_RCVBUF, 65535)       // 设置接收缓冲区大小
                .localAddress(new InetSocketAddress(config.getPort())) // 绑定监听端口
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(
                                new HttpServerCodec(), // 处理HTTP请求的编解码器
                                new HttpObjectAggregator(config.getNettyConfig().getMaxContentLength()), // 聚合HTTP请求
                                new HttpServerExpectContinueHandler(), // 处理HTTP 100 Continue请求
                                new NettyHttpServerHandler(nettyProcessor) // 自定义的处理器
                        );
                    }
                });

        // 绑定端口并同步等待绑定完成
        serverBootstrap.bind().sync();
        log.info("gateway startup on port {}", this.config.getPort());
    }

    /**
     * 关闭Netty服务器
     */
    @Override
    public void shutdown() {
        // 如果服务器未启动，则直接返回
        if (!start.get()) return;

        // 关闭Boss事件循环组
        if (eventLoopGroupBoss != null) {
            eventLoopGroupBoss.shutdownGracefully(); // 优雅关闭，等待任务完成
        }

        // 关闭Worker事件循环组
        if (eventLoopGroupWorker != null) {
            eventLoopGroupWorker.shutdownGracefully(); // 优雅关闭，等待任务完成
        }
    }

    /**
     * 检查服务器是否已启动
     * @return 服务器是否已启动
     */
    @Override
    public boolean isStarted() {
        return start.get();
    }
}
