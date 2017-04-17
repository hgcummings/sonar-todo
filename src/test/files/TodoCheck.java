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
        //TODO:CLOSED-222 Implement this
    }

    // Noncompliant@+1
    /**
     * My Third method.
     * Complete except for everything left todo.
     */
    public void myThirdMethod() {
        //TODO:OPEN-333 Implement this
    }

    // Noncompliant@+1
    /**
     * TODO:CLOSED-444 Implement this
     */
    public void myFourthMethod() {
        // Noncompliant@+1
        // Still a bit more todo here
    }

    /**
     * TODO:OPEN-555 Implement this
     */
    public void myFifthMethod() {
        // Noncompliant@+1
        //TODO: Implement this under OPEN-555
    }

    public void mySixthMethod() {
        // Noncompliant@+1
        //TODO:MISSING-666
    }
}