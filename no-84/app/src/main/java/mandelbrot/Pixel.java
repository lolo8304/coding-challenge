package mandelbrot;

public class Pixel {

    double x;
    double y;

    public Pixel(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Pixel add(Pixel step) {
        return new Pixel(this.x + step.x, this.y + step.y);
    }

    public void inc(Pixel step) {
        this.x += step.x;
        this.y += step.y;
    }
}
