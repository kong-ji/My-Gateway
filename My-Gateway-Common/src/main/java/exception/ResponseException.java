package exception;



import enums.ResponseCode;

import java.io.Serial;

/**
 * HTTP响应相关异常
 *
 */
public class ResponseException extends GatewayException {

    @Serial
    private static final long serialVersionUID = 707018357827678269L;

    public ResponseException(ResponseCode code) {
        super(code.getMessage(), code);
    }

    public ResponseException(Throwable cause, ResponseCode code) {
        super(code.getMessage(), cause, code);
        this.code = code;
    }

}
