package mandelbrot.contexts;

public interface MandelbrotContext {
    void draw(int iterations, int x, int y);

    void draw(int iterations, int index);

    void printContext(long timeInMs);

    int maxIterations();

    int width();
}
