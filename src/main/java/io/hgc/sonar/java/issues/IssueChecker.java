package io.hgc.sonar.java.issues;

import java.util.regex.Pattern;

public interface IssueChecker {
    Issue lookupIssue(String issueId);

    Pattern getIssueRegex();
}
