package io.hgc.sonar.java.tracking;

import com.google.common.io.Resources;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.fest.assertions.Assertions.assertThat;

public class ConnectedJiraWorkItemSourceTest {
    private static Server server;
    private static String serverUrl;
    private static String cannedResponseFilePath = null;

    private WorkItemSource workItemSource;

    @BeforeClass
    public static void startServer() throws Exception {
        server = new Server(0);
        server.setHandler(new StubHandler());
        server.start();
        serverUrl = String.format("http://127.0.0.1:%d/", ((ServerConnector)server.getConnectors()[0]).getLocalPort());
    }

    @Before
    public void setup() {
        cannedResponseFilePath = null;
        workItemSource = JiraWorkItemSource.create(serverUrl);
    }

    @Test
    public void workItemMissing_lookupWorkItem_returnsEmpty() {
        Optional<WorkItem> result = workItemSource.lookupWorkItem("ITEM-1");

        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    public void workItemOpen_lookupWorkItem_returnsOpenWorkItem() {
        cannedResponseFilePath = "jiraTodoIssueResponse.json";

        Optional<WorkItem> result = workItemSource.lookupWorkItem("ITEM-2");

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().isOpen()).isTrue();
    }

    @Test
    public void workItemClosed_lookupWorkItem_returnsOpenWorkItem() {
        cannedResponseFilePath = "jiraDoneIssueResponse.json";

        Optional<WorkItem> result = workItemSource.lookupWorkItem("ITEM-2");

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().isOpen()).isFalse();
    }

    @AfterClass
    public static void stopServer() throws Exception {
        server.stop();
    }

    private static class StubHandler extends AbstractHandler {
        @Override
        public void handle(String s,
                           Request baseRequest,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException, ServletException {
            if (cannedResponseFilePath != null) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);

                IOUtils.copy(
                        Resources.getResource(cannedResponseFilePath).openStream(),
                        response.getWriter());

                baseRequest.setHandled(true);
            }
        }
    }
}
