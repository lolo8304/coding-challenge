package forth;

public interface ForthInterpreterOperationsLoop {
    void pushLoop(long start, long limit);
    boolean incrementLoop();
    void popLoop();
}
