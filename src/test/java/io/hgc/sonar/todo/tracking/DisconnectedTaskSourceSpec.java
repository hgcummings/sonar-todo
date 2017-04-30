package io.hgc.sonar.todo.tracking;

import io.hgc.jarspec.JarSpecJUnitRunner;
import io.hgc.jarspec.Specification;
import io.hgc.jarspec.SpecificationNode;
import org.junit.runner.RunWith;

import java.util.Optional;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(JarSpecJUnitRunner.class)
public class DisconnectedTaskSourceSpec implements Specification {
    @Override
    public SpecificationNode root() {
        return describe("Disconnected task source", () -> {
            TaskSource taskSource = JiraTaskSource.create(null);
            return it("returns open tasks", () -> {
                Optional<Task> result = taskSource.lookupTask("PROJ-1");

                assertThat(result.isPresent()).isTrue();
                assertThat(result.get().isOpen()).isTrue();
            });
        });
    }
}
