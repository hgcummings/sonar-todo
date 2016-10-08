package io.hgc.sonar.java.issues;

import java.util.regex.Pattern;

public interface JiraIssueChecker extends IssueChecker {
    //TODO: Move this into the issue checker
    Pattern issueRegex = Pattern.compile("[A-Z]+-[0-9]+");

    default Pattern getIssueRegex() {
        return issueRegex;
    }
}
