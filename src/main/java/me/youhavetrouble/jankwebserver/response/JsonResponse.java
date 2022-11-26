package me.youhavetrouble.jankwebserver.response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Response containing a JSON object
 */
public class JsonResponse implements HttpResponse {

    private final int status;
    private final JSONObject body;
    private final HashMap<String, List<String>> headers = new HashMap<>();

    private JsonResponse(int status, JSONObject body) {
        this.status = status;
        this.body = body;
    }

    @Override
    public String contentType() {
        return "application/json";
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
        return body.toString();
    }

    public static JsonResponse create(JSONObject body, int status) {
        return new JsonResponse(status, body);
    }

    public static JsonResponse create(String body, int status) throws JSONException {
        JSONObject jsonObject = new JSONObject(body);
        return new JsonResponse(status, jsonObject);
    }

    public static JsonResponse create(JSONObject body) {
        return create(body, 200);
    }

    public static JsonResponse create(String body) throws JSONException {
        return create(body, 200);
    }

}
