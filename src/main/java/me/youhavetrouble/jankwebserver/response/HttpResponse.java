package me.youhavetrouble.jankwebserver.response;

import java.util.List;
import java.util.Map;

public interface HttpResponse {

    String contentType();

    Map<? extends String, ? extends List<String>> headers();

    int status();

    String body();

}
