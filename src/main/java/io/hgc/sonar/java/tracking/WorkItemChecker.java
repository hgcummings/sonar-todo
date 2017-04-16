package io.hgc.sonar.java.tracking;

import java.util.Optional;
import java.util.regex.Pattern;

public interface WorkItemChecker {
    Optional<WorkItem> lookupWorkItem(String itemId);

    Pattern getWorkItemRegex();
}
