package exception;




import enums.ResponseCode;

import java.io.Serial;

/**
 * 限流异常
 * 当请求被限流时抛出此异常
 * 
 * 使用场景包括：
 * 1. 令牌桶限流器中没有可用令牌
 * 2. 漏桶限流器中桶已满
 * 3. 滑动窗口限流器中窗口内请求数已达到阈值
 * 4. 其他流量控制策略拒绝请求
 */
public class LimitedException extends GatewayException {

    /**
     * 序列化版本ID
     * 用于Java序列化机制
     */
    @Serial
    private static final long serialVersionUID = -5975157585816767314L;

    /**
     * 带响应码的构造函数
     * 使用响应码的消息作为异常详细信息
     * 
     * @param code 响应码，通常是TOO_MANY_REQUESTS
     */
    public LimitedException(ResponseCode code) {
        super(code.getMessage(), code);
    }

    /**
     * 带原因和响应码的构造函数
     * 使用响应码的消息作为异常详细信息
     * 
     * @param cause 异常原因，即引发此异常的其他异常
     * @param code 响应码，通常是TOO_MANY_REQUESTS
     */
    public LimitedException(Throwable cause, ResponseCode code) {
        super(code.getMessage(), cause, code);
    }

}
