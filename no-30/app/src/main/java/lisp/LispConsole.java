package lisp;

import lisp.parser.LispRuntime;

public class LispConsole {

    private final LispRuntime runtime;

    public LispConsole() {
        this.runtime = new LispRuntime();
    }
}
