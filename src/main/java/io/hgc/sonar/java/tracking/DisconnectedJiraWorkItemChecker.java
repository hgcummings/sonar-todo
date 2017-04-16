package io.hgc.sonar.java.tracking;

import java.util.Optional;

public class DisconnectedJiraWorkItemChecker implements JiraWorkItemChecker {
    @Override
    public Optional<WorkItem> lookupWorkItem(String itemId) {
        return Optional.of(dummyWorkItem);
    }

    private static WorkItem dummyWorkItem = () -> true;
}
