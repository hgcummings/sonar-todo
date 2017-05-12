package io.hgc.sonar.todo.tracking;

import io.hgc.jarspec.JarSpecJUnitRunner;
import io.hgc.jarspec.Specification;
import io.hgc.jarspec.SpecificationNode;
import io.hgc.sonar.todo.support.MockHttpApi;
import io.hgc.sonar.todo.support.TestServer;
import org.junit.runner.RunWith;

import java.util.Optional;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(JarSpecJUnitRunner.class)
public class ConnectedJiraTaskSourceSpec implements Specification {
    @Override
    public SpecificationNode root() {
        MockHttpApi mockHttpApi = new MockHttpApi();
        TestServer server = new TestServer(mockHttpApi);

        return describe("Connected JIRA task source",
            it("returns no tasks for missing issues", () -> {
                Optional<Task> result = taskSource(server).lookupTask("PROJ-1");

                assertThat(result).isEqualTo(Optional.empty());
            }),
            it("returns open tasks for in-progress issues", () -> {
                mockHttpApi.setCannedResponseFilePath("jiraTodoIssueResponse.json");

                Optional<Task> result = taskSource(server).lookupTask("PROJ-2");

                assertThat(result.isPresent()).isTrue();
                assertThat(result.get().isOpen()).isTrue();
            }),
            it("returns closed tasks for done issues", () -> {
                mockHttpApi.setCannedResponseFilePath("jiraDoneIssueResponse.json");

                Optional<Task> result = taskSource(server).lookupTask("PROJ-2");

                assertThat(result.isPresent()).isTrue();
                assertThat(result.get().isOpen()).isFalse();
            }),
            it("requests only relevant API fields", () -> {
                taskSource(server).lookupTask("PROJ-3");

                assertThat(mockHttpApi.getLastRequestParameters()).isNotEmpty();
                assertThat(mockHttpApi.getLastRequestParameters().get("fields")).containsOnly("status");
            })
        ).withBlockRule(server).withRule(mockHttpApi);
    }

    private static TaskSource taskSource(TestServer server) {
        return JiraTaskSource.create(server.getServerUrl());
    }
}
