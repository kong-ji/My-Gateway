package config;

import config.lib.config.ConfigCenter;
import config.lib.config.RegisterCenter;
import lombok.Data;
import pojo.RouteDefinition;

import java.util.List;

/**
 * 网关静态配置类
 * 包含网关的所有核心配置信息，包括基本信息、配置中心、注册中心、Netty服务器、HTTP客户端和路由配置等
 * 这是整个网关配置的顶层类，通常从配置文件(gateway.yaml)中加载
 */
@Data
public class Config {
    //TODO 暂时硬编码
    /**
     * 网关服务端口
     * 网关监听的HTTP端口
     */
    private int port = 9999;
    
    /**
     * 环境标识
     * 用于区分开发、测试、生产等不同环境
     */
    private String env = "dev";


    /**
     * 配置中心
     */
    private ConfigCenter configCenter = ConfigCenter.NACOS; // 配置中心实现
    private String configAddress = "192.168.150.102:8848"; // 配置中心地址

    /**
     * 注册中心
     */
    private RegisterCenter registerCenter = RegisterCenter.NACOS; // 注册中心实现
    private String registerAddress = "192.168.150.102:8848"; // 注册中心地址


    /**
     * netty相关配置
     */
    private int eventLoopGroupBossNum = 1;
    private int eventLoopGroupWorkerNum = Runtime.getRuntime().availableProcessors();
    private int maxContentLength = 64 * 1024 * 1024; // 64MB

    /**
     * 路由配置列表
     * 包含所有静态定义的路由规则
     * 每个路由规则定义了请求如何被转发到后端服务
     */
    private List<RouteDefinition> routes;

}
