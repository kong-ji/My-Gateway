package http;

import org.asynchttpclient.AsyncHttpClient;
/**
 * HTTP客户端类
 * 封装了异步HTTP客户端的操作，提供HTTP请求的执行功能
 * 使用单例模式实现，确保全局只有一个HTTP客户端实例
 * 负责网关向后端服务发送请求
 */
public class HttpClient {
    /**
     * 异步HTTP客户端实例
     */
    private AsyncHttpClient asyncHttpClient;

    private HttpClient() {

    }

    private final static HttpClient INSTANCE = new HttpClient();

    /**
     * 获取HttpClient单例实例
     * @return HttpClient单例实例
     */
    public static HttpClient getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化方法
     * @param asyncHttpClient 异步HTTP客户端实例
     */
    public void initialized(AsyncHttpClient asyncHttpClient) {
        this.asyncHttpClient = asyncHttpClient;
    }


}
