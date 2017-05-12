package io.hgc.sonar.todo.support;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.junit.rules.ExternalResource;

public class TestServer extends ExternalResource {
    private final Handler handler;
    private Server server;
    private String serverUrl;

    public TestServer(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void before() {
        server = new Server(0);
        server.setHandler(handler);
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        serverUrl = String.format("http://127.0.0.1:%d/",
                ((ServerConnector) server.getConnectors()[0]).getLocalPort());
    }

    @Override
    public void after() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getServerUrl() {
        return serverUrl;
    }
}
