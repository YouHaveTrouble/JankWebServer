package me.youhavetrouble.jankwebserver.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Response containing html
 */
public class HtmlResponse implements HttpResponse {

    private final int status;
    private final String body;
    private final HashMap<String, List<String>> headers = new HashMap<>();

    private HtmlResponse(int status, String body) {
        this.status = status;
        this.body = body;
    }

    @Override
    public String contentType() {
        return "text/html; charset=UTF-8";
    }

    @Override
    public Map<? extends String, ? extends List<String>> headers() {
        return headers;
    }

    @Override
    public int status() {
        return status;
    }

    @Override
    public String body() {
        return body;
    }

    public static HtmlResponse create(String body, int status) {
        return new HtmlResponse(status, body);
    }

    public static HttpResponse create(String body) {
        return create(body, 200);
    }
}
