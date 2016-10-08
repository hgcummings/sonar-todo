package io.hgc.sonar.java;

import com.google.common.collect.Iterables;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionAnnotationLoader;
//import org.sonar.plugins.java.Java; //TODO: Check why this is broken

import java.util.List;

/**
 * Declare rule metadata in server repository of rules. 
 * That allows to list the rules in the page "Rules".
 */
public class JavaCustomRulesDefinition implements RulesDefinition {

  public static final String REPOSITORY_KEY = "java-custom-rules-todo-issues";

  @Override
  public void define(Context context) {
    NewRepository repository = context.createRepository(REPOSITORY_KEY, "java"); // TODO, use Java.KEY
    repository.setName("Java Custom Rules - TODO Issues");

    List<Class> checks = JavaCustomRulesList.getChecks();
    new RulesDefinitionAnnotationLoader().load(repository, Iterables.toArray(checks, Class.class));
    repository.done();
  }

}
