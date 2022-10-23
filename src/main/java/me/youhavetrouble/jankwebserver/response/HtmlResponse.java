package me.youhavetrouble.jankwebserver.response;

import org.json.JSONException;
import org.json.JSONObject;

public class HtmlResponse implements HttpResponse {

    private final int status;
    private final String body;

    private HtmlResponse(int status, String body) {
        this.status = status;
        this.body = body;
    }

    @Override
    public String contentType() {
        return "text/html; charset=UTF-8";
    }

    @Override
    public int status() {
        return status;
    }

    @Override
    public String body() {
        return body;
    }

    public HtmlResponse create(String body, int status) {
        return new HtmlResponse(status, body);
    }

    public HttpResponse create(String body) {
        return create(body, 200);
    }
}
