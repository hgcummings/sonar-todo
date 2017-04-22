package io.hgc.sonar.todo.tracking;

import org.apache.commons.lang.StringUtils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.regex.Pattern;

/**
 * Identifies JIRA tasks
 */
public abstract class JiraTaskSource implements TaskSource {
    private static Pattern taskIdRegex = Pattern.compile("[A-Z]+-[0-9]+");

    @Override
    public Pattern getTaskIdRegex() {
        return taskIdRegex;
    }

    public static TaskSource create(String serverUrl) {
        if (StringUtils.isNotEmpty(serverUrl)) {
            Client client = ClientBuilder.newClient();
            WebTarget webTarget = client.target(serverUrl);
            return new ConnectedJiraTaskSource(webTarget);
        } else {
            return new DisconnectedJiraTaskSource();
        }
    }
}
