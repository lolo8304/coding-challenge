package mandelbrot;

import mandelbrot.contexts.MandelbrotContext;
import mandelbrot.contexts.MandelbrotTerminalContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MandelbrotExplorer {

    private final int width, maxIterations;
    private final int dpi;
    private final String fileName;
    private final Pixel start;
    private final Pixel end;
    private final MandelbrotContext context;

    public MandelbrotExplorer(MandelbrotContext context, int dpi, String fileName) {
        this.context = context;
        this.maxIterations = context.maxIterations();
        this.width = context.width();
        this.dpi = dpi;
        this.fileName = fileName;
        this.start = new Pixel(-1.75,-1.12);
        this.end = new Pixel(0.47,1.12);
    }

    public void run() {

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var i = 0;
            for (int y = 0; y < this.width; y++) {
                for (int x = 0; x < this.width; x++) {
                    executor.submit(new Mandel(this.context, this.start, this.end, new Pixel(x, y, i++), this.width, this.width, maxIterations));
                }
            }
            context.printContext();
        }
    }

}
