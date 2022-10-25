package me.youhavetrouble.jankwebserver;

import com.sun.net.httpserver.HttpServer;
import me.youhavetrouble.jankwebserver.endpoint.Endpoint;
import me.youhavetrouble.jankwebserver.exception.EndpointAlreadyRegisteredException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Logger;

public class JankWebServer {

    public final static Logger logger = Logger.getLogger("JankWebServer");

    private final Executor executor;
    private final Kernel kernel;
    private final int port;

    private HttpServer server;
    private boolean started = false;

    private JankWebServer(int port, @NotNull Executor executor) {
        if (executor == null) throw new NullPointerException("Server's executor cannot be null");
        this.port = port;
        this.executor = executor;
        this.kernel = new Kernel();
    }

    /**
     * Registers the endpoint.
     * @param endpoint Endpoint to register
     * @throws EndpointAlreadyRegisteredException if endpoint was already registered
     */
    public void registerEndpoint(Endpoint endpoint) {
        this.kernel.registerEndpoint(endpoint);
    }

    /**
     * Unregisters the endpoint
     * @param endpoint Endpoint to unregister
     */
    public void unregisterEndpoint(Endpoint endpoint) {
        this.kernel.unregisterEndpoint(endpoint);
    }

    /**
     * Start the web server
     * @throws RuntimeException if server is already running
     */
    public void start() throws IOException {
        if (started) throw new RuntimeException("Web server is already running");
        started = true;
        server = HttpServer.create(new InetSocketAddress(this.port), 0);
        server.setExecutor(executor);
        server.createContext("/", kernel);
        logger.info(String.format("Started web server on port %s", port));
    }

    /**
     * Stops the web server
     * @throws RuntimeException if server is not started
     */
    public void stop() {
        if (!started) throw new RuntimeException("Web server is not currently started");
        server.stop(0);
        this.started = false;
    }

    /**
     * Returns true if server is started
     * @return true if server is started
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Gets the port the server is running on
     * @return Port number
     */
    public int getPort() {
        return port;
    }

    public static JankWebServer create(int port, Executor executor) throws IOException {
        return new JankWebServer(port, executor);
    }

    public static JankWebServer create(int port, int threads) throws IOException {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
        return JankWebServer.create(port, threadPoolExecutor);
    }

    public static JankWebServer create(int port) throws IOException {
        return JankWebServer.create(port, Math.max(Runtime.getRuntime().availableProcessors() / 2, 1));
    }

}
