package io.hgc.sonar.todo.tracking;

import javax.json.JsonObject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 * Looks up tasks in a JIRA instance, using anonymous authentication
 *
 * TODO:SONTO-3 Allow other authentication mechanisms
 */
class ConnectedJiraTaskSource extends JiraTaskSource {
    private final WebTarget webTarget;

    ConnectedJiraTaskSource(WebTarget webTarget) {
        this.webTarget = webTarget;
    }

    @Override
    public Optional<Task> lookupTask(String itemId) {
        Response response = webTarget
                .path(String.format("/rest/api/2/issue/%s", itemId))
                .queryParam("fields", "status")
                .request()
                .get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            JsonObject entity = response.readEntity(JsonObject.class);
            String status = entity
                    .getJsonObject("fields")
                    .getJsonObject("status")
                    .getJsonObject("statusCategory")
                    .getString("key");

            return Optional.of(new JiraTask(status));
        } else {
            return Optional.empty();
        }
    }

    private static class JiraTask implements Task {
        private final String status;

        JiraTask(String status) {
            this.status = status;
        }

        @Override
        public boolean isOpen() {
            return !"done".equals(status);
        }
    }
}
