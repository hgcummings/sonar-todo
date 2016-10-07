package org.sonar.template.java;

import com.google.common.collect.ImmutableList;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.Tree.Kind;

import java.util.List;

public class TodoCheck extends IssuableSubscriptionVisitor {

    // TODO:Initialise this properly

    @Override
    public List<Kind> nodesToVisit() {
        return ImmutableList.of(Kind.TRIVIA);
    }
}
