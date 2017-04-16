package io.hgc.sonar.java.checks;

import com.google.common.collect.ImmutableList;
import io.hgc.sonar.java.issues.Issue;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.SyntaxTrivia;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import io.hgc.sonar.java.issues.DisconnectedJiraIssueChecker;
import io.hgc.sonar.java.issues.IssueChecker;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Rule(
    key = "TodoIssueCheck",
    name = "All TODOs should be associated with open issues",
    description = "Every TODO should reference an open issue under which that TODO will be closed",
    priority = Priority.MAJOR,
    tags = {"tech-debt"}
)
public class TodoIssueCheck extends IssuableSubscriptionVisitor {

    private static final Pattern todoRegex = Pattern.compile("TO-?DO", Pattern.CASE_INSENSITIVE);

    private IssueChecker issueChecker;

    public TodoIssueCheck() {
        this(new DisconnectedJiraIssueChecker());
    }

    //TODO: Work out how to initialise this within Sonar
    public TodoIssueCheck(IssueChecker issueChecker) {
        this.issueChecker = issueChecker;
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
        Matcher issueMatcher = issueChecker.getIssueRegex().matcher(trivia.comment());
        if (todoMatcher.find()) {
            //TODO: Could be more clever here to get the precise line number within multi-line comments
            if (issueMatcher.find(todoMatcher.end()) && issueMatcher.start() == todoMatcher.end() + 1) {
                String issueId = issueMatcher.group();
                Optional<Issue> foundIssue = issueChecker.lookupIssue(issueId);
                if (foundIssue.isPresent()) {
                    if (!foundIssue.get().isOpen()) {
                        addIssue(trivia.startLine(), "Found TODO associated closed issue " + issueId);
                    }
                }
            } else {
                addIssue(trivia.startLine(), "Found TODO without associated issue");
            }
        }
    }
}
