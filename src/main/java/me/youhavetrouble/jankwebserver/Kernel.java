package me.youhavetrouble.jankwebserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.youhavetrouble.jankwebserver.endpoint.Endpoint;
import me.youhavetrouble.jankwebserver.exception.EndpointAlreadyRegisteredException;
import me.youhavetrouble.jankwebserver.exception.NotDirectoryException;
import me.youhavetrouble.jankwebserver.response.HttpResponse;

import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Pattern;

public class Kernel implements HttpHandler {

    private final HashSet<Endpoint> endpoints = new HashSet<>();
    private File staticDirectory;

    protected Kernel() {
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        Endpoint foundEndpoint = null;
        String path = httpExchange.getRequestURI().getPath();
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        for (Endpoint endpoint : endpoints) {
            if (!Pattern.matches(endpoint.path(), path)) continue;
            foundEndpoint = endpoint;
            break;
        }

        if (foundEndpoint == null) {
            // static resource resolution
            if (staticDirectory == null) {
                sendNotFound(httpExchange);
                return;
            }
            try {
                if (path.startsWith("/")) path = path.substring(1);
                Path childPath = staticDirectory.toPath().resolve(path);
                File childFile = childPath.toFile();
                if (!childFile.isFile()) {
                    sendNotFound(httpExchange);
                    return;
                }
                sendStaticResource(httpExchange, childPath);
                return;
            } catch (InvalidPathException e) {
                sendNotFound(httpExchange);
                return;
            }
        }

        HashMap<String, String> queryParams = getQueryParams(httpExchange.getRequestURI());
        String requestBody = new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        RequestMethod requestMethod = null;
        try {
            requestMethod = RequestMethod.valueOf(
                    httpExchange
                            .getRequestMethod()
                            .trim()
                            .toUpperCase(Locale.ENGLISH)
            );
        } catch (IllegalArgumentException ignored) {
        }

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
        httpExchange.sendResponseHeaders(response.status(), response.body().length());
        httpExchange.getResponseBody().write(response.body().getBytes(StandardCharsets.UTF_8));
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

    protected void setStaticDirectory(File staticDirectory) {
        if (!staticDirectory.isDirectory())
            throw new NotDirectoryException("Static resources source needs to be a directory");
        this.staticDirectory = staticDirectory;

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

    private HashMap<String, String> getQueryParams(URI uri) {
        HashMap<String, String> query = new HashMap<>();
        if (uri.getRawQuery() == null) return query;
        String[] splitQuery = uri.getRawQuery().split("&");
        for (String param : splitQuery) {
            final int idx = param.indexOf("=");
            final String key = idx > 0 ? param.substring(0, idx) : param;
            final String value = idx > 0 && param.length() > idx + 1 ? param.substring(idx + 1) : null;
            query.put(URLDecoder.decode(key, StandardCharsets.UTF_8), value == null ? null : URLDecoder.decode(value, StandardCharsets.UTF_8));
        }
        return query;
    }

    private void sendStaticResource(HttpExchange exchange, Path file) {
        OutputStream stream = exchange.getResponseBody();
        try {
            Files.copy(file, stream);
            exchange.sendResponseHeaders(200, 0);
            stream.close();
        } catch (IOException ignored) {}
    }

}
