package netty;


import config.Config;
import config.HttpClientConfig;
import config.LifeCycle;
import http.HttpClient;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import utils.SystemUtil;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Netty HTTP客户端类
 * 负责创建和管理异步HTTP客户端，用于网关向后端服务发送请求
 * 基于AsyncHttpClient库实现，支持高性能的异步HTTP请求
 *
 */
@Slf4j
public class NettyHttpClient implements LifeCycle {

    /**
     * 网关配置对象
     * 包含HTTP客户端的配置信息，如线程数、超时时间等
     */
    private final Config config;

    /**
     * Netty工作线程组
     * 处理HTTP客户端的I/O操作
     * 根据系统环境选择使用EpollEventLoopGroup或NioEventLoopGroup
     */
    private final EventLoopGroup eventLoopGroupWorker;

    /**
     * 客户端启动状态标志
     * 使用原子布尔值确保线程安全
     * 防止重复启动或关闭
     */
    private final AtomicBoolean start = new AtomicBoolean(false);

    /**
     * 异步HTTP客户端实例
     * 实际执行HTTP请求的组件
     * 基于Netty实现的高性能异步HTTP客户端
     */
    private AsyncHttpClient asyncHttpClient;

    /**
     * 构造函数
     * 初始化配置和事件循环组
     * 
     * @param config 网关配置对象，包含HTTP客户端的配置信息
     */
    public NettyHttpClient(Config config) {
        this.config = config;
        // 根据系统环境选择使用Epoll还是NIO
        if (SystemUtil.useEpoll()) {
            // Linux系统下使用Epoll事件循环组
            this.eventLoopGroupWorker = new EpollEventLoopGroup(
                    config.getNettyConfig().getEventLoopGroupWorkerNum(), // 工作线程数
                    new DefaultThreadFactory("epoll-http-client-worker-nio") // 线程工厂，指定线程名前缀
            );
        } else {
            // 其他系统使用NIO事件循环组
            this.eventLoopGroupWorker = new NioEventLoopGroup(
                    config.getNettyConfig().getEventLoopGroupWorkerNum(), // 工作线程数
                    new DefaultThreadFactory("default-http-client-worker-nio") // 线程工厂，指定线程名前缀
            );
        }
    }

    /**
     * 启动HTTP客户端
     * 创建并初始化异步HTTP客户端
     * 将客户端实例注入到全局HTTP客户端单例中
     */
    @Override
    public void start() {
        // CAS操作确保只启动一次
        if (!start.compareAndSet(false, true)) return;
        
        // 获取HTTP客户端配置
        HttpClientConfig httpClientConfig = config.getHttpClient();
        
        // 构建异步HTTP客户端配置
        DefaultAsyncHttpClientConfig.Builder builder = new DefaultAsyncHttpClientConfig.Builder()
                .setEventLoopGroup(eventLoopGroupWorker) // 设置事件循环组，复用已创建的线程组
                .setConnectTimeout(httpClientConfig.getHttpConnectTimeout()) // 设置连接超时时间，控制建立连接的最长等待时间
                .setRequestTimeout(httpClientConfig.getHttpRequestTimeout()) // 设置请求超时时间，控制整个请求的最长等待时间
                .setMaxRedirects(httpClientConfig.getHttpMaxRedirects()) // 设置最大重定向次数，防止重定向循环
                .setAllocator(PooledByteBufAllocator.DEFAULT) // 使用池化的ByteBuf分配器提升性能，减少内存分配和GC压力
                .setCompressionEnforced(true) // 启用压缩，减少网络传输数据量
                .setMaxConnections(httpClientConfig.getHttpMaxConnections()) // 设置最大连接数，控制客户端的总连接数上限
                .setMaxConnectionsPerHost(httpClientConfig.getHttpConnectionsPerHost()) // 设置每个主机的最大连接数，防止单一主机连接过多
                .setPooledConnectionIdleTimeout(httpClientConfig.getHttpPooledConnectionIdleTimeout()); // 设置连接池中空闲连接的超时时间，回收长时间不用的连接
        
        // 创建并初始化异步HTTP客户端
        this.asyncHttpClient = new DefaultAsyncHttpClient(builder.build());
        
        // 初始化全局HTTP客户端实例，使其他组件可以通过单例访问HTTP客户端
        HttpClient.getInstance().initialized(asyncHttpClient);
    }

    /**
     * 关闭HTTP客户端
     * 释放客户端资源，关闭连接池
     * 确保资源的安全释放，避免资源泄漏
     */
    @Override
    public void shutdown() {
        // 如果客户端未启动，直接返回
        if (!start.get()) return;
        
        // 关闭异步HTTP客户端
        if (asyncHttpClient != null) {
            try {
                this.asyncHttpClient.close(); // 关闭客户端，释放资源
            } catch (IOException e) {
                // 记录关闭过程中的错误
                log.error("NettyHttpClient shutdown error", e);
            }
        }
    }

    /**
     * 检查客户端是否已启动
     * 
     * @return 客户端是否已启动
     */
    @Override
    public boolean isStarted() {
        return start.get();
    }
}
