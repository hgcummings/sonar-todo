package io.hgc.sonar.java.checks;

import com.google.common.collect.ImmutableList;
import io.hgc.sonar.java.tracking.WorkItem;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.SyntaxTrivia;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import io.hgc.sonar.java.tracking.DisconnectedJiraWorkItemChecker;
import io.hgc.sonar.java.tracking.WorkItemChecker;

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

    private WorkItemChecker workItemChecker;

    public TodoTrackingCheck() {
        this(new DisconnectedJiraWorkItemChecker());
    }

    //TODO: Work out how to initialise this within Sonar
    public TodoTrackingCheck(WorkItemChecker workItemChecker) {
        this.workItemChecker = workItemChecker;
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
        Matcher workItemMatcher = workItemChecker.getWorkItemRegex().matcher(trivia.comment());
        if (todoMatcher.find()) {
            //TODO: Could be more clever here to get the precise line number within multi-line comments
            if (workItemMatcher.find(todoMatcher.end()) && workItemMatcher.start() == todoMatcher.end() + 1) {
                String workItemId = workItemMatcher.group();
                Optional<WorkItem> foundWorkItem = workItemChecker.lookupWorkItem(workItemId);
                if (foundWorkItem.isPresent() && !foundWorkItem.get().isOpen()) {
                    addIssue(trivia.startLine(), "Found TODO associated closed work item " + workItemId);
                }
            } else {
                addIssue(trivia.startLine(), "Found TODO without associated work item");
            }
        }
    }
}
