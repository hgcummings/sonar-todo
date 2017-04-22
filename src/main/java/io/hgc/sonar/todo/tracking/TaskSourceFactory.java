package io.hgc.sonar.todo.tracking;

import io.hgc.sonar.todo.plugin.TodoTrackingPlugin;
import org.sonar.api.batch.ScannerSide;
import org.sonar.api.config.Settings;
import org.sonarsource.api.sonarlint.SonarLintSide;

@ScannerSide
@SonarLintSide
public class TaskSourceFactory {
    public TaskSource createTaskSource(Settings settings) {
        String serverUrl = settings.getString(TodoTrackingPlugin.TASK_SOURCE_URL_PROPERTY_KEY);
        return JiraTaskSource.create(serverUrl);
    }
}
