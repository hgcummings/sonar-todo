package io.hgc.sonar.java.tracking;

import javax.json.JsonObject;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Optional;

/**
 * Looks up work items in a JIRA instance, using anonymous authentication
 *
 * TODO:SONTO-5 Allow other authentication mechanisms
 */
class ConnectedJiraWorkItemSource extends JiraWorkItemSource {
    private final WebTarget webTarget;

    ConnectedJiraWorkItemSource(WebTarget webTarget) {
        this.webTarget = webTarget;
    }

    @Override
    public Optional<WorkItem> lookupWorkItem(String itemId) {
        Response response = webTarget
                .path(String.format("/rest/api/2/issue/%s?fields=status", itemId))
                .request()
                .get();

        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            JsonObject entity = response.readEntity(JsonObject.class);
            String status = entity
                    .getJsonObject("fields")
                    .getJsonObject("status")
                    .getJsonObject("statusCategory")
                    .getString("key");

            return Optional.of(new JiraWorkItem(status));
        } else {
            return Optional.empty();
        }
    }

    private static class JiraWorkItem implements WorkItem {
        private final String status;

        JiraWorkItem(String status) {
            this.status = status;
        }

        @Override
        public boolean isOpen() {
            return !"done".equals(status);
        }
    }
}
