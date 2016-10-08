package org.sonar.template.java.issues;

public class DisconnectedJiraIssueChecker extends JiraIssueChecker {
    @Override
    public Issue lookupIssue(String issueId) {
        return DUMMY_ISSUE;
    }

    private static Issue DUMMY_ISSUE = new Issue() {
        @Override
        public boolean isOpen() {
            return true;
        }
    };
}
