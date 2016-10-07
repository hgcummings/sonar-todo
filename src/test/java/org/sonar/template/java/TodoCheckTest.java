package org.sonar.template.java;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class TodoCheckTest {

    @Test
    public void test() {
        JavaCheckVerifier.verify("src/test/files/TodoCheck.java", new TodoCheck());
    }
}
