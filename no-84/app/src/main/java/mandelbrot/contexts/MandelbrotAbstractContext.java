package mandelbrot.contexts;

import mandelbrot.MandelbrotExplorer;

public abstract class MandelbrotAbstractContext implements  MandelbrotContext {

    private final int width;
    private final float max;
    private final MandelbrotExplorer explorer;

    public MandelbrotAbstractContext(MandelbrotExplorer explorer, int width, int max) {
        this.explorer = explorer;
        this.width = width;
        this.max = (float) max;
    }

    public void draw(int iterations, int x, int y) {
        var i = y * this.width + x;
        this.draw(iterations, i);
    }

    @Override
    public int maxIterations() {
        return (int)this.max;
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public MandelbrotExplorer explorer() {
        return this.explorer;
    }
}
