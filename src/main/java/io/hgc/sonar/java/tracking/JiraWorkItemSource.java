package io.hgc.sonar.java.tracking;

import org.apache.commons.lang.StringUtils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.regex.Pattern;

/**
 * Identifies JIRA work items
 */
public abstract class JiraWorkItemSource implements WorkItemSource {
    private static Pattern workItemRegex = Pattern.compile("[A-Z]+-[0-9]+");

    public Pattern getWorkItemRegex() {
        return workItemRegex;
    }

    public static WorkItemSource create(String serverUrl) {
        if (StringUtils.isNotEmpty(serverUrl)) {
            Client client = ClientBuilder.newClient();
            WebTarget webTarget = client.target(serverUrl);
            return new ConnectedJiraWorkItemSource(webTarget);
        } else {
            return new DisconnectedJiraWorkItemSource();
        }
    }
}
