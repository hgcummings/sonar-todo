package org.sonar.template.java;

import com.google.common.collect.ImmutableList;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.SyntaxTrivia;
import org.sonar.plugins.java.api.tree.Tree.Kind;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TodoCheck extends IssuableSubscriptionVisitor {

    private static final Pattern todoRegex = Pattern.compile("[Tt][Oo]-?[Dd][Oo](?!:[A-Z]+-[0-9]+)", Pattern.DOTALL);

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

        Matcher matcher = todoRegex.matcher(trivia.comment());
        if (matcher.find()) {
            //TODO: Could be more clever here to get the precise line number within multi-line comments
            addIssue(trivia.startLine(), "Found TODO without JIRA ticket");
        }
    }
}
