package io.hgc.sonar.java.tracking;

import java.util.Optional;
import java.util.regex.Pattern;

public interface WorkItemSource {
    Optional<WorkItem> lookupWorkItem(String itemId);

    Pattern getWorkItemRegex();
}
