package io.hgc.sonar.java.issues;

import java.util.Optional;
import java.util.regex.Pattern;

public interface IssueChecker {
    Optional<Issue> lookupIssue(String issueId);

    Pattern getIssueRegex();
}
