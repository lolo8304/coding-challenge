package mandelbrot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MandelbrotExplorer {

    private final int width, maxIterations;
    private final int dpi;
    private final String fileName;
    private final Pixel start;
    private final Pixel end;

    public MandelbrotExplorer(int maxIterations, int width, int dpi, String fileName) {
        this.maxIterations = maxIterations;
        this.width = width;
        this.dpi = dpi;
        this.fileName = fileName;
        this.start = new Pixel(-1.75,-1.12);
        this.end = new Pixel(0.47,1.12);
    }

    public void run() {

        var context = new MandelbrotContext(this.width, this.maxIterations);

        // Create a virtual thread executor
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int y = 0; y < this.width; y++) {
                for (int x = 0; x < this.width; x++) {
                    executor.submit(new Mandel(context, this.start, this.end, new Pixel(x, y), this.width, this.width, maxIterations));
                }
            }
            context.printContext();
        }
    }

}
