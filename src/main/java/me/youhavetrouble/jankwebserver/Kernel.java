package me.youhavetrouble.jankwebserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.youhavetrouble.jankwebserver.endpoint.Endpoint;
import me.youhavetrouble.jankwebserver.exception.EndpointAlreadyRegisteredException;
import me.youhavetrouble.jankwebserver.response.HttpResponse;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Pattern;

public class Kernel implements HttpHandler {

    private final HashSet<Endpoint> endpoints = new HashSet<>();

    protected Kernel() {}

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        Endpoint foundEndpoint = null;
        String path = httpExchange.getRequestURI().getPath();
        if (path.endsWith("/")) {
            path = path.substring(0, path.length()-1);
        }

        for (Endpoint endpoint : endpoints) {
            if (!Pattern.matches(endpoint.path(), path)) continue;
            foundEndpoint = endpoint;
            break;
        }

        if (foundEndpoint == null) {
            sendNotFound(httpExchange);
            return;
        }

        HashMap<String, String> queryParams = getQueryParams(httpExchange);

        String requestBody = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        RequestMethod requestMethod = null;
        try {
            requestMethod = RequestMethod.valueOf(
                    httpExchange
                            .getRequestMethod()
                            .trim()
                            .toUpperCase(Locale.ENGLISH)
            );
        } catch (IllegalArgumentException ignored) {}

        if (requestMethod == null) {
            sendBadRequest(httpExchange);
            return;
        }

        HttpResponse response = foundEndpoint.handle(
                requestMethod,
                httpExchange.getRequestURI(),
                httpExchange.getRequestHeaders(),
                queryParams,
                requestBody
        );

        httpExchange.getResponseHeaders().putAll(response.headers());

        httpExchange.getResponseHeaders().set("Content-Type", response.contentType());
        httpExchange.getResponseBody().write(response.body().getBytes(StandardCharsets.UTF_8));
        httpExchange.sendResponseHeaders(response.status(), response.body().length());
        httpExchange.close();

    }

    protected void registerEndpoint(Endpoint endpoint) {
        if (!endpoints.add(endpoint)) {
            throw new EndpointAlreadyRegisteredException("Endpoint like that is already registered");
        }
    }

    protected void unregisterEndpoint(Endpoint endpoint) {
        endpoints.remove(endpoint);
    }

    private void sendNotFound(HttpExchange httpExchange) throws IOException {
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(404, -1);
        httpExchange.close();
    }

    private void sendBadRequest(HttpExchange httpExchange) throws IOException {
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(400, -1);
        httpExchange.close();
    }
    private HashMap<String, String> getQueryParams(HttpExchange httpExchange) {
        HashMap<String, String> query = new HashMap<>();
        String[] splitQuery = httpExchange.getRequestURI().getRawQuery().split("&");
        for (String param : splitQuery) {
            final int idx = param.indexOf("=");
            final String key = idx > 0 ? param.substring(0, idx) : param;
            final String value = idx > 0 && param.length() > idx + 1 ? param.substring(idx + 1) : null;
            query.put(URLDecoder.decode(key, StandardCharsets.UTF_8), value == null ? null : URLDecoder.decode(value, StandardCharsets.UTF_8));
        }
        return query;
    }

}
