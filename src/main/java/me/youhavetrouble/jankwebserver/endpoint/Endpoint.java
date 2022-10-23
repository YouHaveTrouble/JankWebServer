package me.youhavetrouble.jankwebserver.endpoint;

import me.youhavetrouble.jankwebserver.exception.InvalidPathException;
import me.youhavetrouble.jankwebserver.response.HttpResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.regex.Pattern;

public interface Endpoint {

    String path();

    HttpResponse handle(
            @NotNull Map<String, String> queryParams,
            @Nullable String requestBody
    );

}
