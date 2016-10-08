package org.sonar.template.java;

import org.junit.Before;
import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;
import org.sonar.template.java.issues.Issue;
import org.sonar.template.java.issues.IssueChecker;
import org.sonar.template.java.issues.JiraIssueChecker;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TodoCheckTest {
    private IssueChecker stubIssueChecker;

    @Before
    public void setup() {
        final Map<String, Issue> stubIssueData = new HashMap<>();

        stubIssueChecker = new JiraIssueChecker() {
            @Override
            public Issue lookupIssue(String issueId) {
                return stubIssueData.get(issueId);
            }
        };

        Issue openIssue = mock(Issue.class);
        Issue closedIssue = mock(Issue.class);
        when(openIssue.isOpen()).thenReturn(true);
        when(closedIssue.isOpen()).thenReturn(false);

        for (int i = 1; i < 10; ++i) {
            Issue issueToReturn = i % 2 == 0 ? closedIssue : openIssue;
            stubIssueData.put(String.format("PROJ-%d", i * 111), issueToReturn);
        }
    }

    @Test
    public void test() {
        JavaCheckVerifier.verify("src/test/files/TodoCheck.java", new TodoCheck(stubIssueChecker));
    }
}
