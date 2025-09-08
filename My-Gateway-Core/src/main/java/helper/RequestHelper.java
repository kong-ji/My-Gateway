package helper;

import com.alibaba.nacos.common.utils.StringUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.asynchttpclient.Request;
import pojo.ServiceDefinition;
import request.GatewayRequest;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static constant.HttpConstant.HTTP_FORWARD_SEPARATOR;


/**
 * 请求辅助类
 * 负责处理网关请求的构建和转换
 * 实现Netty服务端、网关、Http客户端之间的请求转换
 */
public class RequestHelper {

    /**
     * 构建网关请求
     * 根据服务定义、HTTP请求和通道上下文创建网关请求
     * 
     * @param serviceDefinition 服务定义，包含服务的配置信息
     * @param fullHttpRequest Netty的完整HTTP请求
     * @param ctx Netty的通道处理上下文
     * @return 创建的网关请求
     */
    public static GatewayRequest buildGatewayRequest(ServiceDefinition serviceDefinition, FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {
        HttpHeaders headers = fullHttpRequest.headers(); // 服务端的http请求头
        String host = headers.get(HttpHeaderNames.HOST); // host
        HttpMethod method = fullHttpRequest.method(); // http请求类型
        String uri = fullHttpRequest.uri(); // uri
        String clientIp = getClientIp(ctx, fullHttpRequest); // 客户端ip
        String contentType = HttpUtil.getMimeType(fullHttpRequest) == null ? null :
                HttpUtil.getMimeType(fullHttpRequest).toString(); // 请求的MIME类型
        Charset charset = HttpUtil.getCharset(fullHttpRequest, StandardCharsets.UTF_8); // 字符集

        // 创建并返回网关请求，包含服务定义、字符集、客户端IP、主机、URI、方法、内容类型、头信息和完整HTTP请求
        return new GatewayRequest(serviceDefinition, charset, clientIp, host, uri, method,
                contentType, headers, fullHttpRequest);
    }

    /**
     * 构建HTTP客户端请求
     * 将网关请求转换为异步HTTP客户端的请求
     * 
     * @param gatewayRequest 网关请求
     * @return 异步HTTP客户端的请求
     */
    public static Request buildHttpClientRequest(GatewayRequest gatewayRequest) {
        // 调用网关请求的build方法构建异步HTTP客户端请求
        return gatewayRequest.build();
    }

    /**
     * 获取客户端IP
     * 首先尝试从X-Forwarded-For头获取，如果没有则从通道远程地址获取
     * 
     * @param ctx Netty的通道处理上下文
     * @param request Netty的完整HTTP请求
     * @return 客户端IP地址
     */
    private static String getClientIp(ChannelHandlerContext ctx, FullHttpRequest request) {
        // 从X-Forwarded-For头获取客户端IP
        String xForwardedValue = request.headers().get(HTTP_FORWARD_SEPARATOR);

        String clientIp = null;
        if (StringUtils.isNotEmpty(xForwardedValue)) {
            // 解析X-Forwarded-For头，格式为：client, proxy1, proxy2, ...
            List<String> values = Arrays.asList(xForwardedValue.split(", "));
            if (values.size() >= 1 && StringUtils.isNotBlank(values.get(0))) {
                // 取第一个值，即最原始的客户端IP
                clientIp = values.get(0);
            }
        }
        if (clientIp == null) {
            // 如果X-Forwarded-For头不存在或解析失败，则从通道远程地址获取
            InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            clientIp = inetSocketAddress.getAddress().getHostAddress();
        }
        return clientIp;
    }

}
