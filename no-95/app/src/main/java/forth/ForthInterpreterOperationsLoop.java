package forth;

public interface ForthInterpreterOperationsLoop {
    public void pushLoop(int start, int limit);
    public boolean incrementLoop();
    public void popLoop();
}
