package io.hgc.sonar.todo.tracking;

import com.google.common.io.Resources;
import io.hgc.jarspec.JarSpecJUnitRunner;
import io.hgc.jarspec.Specification;
import io.hgc.jarspec.SpecificationNode;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(JarSpecJUnitRunner.class)
public class ConnectedJiraTaskSourceSpec implements Specification {
    private static String serverUrl;
    private static String cannedResponseFilePath = null;
    private static Map<String, String[]> requestParameters;

    private TaskSource taskSource;

    @Override
    public SpecificationNode root() {
        serverUrl = createServer();

        return describe("Connected JIRA task source",
            it("returns no tasks for missing issues", () -> {
                Optional<Task> result = taskSource.lookupTask("PROJ-1");

                assertThat(result).isEqualTo(Optional.empty());
            }),
            it("returns open tasks for in-progress issues", () -> {
                cannedResponseFilePath = "jiraTodoIssueResponse.json";

                Optional<Task> result = taskSource.lookupTask("PROJ-2");

                assertThat(result.isPresent()).isTrue();
                assertThat(result.get().isOpen()).isTrue();
            }),
            it("returns closed tasks for done issues", () -> {
                cannedResponseFilePath = "jiraDoneIssueResponse.json";

                Optional<Task> result = taskSource.lookupTask("PROJ-2");

                assertThat(result.isPresent()).isTrue();
                assertThat(result.get().isOpen()).isFalse();
            }),
            it("requests only relevant API fields", () -> {
                taskSource.lookupTask("PROJ-3");

                assertThat(requestParameters).isNotEmpty();
                assertThat(requestParameters.get("fields")).containsOnly("status");
            })
        ).withReset(() -> {
            requestParameters = null;
            cannedResponseFilePath = null;
            taskSource = JiraTaskSource.create(serverUrl);
        });
    }

    private String createServer() {
        Server server = new Server(0);
        server.setHandler(new StubHandler());
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return String.format("http://127.0.0.1:%d/",
                ((ServerConnector) server.getConnectors()[0]).getLocalPort());
    }

    private static class StubHandler extends AbstractHandler {
        @Override
        public void handle(String s,
                           Request baseRequest,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException, ServletException {
            requestParameters = request.getParameterMap();

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
