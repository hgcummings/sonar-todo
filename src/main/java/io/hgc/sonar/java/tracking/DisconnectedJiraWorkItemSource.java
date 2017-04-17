package io.hgc.sonar.java.tracking;

import java.util.Optional;

/**
 * Work item source that identifies work items with a valid JIRA ticket number, but doesn't
 * actually connect to a JIRA instance to check their status (assumes all items are open).
 */
class DisconnectedJiraWorkItemSource extends JiraWorkItemSource {
    private static WorkItem dummyWorkItem = () -> true;

    @Override
    public Optional<WorkItem> lookupWorkItem(String itemId) {
        return Optional.of(dummyWorkItem);
    }
}
