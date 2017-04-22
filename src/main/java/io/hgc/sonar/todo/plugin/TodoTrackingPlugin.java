package io.hgc.sonar.todo.plugin;

import io.hgc.sonar.todo.tracking.TaskSourceFactory;
import org.sonar.api.Plugin;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;

/**
 * Entry point of the plugin. Registers configuration properties and plugin components.
 */
public class TodoTrackingPlugin implements Plugin {
    public static final String TASK_SOURCE_URL_PROPERTY_KEY = "sonar.todoTracking.taskSourceUrl";

    @Override
    public void define(Context context) {
        context.addExtensions(
                PropertyDefinition.builder(TASK_SOURCE_URL_PROPERTY_KEY)
                        .index(0)
                        .name("Task source URL")
                        .description("This will be the URL of your task-tracking system (e.g. JIRA)")
                        .type(PropertyType.STRING)
                        .build(),
                TodoTrackingRulesDefinition.class,
                TodoTrackingSensor.class,
                TaskSourceFactory.class
        );
    }
}
