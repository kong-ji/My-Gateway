package enums;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Getter;

/**
 * 响应码枚举
 * 定义了网关系统中所有的响应状态码和对应的消息
 * 包括成功响应、客户端错误和服务端错误
 * 
 * 响应码分类：
 * - 2xx：成功响应
 * - 4xx：客户端错误，如请求路径不存在、请求过多等
 * - 5xx：服务端错误，如服务不可用、网关内部错误等
 */
@Getter
public enum ResponseCode {

    /* 2xx 成功响应 */
    /**
     * 请求成功
     * 对应HTTP 200 OK
     */
    SUCCESS(HttpResponseStatus.OK, "成功"),


    /* 4xx 客户端错误 */
    /**
     * 请求路径不存在
     * 对应HTTP 404 Not Found
     * 当请求的路径没有匹配的路由规则时返回
     */
    PATH_NO_MATCHED(HttpResponseStatus.NOT_FOUND, "没有找到匹配的路径, 请求快速失败"),
    
    /**
     * 服务定义不存在
     * 对应HTTP 404 Not Found
     * 当请求的服务未在注册中心注册时返回
     */
    SERVICE_DEFINITION_NOT_FOUND(HttpResponseStatus.NOT_FOUND, "未找到对应的服务定义"),
    
    /**
     * 服务实例不存在
     * 对应HTTP 404 Not Found
     * 当服务的所有实例都不可用时返回
     */
    SERVICE_INSTANCE_NOT_FOUND(HttpResponseStatus.NOT_FOUND, "未找到对应的服务实例"),

    /**
     * 请求过多
     * 对应HTTP 429 Too Many Requests
     * 当请求被限流时返回
     */
    TOO_MANY_REQUESTS(HttpResponseStatus.TOO_MANY_REQUESTS, "请求过多，请稍后再试"),


    /* 5xx 服务端错误 */
    /**
     * 服务不可用
     * 对应HTTP 503 Service Unavailable
     * 当后端服务暂时不可用时返回
     */
    SERVICE_UNAVAILABLE(HttpResponseStatus.SERVICE_UNAVAILABLE, "服务暂时不可用,请稍后再试"),

    /**
     * 网关熔断降级
     * 对应HTTP 504 Gateway Timeout
     * 当触发熔断降级策略时返回
     */
    GATEWAY_FALLBACK(HttpResponseStatus.GATEWAY_TIMEOUT, "业务暂时不可用，触发熔断降级"),
    
    /**
     * 请求超时
     * 对应HTTP 504 Gateway Timeout
     * 当连接后端服务超时时返回
     */
    REQUEST_TIMEOUT(HttpResponseStatus.GATEWAY_TIMEOUT, "连接下游服务超时"),

    /**
     * 网关内部错误
     * 对应HTTP 500 Internal Server Error
     * 当网关内部发生未预期的异常时返回
     */
    INTERNAL_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR, "网关内部错误"),
    
    /**
     * 过滤器配置解析错误
     * 对应HTTP 500 Internal Server Error
     * 当过滤器配置解析失败时返回
     */
    FILTER_CONFIG_PARSE_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR, "过滤器配置解析异常"),
    
    /**
     * HTTP响应错误
     * 对应HTTP 500 Internal Server Error
     * 当后端服务返回异常响应时返回
     */
    HTTP_RESPONSE_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR, "服务返回异常"),
    
    /**
     * 流量控制错误
     * 对应HTTP 500 Internal Server Error
     * 当流量控制组件发生错误时返回
     */
    FLOW_CONTROL_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR, "请求过量错误");


    /**
     * HTTP响应状态
     * 对应Netty的HttpResponseStatus
     */
    private final HttpResponseStatus status;
    
    /**
     * 响应消息
     * 描述响应状态的详细信息
     */
    private final String message;

    /**
     * 构造函数
     * 
     * @param status HTTP响应状态
     * @param msg 响应消息
     */
    ResponseCode(HttpResponseStatus status, String msg) {
        this.status = status;
        this.message = msg;
    }

}
