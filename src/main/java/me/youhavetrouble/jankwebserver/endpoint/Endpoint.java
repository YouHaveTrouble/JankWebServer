package me.youhavetrouble.jankwebserver.endpoint;

import com.sun.net.httpserver.HttpExchange;
import me.youhavetrouble.jankwebserver.response.HttpResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface Endpoint {

    String path();

    HttpResponse handle(
            @NotNull HttpExchange httpExchange,
            @NotNull Map<String, String> queryParams,
            @Nullable String requestBody
    );

}
