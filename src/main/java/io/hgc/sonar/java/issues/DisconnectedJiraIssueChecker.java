package io.hgc.sonar.java.issues;

import java.util.Optional;

public class DisconnectedJiraIssueChecker implements JiraIssueChecker {
    @Override
    public Optional<Issue> lookupIssue(String issueId) {
        return Optional.of(DUMMY_ISSUE);
    }

    private static Issue DUMMY_ISSUE = () -> true;
}
