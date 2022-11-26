package me.youhavetrouble.jankwebserver.endpoint;

import com.sun.net.httpserver.Headers;
import me.youhavetrouble.jankwebserver.RequestMethod;
import me.youhavetrouble.jankwebserver.response.HttpResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Map;

public interface Endpoint {

    /**
     * Regex string that request path has to match to handle this endpoint
     * @return Regex string
     */
    String path();

    /**
     * Handle the incoming request data and return a response
     * @param requestMethod Request method for the endpoint
     * @param requestURI URI of the endpoint.<br>
     * Always starts with /<br>
     * / from the end is always stripped
     * @param headers Request headers
     * @param queryParams Request query params as key-value map
     * @param requestBody Body of a request
     * @return Response to send back
     */
    HttpResponse handle(
            @NotNull RequestMethod requestMethod,
            @NotNull URI requestURI,
            @NotNull Headers headers,
            @NotNull Map<String, String> queryParams,
            @Nullable String requestBody
    );

}
