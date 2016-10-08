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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TodoIssueCheckTest {
    private IssueChecker stubIssueChecker;

    @Before
    public void setup() {
        final Map<String, Issue> stubIssueData = new HashMap<>();
        stubIssueChecker = (JiraIssueChecker) stubIssueData::get;

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
        JavaCheckVerifier.verify("src/test/files/TodoCheck.java", new TodoIssueCheck(stubIssueChecker));
    }
}
