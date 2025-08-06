package forth;

public interface ForthInterpreterOperationsLoop {
    void pushLoop(int start, int limit);
    boolean incrementLoop();
    void popLoop();
}
