package io.hgc.sonar.java;

import org.junit.Before;
import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;
import io.hgc.sonar.java.checks.TodoTrackingCheck;
import io.hgc.sonar.java.tracking.WorkItem;
import io.hgc.sonar.java.tracking.WorkItemSource;
import io.hgc.sonar.java.tracking.JiraWorkItemSource;

import java.util.Optional;
import java.util.regex.Pattern;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TodoTrackingCheckTest {
    private WorkItemSource stubWorkItemSource;

    @Before
    public void setup() {
        WorkItem openWorkItem = mock(WorkItem.class);
        WorkItem closedWorkItem = mock(WorkItem.class);
        when(openWorkItem.isOpen()).thenReturn(true);
        when(closedWorkItem.isOpen()).thenReturn(false);

        stubWorkItemSource = new JiraWorkItemSource() {
            @Override
            public Optional<WorkItem> lookupWorkItem(String itemId) {
                if (itemId.startsWith("OPEN")) {
                    return Optional.of(openWorkItem);
                } else if (itemId.startsWith("CLOSED")) {
                    return Optional.of(closedWorkItem);
                } else {
                    return Optional.empty();
                }
            }
        };
    }

    @Test
    public void test() {
        JavaCheckVerifier.verify("src/test/files/TodoCheck.java", new TodoTrackingCheck(stubWorkItemSource));
    }
}
