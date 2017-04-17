package io.hgc.sonar.java.tracking;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.fest.assertions.Assertions.assertThat;

public class DisconnectedWorkItemSourceTest {
    private WorkItemSource workItemSource;

    @Before
    public void setup() {
        workItemSource = JiraWorkItemSource.create(null);
    }

    @Test
    public void lookupWorkItem_returnsOpenWorkItem() {
        Optional<WorkItem> result = workItemSource.lookupWorkItem("ITEM-1");

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().isOpen()).isTrue();
    }
}
