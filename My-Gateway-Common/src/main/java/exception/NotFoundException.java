package exception;


import enums.ResponseCode;

import java.io.Serial;

/**
 * 未找到异常
 * 当请求的资源不存在时抛出此异常
 * 
 * 使用场景包括：
 * 1. 请求的路径没有匹配的路由规则
 * 2. 请求的服务未在注册中心注册
 * 3. 服务的所有实例都不可用
 * 
 * 此异常通常对应HTTP 404 Not Found响应
 */
public class NotFoundException extends GatewayException {

    /**
     * 序列化版本ID
     * 用于Java序列化机制
     */
    @Serial
	private static final long serialVersionUID = -4825153388389722853L;

    /**
     * 带响应码的构造函数
     * 使用响应码的消息作为异常详细信息
     * 
     * @param code 响应码，通常是PATH_NO_MATCHED、SERVICE_DEFINITION_NOT_FOUND或SERVICE_INSTANCE_NOT_FOUND
     */
    public NotFoundException(ResponseCode code) {
        super(code.getMessage(), code);
    }

    /**
     * 带原因和响应码的构造函数
     * 使用响应码的消息作为异常详细信息
     * 
     * @param cause 异常原因，即引发此异常的其他异常
     * @param code 响应码，通常是PATH_NO_MATCHED、SERVICE_DEFINITION_NOT_FOUND或SERVICE_INSTANCE_NOT_FOUND
     */
    public NotFoundException(Throwable cause, ResponseCode code) {
        super(code.getMessage(), cause, code);
    }

}
