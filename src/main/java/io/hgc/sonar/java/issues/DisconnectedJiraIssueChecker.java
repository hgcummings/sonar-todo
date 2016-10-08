package io.hgc.sonar.java.issues;

public class DisconnectedJiraIssueChecker implements JiraIssueChecker {
    @Override
    public Issue lookupIssue(String issueId) {
        return DUMMY_ISSUE;
    }

    private static Issue DUMMY_ISSUE = () -> true;
}
