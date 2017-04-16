package io.hgc.sonar.java;

import org.junit.Test;
import org.sonar.api.server.rule.RulesDefinition;

import static org.fest.assertions.Assertions.assertThat;

public class JavaCustomRulesDefinitionTest {

  @Test
  public void registration_test() {
    JavaCustomRulesDefinition definition = new JavaCustomRulesDefinition();
    RulesDefinition.Context context = new RulesDefinition.Context();
    definition.define(context);
    String repositoryKey = "java-custom-rule-todo-tracking";
    RulesDefinition.Repository repository = context.repository(repositoryKey);

    assertThat(repository.key()).isEqualTo(repositoryKey);
    assertThat(repository.name()).isNotEmpty();
    assertThat(repository.language()).isEqualTo("java");
    assertThat(repository.rules()).hasSize(JavaCustomRulesList.getChecks().size());
  }
}
