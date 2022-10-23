package me.youhavetrouble.jankwebserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.youhavetrouble.jankwebserver.endpoint.Endpoint;
import me.youhavetrouble.jankwebserver.exception.EndpointAlreadyRegisteredException;
import me.youhavetrouble.jankwebserver.exception.InvalidPathException;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

public class Kernel implements HttpHandler {

    private final HashSet<Endpoint> endpoints = new HashSet<>();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        Endpoint foundEndpoint = null;
        String path = httpExchange.getRequestURI().getPath();

        for (Endpoint endpoint : endpoints) {
            if (!Pattern.matches("^"+endpoint.path()+"[/]{0,1}$", path)) continue;
            foundEndpoint = endpoint;
            break;
        }

        if (foundEndpoint == null) {
            sendNotFound(httpExchange);
            return;
        }

        HashMap<String, String> queryParams = getQueryParams(httpExchange);

        String requestBody = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        foundEndpoint.handle(queryParams, requestBody);

        sendNotFound(httpExchange);
    }

    /**
     * Registers the endpoint.
     * <br>
     * Endpoint path needs to match ^[\p{L}0-9/\-_]*$ regex
     * @param endpoint Endpoint to register
     * @throws EndpointAlreadyRegisteredException if endpoint was already registered
     * @throws InvalidPathException if endpoint's path does not match ^[\p{L}0-9/\-_]*$ regex
     */
    protected void registerEndpoint(Endpoint endpoint) {
        if (!Pattern.matches("^[\\p{L}0-9/\\-_]*$", endpoint.path())) {
            throw new InvalidPathException(String.format("Path %s contains disallowed characters", endpoint.path()));
        }
        if (!endpoints.add(endpoint)) {
            throw new EndpointAlreadyRegisteredException("Endpoint like that is already registered");
        }
    }

    private void sendNotFound(HttpExchange httpExchange) throws IOException {
        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(404, 0);
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
