package io.hgc.sonar.todo.plugin;

import io.hgc.sonar.todo.tracking.Task;
import io.hgc.sonar.todo.tracking.TaskSource;
import io.hgc.sonar.todo.tracking.TaskSourceFactory;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.api.config.Settings;

import java.io.File;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import static io.hgc.sonar.todo.plugin.TodoTrackingRulesDefinition.*;
import static io.hgc.sonar.todo.plugin.testHelper.InputFileBuilder.createInputFile;
import static org.fest.assertions.Assertions.assertThat;

public class TodoTrackingSensorTest {
    private TodoTrackingSensor todoTrackingSensor;
    private SensorContextTester sensorContext;
    private StubTaskSource stubTaskSource;

    @Before
    public void setup() throws Exception {
        stubTaskSource = new StubTaskSource();
        todoTrackingSensor = new TodoTrackingSensor(new TaskSourceFactory() {
            @Override
            public TaskSource createTaskSource(Settings settings) {
                return stubTaskSource;
            }
        });
        File tempfile = File.createTempFile(UUID.randomUUID().toString(), "");
        tempfile.deleteOnExit();
        sensorContext = SensorContextTester.create(tempfile.getParentFile());
    }

    @Test
    public void todoWithoutTaskId_raisesTaskNotSpecifiedIssue() throws Exception {
        sensorContext.fileSystem().add(createInputFile()
                .withText("TODO")
                .build());

        todoTrackingSensor.execute(sensorContext);

        assertThat(sensorContext.allIssues()).hasSize(1);
        Issue issue = sensorContext.allIssues().iterator().next();
        assertThat(issue.ruleKey().repository()).isEqualTo(REPOSITORY_KEY);
        assertThat(issue.ruleKey().rule()).isEqualTo(TASK_NOT_SPECIFIED_KEY);
    }

    @Test
    public void todoWithClosedTaskId_raisesTaskClosedIssue() throws Exception {
        sensorContext.fileSystem().add(createInputFile().withText("TODO:123").build());
        stubTaskSource.setTask(new Task() {
            @Override
            public boolean isOpen() {
                return false;
            }
        });

        todoTrackingSensor.execute(sensorContext);

        assertThat(sensorContext.allIssues()).hasSize(1);
        Issue issue = sensorContext.allIssues().iterator().next();
        assertThat(issue.ruleKey().repository()).isEqualTo(REPOSITORY_KEY);
        assertThat(issue.ruleKey().rule()).isEqualTo(TASK_CLOSED_KEY);
    }

    @Test
    public void todoWithOpenTaskId_raisesTaskOpenIssue() throws Exception {
        sensorContext.fileSystem().add(createInputFile().withText("TODO:123").build());
        stubTaskSource.setTask(new Task() {
            @Override
            public boolean isOpen() {
                return true;
            }
        });

        todoTrackingSensor.execute(sensorContext);

        assertThat(sensorContext.allIssues()).hasSize(1);
        Issue issue = sensorContext.allIssues().iterator().next();
        assertThat(issue.ruleKey().repository()).isEqualTo(REPOSITORY_KEY);
        assertThat(issue.ruleKey().rule()).isEqualTo(TASK_OPEN_KEY);
    }

    @Test
    public void todoWithInvalidTaskId_raisesTaskMissingIssue() throws Exception {
        sensorContext.fileSystem().add(createInputFile().withText("TODO:123").build());

        todoTrackingSensor.execute(sensorContext);

        assertThat(sensorContext.allIssues()).hasSize(1);
        Issue issue = sensorContext.allIssues().iterator().next();
        assertThat(issue.ruleKey().repository()).isEqualTo(REPOSITORY_KEY);
        assertThat(issue.ruleKey().rule()).isEqualTo(TASK_MISSING_KEY);
    }

    @Test
    public void todoWithoutTaskId_highlightsTodoText() throws Exception {
        final int lineNumber = 7;
        final int offset = 19;
        final String text = "TODO";

        sensorContext.fileSystem().add(createInputFile()
                .withText(text)
                .atLine(lineNumber)
                .atOffset(offset)
                .build());

        todoTrackingSensor.execute(sensorContext);

        assertThat(sensorContext.allIssues()).hasSize(1);
        Issue issue = sensorContext.allIssues().iterator().next();
        TextRange highlight = issue.primaryLocation().textRange();
        assertThat(highlight.start().line()).isEqualTo(lineNumber);
        assertThat(highlight.end().line()).isEqualTo(lineNumber);
        assertThat(highlight.start().lineOffset()).isEqualTo(offset);
        assertThat(highlight.end().lineOffset()).isEqualTo(offset + text.length());
    }

    @Test
    public void todoWithTaskId_highlightsTodoTextAndTaskId() throws Exception {
        final int lineNumber = 7;
        final int offset = 19;
        final String text = "TODO:1234";

        sensorContext.fileSystem().add(createInputFile()
                .withText(text)
                .atLine(lineNumber)
                .atOffset(offset)
                .build());

        todoTrackingSensor.execute(sensorContext);

        assertThat(sensorContext.allIssues()).hasSize(1);
        Issue issue = sensorContext.allIssues().iterator().next();
        TextRange highlight = issue.primaryLocation().textRange();
        assertThat(highlight.start().line()).isEqualTo(lineNumber);
        assertThat(highlight.end().line()).isEqualTo(lineNumber);
        assertThat(highlight.start().lineOffset()).isEqualTo(offset);
        assertThat(highlight.end().lineOffset()).isEqualTo(offset + text.length());
    }
    
    private static class StubTaskSource implements TaskSource {
        private Task task;

        @Override
        public Optional<Task> lookupTask(String itemId) {
            return Optional.ofNullable(task);
        }

        @Override
        public Pattern getTaskIdRegex() {
            return Pattern.compile("[0-9]+");
        }

        public void setTask(Task task) {
            this.task = task;
        }
    }
}
