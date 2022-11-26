package me.youhavetrouble.jankwebserver;

import java.io.IOException;

public class ExampleServer {

    public static void main(String[] args) throws IOException {
        JankWebServer server = JankWebServer.create(8080);
        server.start();
    }

}
