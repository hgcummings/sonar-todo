package io.hgc.sonar.java.tracking;

import java.util.regex.Pattern;

public interface JiraWorkItemChecker extends WorkItemChecker {
    //TODO: Move this into the concrete implementation
    Pattern workItemRegex = Pattern.compile("[A-Z]+-[0-9]+");

    default Pattern getWorkItemRegex() {
        return workItemRegex;
    }
}
