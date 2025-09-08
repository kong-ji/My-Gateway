package helper;

import cn.hutool.json.JSONUtil;

import enums.ResponseCode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.Response;
import response.GatewayResponse;

import java.util.Objects;


/**
 * 响应辅助类
 * 负责处理网关响应的构建和转换
 * 实现Netty服务端、网关、Http客户端之间的响应转换
 */
@Slf4j
public class ResponseHelper {

    /**
     * 构建HTTP响应
     * 将网关响应转换为Netty的HTTP响应
     * 
     * @param gatewayResponse 网关响应
     * @return Netty的完整HTTP响应
     */
    public static FullHttpResponse buildHttpResponse(GatewayResponse gatewayResponse) {
        // 创建响应内容
        ByteBuf content;
        if (Objects.nonNull(gatewayResponse.getResponse())) {
            // 如果有下游服务的响应，使用下游响应的内容
            content = Unpooled.wrappedBuffer(gatewayResponse.getResponse().getResponseBodyAsByteBuffer()); // 下游服务的http响应结果
        } else if (gatewayResponse.getContent() != null) {
            // 如果有自定义内容，使用自定义内容
            content = Unpooled.wrappedBuffer(gatewayResponse.getContent().getBytes());
        } else {
            // 如果没有内容，使用空字符串
            content = Unpooled.wrappedBuffer("".getBytes());
        }

        // 创建HTTP响应
        DefaultFullHttpResponse httpResponse;
        if (Objects.nonNull(gatewayResponse.getResponse())) { // 下游响应不为空，直接拿下游响应构造
            // 使用下游响应的状态码和内容创建HTTP响应
            httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.valueOf(gatewayResponse.getResponse().getStatusCode()), content);
            // 添加下游响应的头信息
            httpResponse.headers().add(gatewayResponse.getResponse().getHeaders());
        } else {
            // 使用网关响应的状态码和内容创建HTTP响应
            httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    gatewayResponse.getHttpResponseStatus(), content);
            // 添加网关响应的头信息
            httpResponse.headers().add(gatewayResponse.getResponseHeaders());
            // 设置内容长度
            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
        }

        return httpResponse;
    }

    /**
     * 构建HTTP响应
     * 根据响应码创建Netty的HTTP响应
     * 
     * @param responseCode 响应码枚举
     * @return Netty的完整HTTP响应
     */
    public static FullHttpResponse buildHttpResponse(ResponseCode responseCode) {
        // 使用响应码的状态和消息创建HTTP响应
        DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                responseCode.getStatus(),
                Unpooled.wrappedBuffer(responseCode.getMessage().getBytes()));
        // 设置内容类型为JSON
        httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
        // 设置内容长度
        httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());

        return httpResponse;
    }

    /**
     * 构建网关响应
     * 将异步HTTP客户端的响应转换为网关响应
     * 
     * @param response 异步HTTP客户端的响应
     * @return 网关响应
     */
    public static GatewayResponse buildGatewayResponse(Response response) {
        // 创建网关响应
        GatewayResponse gatewayResponse = new GatewayResponse();
        // 设置响应头
        gatewayResponse.setResponseHeaders(response.getHeaders());
        // 设置HTTP状态码
        gatewayResponse.setHttpResponseStatus(HttpResponseStatus.valueOf(response.getStatusCode()));
        // 设置响应内容
        gatewayResponse.setContent(response.getResponseBody());
        // 设置原始响应
        gatewayResponse.setResponse(response);

        return gatewayResponse;
    }

    /**
     * 构建网关响应
     * 根据响应码创建网关响应
     * 
     * @param code 响应码枚举
     * @return 网关响应
     */
    public static GatewayResponse buildGatewayResponse(ResponseCode code) {
        // 创建网关响应
        GatewayResponse gatewayResponse = new GatewayResponse();
        // 添加内容类型头信息
        gatewayResponse.addHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
        // 设置HTTP状态码
        gatewayResponse.setHttpResponseStatus(code.getStatus());
        // 设置响应内容为JSON格式的消息
        gatewayResponse.setContent(JSONUtil.toJsonStr(code.getMessage()));

        return gatewayResponse;
    }

    /**
     * 构建网关响应
     * 将数据对象转换为JSON格式的网关响应
     * 
     * @param data 数据对象
     * @return 网关响应
     */
    public static GatewayResponse buildGatewayResponse(Object data) {
        // 创建网关响应
        GatewayResponse gatewayResponse = new GatewayResponse();
        // 添加内容类型头信息
        gatewayResponse.addHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON + ";charset=utf-8");
        // 设置HTTP状态码为成功
        gatewayResponse.setHttpResponseStatus(ResponseCode.SUCCESS.getStatus());
        // 设置响应内容为JSON格式的数据
        gatewayResponse.setContent(JSONUtil.toJsonStr(data));

        return gatewayResponse;
    }
}
