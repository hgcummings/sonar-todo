package io.hgc.sonar.java.checks;

import com.google.common.collect.ImmutableList;
import io.hgc.sonar.java.tracking.JiraWorkItemSource;
import io.hgc.sonar.java.tracking.WorkItem;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.SyntaxTrivia;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import io.hgc.sonar.java.tracking.WorkItemSource;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Rule(
    key = "TodoTrackingCheck",
    name = "All TODOs should be associated with open work items",
    description = "Every TODO should reference an open work item under which that TODO will be closed",
    priority = Priority.MAJOR,
    tags = {"tech-debt"}
)
public class TodoTrackingCheck extends IssuableSubscriptionVisitor {

    private static final Pattern todoRegex = Pattern.compile("TO-?DO", Pattern.CASE_INSENSITIVE);

    private WorkItemSource workItemSource;

    @RuleProperty(
        description = "Work item source URL. This will be the URL of your project-tracking system (e.g. JIRA)."
    )
    protected String serverUrl;

    public TodoTrackingCheck() {
        this.workItemSource = JiraWorkItemSource.create(serverUrl);
    }

    public TodoTrackingCheck(WorkItemSource workItemSource) {
        this.workItemSource = workItemSource;
    }

    @Override
    public List<Kind> nodesToVisit() {
        return ImmutableList.of(Kind.TRIVIA);
    }

    @Override
    public void visitTrivia(SyntaxTrivia trivia) {
        if (trivia.comment() == null) {
            //TODO: Why does IntelliJ think this can't happen (no @NotNull annotation in sight)
            return;
        }

        Matcher todoMatcher = todoRegex.matcher(trivia.comment());
        Matcher workItemMatcher = workItemSource.getWorkItemRegex().matcher(trivia.comment());
        if (todoMatcher.find()) {
            //TODO: Could be more clever here to get the precise line number within multi-line comments
            if (workItemMatcher.find(todoMatcher.end()) && workItemMatcher.start() == todoMatcher.end() + 1) {
                String workItemId = workItemMatcher.group();
                Optional<WorkItem> foundWorkItem = workItemSource.lookupWorkItem(workItemId);
                if (foundWorkItem.isPresent() && !foundWorkItem.get().isOpen()) {
                    addIssue(trivia.startLine(), "Complete this TODO, or associate it with an open work item " + workItemId);
                } else if (!foundWorkItem.isPresent()) {
                    addIssue(trivia.startLine(), "Associate this TODO with a valid work item " + workItemId);
                }
            } else {
                addIssue(trivia.startLine(), "Associate this TODO with a work item");
            }
        }
    }
}
