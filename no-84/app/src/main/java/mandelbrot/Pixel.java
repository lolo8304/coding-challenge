package mandelbrot;

public class Pixel {

    public double x;
    public double y;
    public int i;

    public Pixel(double x, double y) {
        this(x, y, 0);
    }

    public Pixel(double x, double y, int i) {
        this.x = x;
        this.y = y;
        this.i = i;
    }

    public Pixel add(Pixel step) {
        return new Pixel(this.x + step.x, this.y + step.y);
    }

    public void inc(Pixel step) {
        this.x += step.x;
        this.y += step.y;
    }
}
