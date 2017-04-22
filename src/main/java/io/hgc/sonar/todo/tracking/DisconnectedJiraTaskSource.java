package io.hgc.sonar.todo.tracking;

import java.util.Optional;

/**
 * Task source that identifies tasks with a valid JIRA ticket number, but doesn't
 * actually connect to a JIRA instance to check their status (assumes all items are open).
 */
class DisconnectedJiraTaskSource extends JiraTaskSource {
    private static Task dummyTask = () -> true;

    @Override
    public Optional<Task> lookupTask(String itemId) {
        return Optional.of(dummyTask);
    }
}
