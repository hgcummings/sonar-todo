package org.sonar.template.java.issues;

import java.util.regex.Pattern;

public interface IssueChecker {
    Issue lookupIssue(String issueId);

    Pattern getIssueRegex();
}
