package me.youhavetrouble.jankwebserver;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

public class JankWebServer {

    private final int port;
    public final static Logger logger = Logger.getLogger("JankWebServer");

    private final HttpServer server;

    private JankWebServer(int port, Executor executor) throws IOException {

        this.port = port;

        server = HttpServer.create(new InetSocketAddress(this.port), 0);
        server.setExecutor(executor);
        server.createContext("/", new Kernel());
        logger.info(String.format("Started web server on port %s", port));
    }

    /**
     * Gets the port the server is running on
     * @return Port number
     */
    public int getPort() {
        return port;
    }

    public static JankWebServer start(int port, Executor executor) throws IOException {
        return new JankWebServer(port, executor);
    }

    public static JankWebServer start(int port, int threads) throws IOException {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
        return JankWebServer.start(port, threadPoolExecutor);
    }

    public static JankWebServer start(int port) throws IOException {
        return JankWebServer.start(port, Math.max(Runtime.getRuntime().availableProcessors() / 2, 1));
    }

}
