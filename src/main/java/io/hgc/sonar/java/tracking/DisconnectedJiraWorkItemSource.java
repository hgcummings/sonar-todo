package io.hgc.sonar.java.tracking;

import java.util.Optional;

class DisconnectedJiraWorkItemSource extends JiraWorkItemSource {
    private static WorkItem dummyWorkItem = () -> true;

    @Override
    public Optional<WorkItem> lookupWorkItem(String itemId) {
        return Optional.of(dummyWorkItem);
    }
}
