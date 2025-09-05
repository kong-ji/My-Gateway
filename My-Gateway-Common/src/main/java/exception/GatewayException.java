package exception;

// 导入响应码枚举，定义各种HTTP响应状态

import enums.ResponseCode;
import lombok.Getter;

import java.io.Serial;

/**
 * 网关异常基类
 * 所有网关系统中的自定义异常都应继承此类
 *
 * 网关异常包含一个响应码，用于指示错误类型和对应的HTTP状态码
 * 当网关处理请求过程中发生异常时，会抛出此异常或其子类
 * 异常会被上层捕获并转换为对应的HTTP响应返回给客户端
 */
@Getter
public class GatewayException extends RuntimeException {

    /**
     * 序列化版本ID
     * 用于Java序列化机制
     */
    @Serial
    private static final long serialVersionUID = -1159027826621990252L;

    /**
     * 响应码
     * 包含HTTP状态码和错误消息
     * 用于指示错误类型
     */
    protected ResponseCode code;

    /**
     * 默认构造函数
     * 创建一个没有详细信息的网关异常
     */
    public GatewayException() {
    }

    /**
     * 带消息和响应码的构造函数
     *
     * @param message 异常详细信息
     * @param code 响应码，指示错误类型和HTTP状态码
     */
    public GatewayException(String message, ResponseCode code) {
        super(message);
        this.code = code;
    }

    /**
     * 带消息、原因和响应码的构造函数
     *
     * @param message 异常详细信息
     * @param cause 异常原因，即引发此异常的其他异常
     * @param code 响应码，指示错误类型和HTTP状态码
     */
    public GatewayException(String message, Throwable cause, ResponseCode code) {
        super(message, cause);
        this.code = code;
    }

}
