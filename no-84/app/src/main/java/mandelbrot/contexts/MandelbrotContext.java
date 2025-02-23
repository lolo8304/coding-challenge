package mandelbrot.contexts;

import mandelbrot.MandelbrotExplorer;

public interface MandelbrotContext {
    void draw(int iterations, int x, int y);

    void draw(int iterations, int index);

    void printContext(long timeInMs);

    int maxIterations();

    int width();

    MandelbrotExplorer explorer();
}
