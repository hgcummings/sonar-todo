package io.hgc.sonar.java;

import org.junit.Before;
import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;
import io.hgc.sonar.java.checks.TodoIssueCheck;
import io.hgc.sonar.java.issues.Issue;
import io.hgc.sonar.java.issues.IssueChecker;
import io.hgc.sonar.java.issues.JiraIssueChecker;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TodoIssueCheckTest {
    private IssueChecker stubIssueChecker;

    @Before
    public void setup() {
        Issue openIssue = mock(Issue.class);
        Issue closedIssue = mock(Issue.class);
        when(openIssue.isOpen()).thenReturn(true);
        when(closedIssue.isOpen()).thenReturn(false);

        stubIssueChecker = (JiraIssueChecker) issueId -> {
            if (issueId.startsWith("OPEN")) {
                return Optional.of(openIssue);
            } else if (issueId.startsWith("CLOSED")) {
                return Optional.of(closedIssue);
            } else {
                return Optional.empty();
            }
        };
    }

    @Test
    public void test() {
        JavaCheckVerifier.verify("src/test/files/TodoCheck.java", new TodoIssueCheck(stubIssueChecker));
    }
}
