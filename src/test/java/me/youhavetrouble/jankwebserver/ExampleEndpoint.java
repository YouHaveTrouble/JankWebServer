package me.youhavetrouble.jankwebserver;

import com.sun.net.httpserver.Headers;
import me.youhavetrouble.jankwebserver.endpoint.Endpoint;
import me.youhavetrouble.jankwebserver.response.HttpResponse;
import me.youhavetrouble.jankwebserver.response.JsonResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Map;

public class ExampleEndpoint implements Endpoint {
    @Override
    public String path() {
        return "/test";
    }

    @Override
    public HttpResponse handle(@NotNull RequestMethod requestMethod, @NotNull URI requestURI, @NotNull Headers headers, @NotNull Map<String, String> queryParams, @Nullable String requestBody) {
        return JsonResponse.create("{\"message\":\"test\"}");
    }
}
