public class MyClass {
    public void myFirstMethod() {
        // Noncompliant@+1
        // TO-DO: Implement this
    }

    // Noncompliant@+1
    /**
     * TODO: Implement this 22222
     */
    public void mySecondMethod() {
        // Noncompliant@+1
        //TODO:PROJ-222 Implement this
    }

    // Noncompliant@+1
    /**
     * My Third method.
     * Complete except for everything left todo.
     */
    public void myThirdMethod() {
        //TODO:PROJ-333 Implement this
    }

    // Noncompliant@+1
    /**
     * TODO:PROJ-444 Implement this
     */
    public void myFourthMethod() {
        // Noncompliant@+1
        // Still a bit more todo here
    }

    /**
     * TODO:PROJ-555 Implement this
     */
    public void myFourthMethod() {
        // Noncompliant@+1
        //TODO: Implement this under PROJ-777
    }
}