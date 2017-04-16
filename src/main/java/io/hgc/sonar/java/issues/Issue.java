package io.hgc.sonar.java.issues;

/**
 * Represents an issue in an issue-tracking system such as Jira
 *
 * TODO: The word "issue" now has two meanings in this codebase (as it also has a specific
 * meaning in SonarQube). Should rename this concept to something else (e.g. "WorkItem").
 */
public interface Issue {
    boolean isOpen();
}
