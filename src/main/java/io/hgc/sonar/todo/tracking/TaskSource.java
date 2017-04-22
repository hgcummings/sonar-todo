package io.hgc.sonar.todo.tracking;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Looks up tasks by their id in a project tracking tool such as JIRA
 */
public interface TaskSource {
    Optional<Task> lookupTask(String itemId);

    Pattern getTaskIdRegex();
}
