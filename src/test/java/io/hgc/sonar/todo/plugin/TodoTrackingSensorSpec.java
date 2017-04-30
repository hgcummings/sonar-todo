package io.hgc.sonar.todo.plugin;

import io.hgc.jarspec.JarSpecJUnitRunner;
import io.hgc.jarspec.Specification;
import io.hgc.jarspec.SpecificationNode;
import io.hgc.sonar.todo.tracking.Task;
import io.hgc.sonar.todo.tracking.TaskSource;
import io.hgc.sonar.todo.tracking.TaskSourceFactory;
import org.junit.runner.RunWith;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.sensor.issue.Issue;
import org.sonar.api.config.Settings;

import java.util.Optional;
import java.util.regex.Pattern;

import static io.hgc.sonar.todo.plugin.TodoTrackingRulesDefinition.*;
import static io.hgc.sonar.todo.plugin.testHelper.InputFileBuilder.createInputFile;
import static org.fest.assertions.Assertions.assertThat;

@RunWith(JarSpecJUnitRunner.class)
public class TodoTrackingSensorSpec implements Specification {
    @Override
    public SpecificationNode root() {
        return describe("TodoTrackingSensor",
            describe("For TODOs without a task ID",
                it("highlights the TODO text", () -> {
                    final TodoTrackingSensor todoTrackingSensor =
                            new TodoTrackingSensor(new StubTaskSourceFactory());
                    final int lineNumber = 7;
                    final int offset = 19;
                    final String text = "TODO";
                    final SensorContextTester sensorContext = createSensorContextWithFile(
                            createInputFile().containingText(text).atLine(lineNumber).atOffset(offset).build());

                    todoTrackingSensor.execute(sensorContext);

                    assertThat(sensorContext.allIssues()).hasSize(1);
                    Issue issue = sensorContext.allIssues().iterator().next();
                    TextRange highlight = issue.primaryLocation().textRange();
                    assertThat(highlight.start().line()).isEqualTo(lineNumber);
                    assertThat(highlight.end().line()).isEqualTo(lineNumber);
                    assertThat(highlight.start().lineOffset()).isEqualTo(offset);
                    assertThat(highlight.end().lineOffset()).isEqualTo(offset + text.length());
                }),
                it("raises a 'task not specified' issue", () -> {
                    final TodoTrackingSensor todoTrackingSensor =
                            new TodoTrackingSensor(new StubTaskSourceFactory());
                    final SensorContextTester sensorContext = createSensorContextWithFile(
                            createInputFile().containingText("TODO").build());

                    todoTrackingSensor.execute(sensorContext);

                    assertThat(sensorContext.allIssues()).hasSize(1);
                    Issue issue = sensorContext.allIssues().iterator().next();
                    assertThat(issue.ruleKey().repository()).isEqualTo(REPOSITORY_KEY);
                    assertThat(issue.ruleKey().rule()).isEqualTo(TASK_NOT_SPECIFIED_KEY);
                })
            ),
            describe("For TODOs with a task ID",
                it("highlights the TODO text and task ID", () -> {
                    final int lineNumber = 7;
                    final int offset = 19;
                    final String text = "TODO:1234";
                    final TodoTrackingSensor todoTrackingSensor =
                            new TodoTrackingSensor(new StubTaskSourceFactory());
                    final SensorContextTester sensorContext = createSensorContextWithFile(
                            createInputFile().containingText(text).atLine(lineNumber).atOffset(offset).build());

                    todoTrackingSensor.execute(sensorContext);

                    assertThat(sensorContext.allIssues()).hasSize(1);
                    Issue issue = sensorContext.allIssues().iterator().next();
                    TextRange highlight = issue.primaryLocation().textRange();
                    assertThat(highlight.start().line()).isEqualTo(lineNumber);
                    assertThat(highlight.end().line()).isEqualTo(lineNumber);
                    assertThat(highlight.start().lineOffset()).isEqualTo(offset);
                    assertThat(highlight.end().lineOffset()).isEqualTo(offset + text.length());
                }),
                it("raises a task missing issue if the ID is not valid", () -> {
                    final TodoTrackingSensor todoTrackingSensor =
                            new TodoTrackingSensor(new StubTaskSourceFactory());
                    final SensorContextTester sensorContext = createSensorContextWithFile(
                            createInputFile().containingText("TODO:123").build());

                    todoTrackingSensor.execute(sensorContext);

                    assertThat(sensorContext.allIssues()).hasSize(1);
                    Issue issue = sensorContext.allIssues().iterator().next();
                    assertThat(issue.ruleKey().repository()).isEqualTo(REPOSITORY_KEY);
                    assertThat(issue.ruleKey().rule()).isEqualTo(TASK_MISSING_KEY);
                }),
                it("raises a 'task closed' issue if the corresponding task is closed", () -> {
                    final TodoTrackingSensor todoTrackingSensor =
                            new TodoTrackingSensor(new StubTaskSourceFactory(new Task() {
                                @Override
                                public boolean isOpen() { return false; }
                            }));
                    final SensorContextTester sensorContext = createSensorContextWithFile(
                            createInputFile().containingText("TODO:123").build());

                    todoTrackingSensor.execute(sensorContext);

                    assertThat(sensorContext.allIssues()).hasSize(1);
                    Issue issue = sensorContext.allIssues().iterator().next();
                    assertThat(issue.ruleKey().repository()).isEqualTo(REPOSITORY_KEY);
                    assertThat(issue.ruleKey().rule()).isEqualTo(TASK_CLOSED_KEY);
                }),
                it("raises a 'task open' issue if the corresponding task is open", () -> {
                    final TodoTrackingSensor todoTrackingSensor =
                            new TodoTrackingSensor(new StubTaskSourceFactory(new Task() {
                                @Override
                                public boolean isOpen() {return true; }
                            }));
                    final SensorContextTester sensorContext = createSensorContextWithFile(
                            createInputFile().containingText("TODO:123").build());

                    todoTrackingSensor.execute(sensorContext);

                    assertThat(sensorContext.allIssues()).hasSize(1);
                    Issue issue = sensorContext.allIssues().iterator().next();
                    assertThat(issue.ruleKey().repository()).isEqualTo(REPOSITORY_KEY);
                    assertThat(issue.ruleKey().rule()).isEqualTo(TASK_OPEN_KEY);
                })
            )
        );
    }

    private static class StubTaskSourceFactory extends TaskSourceFactory {
        private TaskSource stubTaskSource;

        private StubTaskSourceFactory() {
            this.stubTaskSource = new StubTaskSource(null);
        }

        private StubTaskSourceFactory(Task task) {
            this.stubTaskSource = new StubTaskSource(task);
        }

        @Override
        public TaskSource createTaskSource(Settings settings) {
            return stubTaskSource;
        }

        private class StubTaskSource implements TaskSource {
            private Task task;

            private StubTaskSource(Task task) {
                this.task = task;
            }

            @Override
            public Optional<Task> lookupTask(String itemId) {
                return Optional.ofNullable(task);
            }

            @Override
            public Pattern getTaskIdRegex() {
                return Pattern.compile("[0-9]+");
            }
        }
    }

    public static SensorContextTester createSensorContextWithFile(InputFile inputFile) {
        SensorContextTester sensorContext = SensorContextTester.create(inputFile.file().getParentFile());
        sensorContext.fileSystem().add(inputFile);
        return sensorContext;
    }
}
