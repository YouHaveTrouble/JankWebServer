package me.youhavetrouble.jankwebserver.response;

public interface HttpResponse {

    String contentType();

    int status();

    String body();

}
