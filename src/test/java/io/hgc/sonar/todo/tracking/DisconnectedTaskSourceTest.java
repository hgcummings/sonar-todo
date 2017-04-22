package io.hgc.sonar.todo.tracking;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.fest.assertions.Assertions.assertThat;

public class DisconnectedTaskSourceTest {
    private TaskSource taskSource;

    @Before
    public void setup() {
        taskSource = JiraTaskSource.create(null);
    }

    @Test
    public void lookupTask_returnsOpenTask() {
        Optional<Task> result = taskSource.lookupTask("PROJ-1");

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().isOpen()).isTrue();
    }
}
