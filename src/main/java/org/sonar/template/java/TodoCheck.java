package org.sonar.template.java;

import com.google.common.collect.ImmutableList;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.SyntaxTrivia;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.template.java.issues.DisconnectedJiraIssueChecker;
import org.sonar.template.java.issues.IssueChecker;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TodoCheck extends IssuableSubscriptionVisitor {

    private static final Pattern todoRegex = Pattern.compile("TO-?DO", Pattern.CASE_INSENSITIVE);

    private IssueChecker issueChecker;

    public TodoCheck() {
        this(new DisconnectedJiraIssueChecker());
    }

    //TODO: Work out how to initialise this within Sonar
    public TodoCheck(IssueChecker issueChecker) {
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
                if (!issueChecker.lookupIssue(issueId).isOpen()) {
                    addIssue(trivia.startLine(), "Found TODO associated closed issue " + issueId);
                }
            } else {
                addIssue(trivia.startLine(), "Found TODO without associated issue");
            }
        }
    }
}
