package response;



import io.netty.handler.codec.http.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.asynchttpclient.Response;



@Data
@NoArgsConstructor
public class GatewayResponse {

    /**
     * 响应头
     */
    private HttpHeaders responseHeaders = new DefaultHttpHeaders();

    /**
     * 响应内容
     */
    private String content;

    /**
     * 原始响应
     * 从后端服务获取的原始HTTP响应
     * 包含完整的响应信息，如状态码、头信息、内容等
     * 使用异步HTTP客户端的Response表示
     */
    private Response response;

    /**
     * 响应状态码
     * HTTP响应的状态码，如200 OK、404 Not Found等
     * 使用Netty的HttpResponseStatus表示
     */
    private HttpResponseStatus httpResponseStatus;

    /**
     * 添加响应头
     */
    public void addHeader(CharSequence key, CharSequence val) {
        responseHeaders.add(key, val);
    }

}
