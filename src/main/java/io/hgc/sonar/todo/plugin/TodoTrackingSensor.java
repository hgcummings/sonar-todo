package io.hgc.sonar.todo.plugin;

import io.hgc.sonar.todo.tracking.Task;
import io.hgc.sonar.todo.tracking.TaskSource;
import io.hgc.sonar.todo.tracking.TaskSourceFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.batch.sensor.issue.internal.DefaultIssueLocation;
import org.sonar.api.rule.RuleKey;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.hgc.sonar.todo.plugin.TodoTrackingRulesDefinition.*;

/**
 * Sensor for checking whether TODOs are being tracked by corresponding tasks.
 */
public class TodoTrackingSensor implements Sensor {
    private static final Pattern todoRegex = Pattern.compile("TO-?DO", Pattern.CASE_INSENSITIVE);
    private final TaskSourceFactory taskSourceFactory;
    private TaskSource taskSource;

    public TodoTrackingSensor(TaskSourceFactory taskSourceFactory) {
        this.taskSourceFactory = taskSourceFactory;
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor
            .name(TodoTrackingSensor.class.getSimpleName())
            .createIssuesForRuleRepository(TodoTrackingRulesDefinition.REPOSITORY_KEY);
    }

    @Override
    public void execute(SensorContext context) {
        this.taskSource = taskSourceFactory.createTaskSource(context.settings());

        FileSystem fs = context.fileSystem();
        for (InputFile file : context.fileSystem().inputFiles(fs.predicates().all())) {
            try {
                checkFile(context, file);
            } catch (IOException e) {
                //TODO: Handle this properly
                e.printStackTrace();
            }
        }
    }

    private void checkFile(SensorContext context, InputFile file) throws IOException {
        LineIterator lines = IOUtils.lineIterator(file.inputStream(), file.charset().name());
        int lineNumber = 1;

        while (lines.hasNext()) {
            checkLine(context, file, lines.nextLine(), lineNumber++);
        }
    }

    private void checkLine(SensorContext context, InputFile file, String line, int lineNumber) {
        Matcher todoMatcher = todoRegex.matcher(line);
        Matcher taskMatcher = taskSource.getTaskIdRegex().matcher(line);

        while (todoMatcher.find()) {
            if (taskMatcher.find(todoMatcher.end()) && taskMatcher.start() == todoMatcher.end() + 1) {
                String taskId = taskMatcher.group();
                NewIssueLocation location = new DefaultIssueLocation()
                        .on(file)
                        .at(file.newRange(lineNumber, todoMatcher.start(), lineNumber, taskMatcher.end()));
                addIssueForTodoWithTaskId(context, taskId, location);
            } else {
                NewIssueLocation location = new DefaultIssueLocation()
                        .on(file)
                        .at(file.newRange(lineNumber, todoMatcher.start(), lineNumber, todoMatcher.end()));
                addIssueForTodoWithoutTaskId(context, location);
            }
        }
    }

    private void addIssueForTodoWithTaskId(SensorContext context, String taskId, NewIssueLocation location) {
        Optional<Task> foundTask = taskSource.lookupTask(taskId);
        if (foundTask.isPresent()) {
            if (foundTask.get().isOpen()) {
                context.newIssue().forRule(RuleKey.of(REPOSITORY_KEY, TASK_OPEN_KEY)).at(location).save();
            } else {
                context.newIssue().forRule(RuleKey.of(REPOSITORY_KEY, TASK_CLOSED_KEY)).at(location).save();
            }
        } else {
            context.newIssue().forRule(RuleKey.of(REPOSITORY_KEY, TASK_MISSING_KEY)).at(location).save();
        }
    }

    private void addIssueForTodoWithoutTaskId(SensorContext context, NewIssueLocation location) {
        context.newIssue().forRule(RuleKey.of(REPOSITORY_KEY, TASK_NOT_SPECIFIED_KEY)).at(location).save();
    }
}
