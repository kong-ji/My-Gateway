package config;

import lombok.Data;
import pojo.RouteDefinition;

import java.util.List;

import static constant.ConfigConstant.*;

/**
 * 网关静态配置类
 * 包含网关的所有核心配置信息，包括基本信息、配置中心、注册中心、Netty服务器、HTTP客户端和路由配置等
 * 这是整个网关配置的顶层类，通常从配置文件(gateway.yaml)中加载
 */
@Data
public class Config {

    /**
     * 网关服务名称
     * 用于标识网关实例
     */
    private String name = DEFAULT_NAME;

    /**
     * 网关服务端口
     * 网关监听的HTTP端口
     */
    private int port = DEFAULT_PORT;

    /**
     * 环境标识
     * 用于区分开发、测试、生产等不同环境
     */
    private String env = DEFAULT_ENV;


    /**
     * 配置中心
     */
    private ConfigCenter configCenter = new ConfigCenter();


    /**
     * 注册中心
     */
    private RegisterCenter registerCenter = new RegisterCenter();



    /**
     * netty相关配置
     */
    NettyConfig nettyConfig = new NettyConfig();

    /**
     * HTTP客户端配置
     * 包含HTTP客户端的连接池大小、超时时间等参数
     * 用于配置网关转发请求时使用的HTTP客户端
     */
    private HttpClientConfig httpClient = new HttpClientConfig();

    /**
     * 路由配置列表
     * 包含所有静态定义的路由规则
     * 每个路由规则定义了请求如何被转发到后端服务
     */
    private List<RouteDefinition> routes;

}
