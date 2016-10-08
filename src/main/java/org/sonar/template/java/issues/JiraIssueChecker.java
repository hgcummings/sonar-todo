package org.sonar.template.java.issues;

import java.util.regex.Pattern;

public abstract class JiraIssueChecker implements IssueChecker {
    //TODO: Move this into the issue checker
    private static final Pattern issueRegex = Pattern.compile("[A-Z]+-[0-9]+");

    public Pattern getIssueRegex() {
        return issueRegex;
    }
}
