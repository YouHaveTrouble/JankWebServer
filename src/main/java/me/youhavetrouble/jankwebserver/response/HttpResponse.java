package me.youhavetrouble.jankwebserver.response;

import java.util.List;
import java.util.Map;

public interface HttpResponse {

    /**
     * Response content type. For ex. text/html or application/json
     * @return Response content type.
     */
    String contentType();

    Map<? extends String, ? extends List<String>> headers();

    /**
     * Http response status
     * @return Http response status
     */
    int status();

    /**
     * Response body
     * @return Response body
     */
    String body();

}
