package io.hgc.sonar.java.tracking;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Looks up work items by their id in a project tracking tool such as JIRA
 */
public interface WorkItemSource {
    Optional<WorkItem> lookupWorkItem(String itemId);

    Pattern getWorkItemRegex();
}
