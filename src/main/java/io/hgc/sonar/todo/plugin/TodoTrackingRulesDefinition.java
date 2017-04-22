package io.hgc.sonar.todo.plugin;

import org.sonar.api.rule.Severity;
import org.sonar.api.server.rule.RulesDefinition;
/**
 * Declares rule metadata in server repository of rules, so they appear in the "Rules" page.
 */
public class TodoTrackingRulesDefinition implements RulesDefinition {

    static final String REPOSITORY_KEY = "todo-task-tracking";
    static final String TASK_CLOSED_KEY = "taskClosed";
    static final String TASK_MISSING_KEY = "taskMissing";
    static final String TASK_NOT_SPECIFIED_KEY = "noTaskSpecified";
    static final String TASK_OPEN_KEY = "taskOpen";

    @Override
    public void define(Context context) {
        NewRepository repository = context.createRepository(REPOSITORY_KEY, "java"); // TODO: Generalise this to other languages
        repository.setName("TODO tracking");
        repository.createRule("taskClosed")
                .setName("TODOs should be resolved before closing associated tasks.")
                .setMarkdownDescription("This TODO is associated with a task that has been marked as closed. Either re-open the task, or resolve the TODO.")
                .setSeverity(Severity.CRITICAL)
                .setActivatedByDefault(true);
        repository.createRule("taskMissing")
                .setName("TODOs should be associated with a valid task identifier.")
                .setMarkdownDescription("This TODO is associated with a task identifier that does not exist. Check and amend the task ID, or resolve the TODO.")
                .setSeverity(Severity.MAJOR)
                .setActivatedByDefault(true);
        repository.createRule("noTaskSpecified")
                .setName("TODOs should be associated with appropriate tasks.")
                .setMarkdownDescription("This TODO is not associated with a task. Either resolve the TODO or associate it with a task.")
                .setSeverity(Severity.MAJOR)
                .setActivatedByDefault(true);
        repository.createRule("taskOpen")
                .setName("Complete the task associated with this TODO.")
                .setMarkdownDescription("This TODO is associated with an open task. This TODO must be resolved before closing the task.")
                .setSeverity(Severity.INFO)
                .setActivatedByDefault(true);
        repository.done();
    }

}
