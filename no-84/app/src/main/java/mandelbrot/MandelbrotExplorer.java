package mandelbrot;

import mandelbrot.contexts.MandelbrotContext;

import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class MandelbrotExplorer {

    private final Pixel initialStart;
    private final Pixel initialEnd;
    private Pixel start;
    private Pixel end;
    private MandelbrotContext context;
    private Stack<ZoomArea> stack;

    public MandelbrotExplorer() {
        this.initialStart = new Pixel(-1.75,-1.12);
        this.initialEnd = new Pixel(0.47,1.12);
        this.start = this.initialStart;
        this.end = this.initialEnd;
        this.stack = new Stack<>();
        this.stack.push(new ZoomArea(this.start, this.end));
    }

    public void setContext(MandelbrotContext context) {
        this.context = context;
    }

    public void zoom(Pixel pos, Pixel zoomWidth) {
        // pos is in mandelbrot coords
        var newStart = new Pixel(pos.x - zoomWidth.x / 2, pos.y - zoomWidth.y / 2 );
        var newEnd = new Pixel(pos.x + zoomWidth.x / 2, pos.y + zoomWidth.y / 2 );
        var newZoomArea = new ZoomArea(newStart, newEnd);
        this.stack.push(newZoomArea);
        this.start = newStart;
        this.end = newEnd;
        this.run();
    }

    public void zoomInAt(Pixel pos) {
        // pos is in x,y coord between width + width
        var mandelWidth = this.end.x - this.start.x;
        var mandelHeight = this.end.y - this.start.y;
        var mandelPosX = this.start.x + (mandelWidth * pos.x / this.context.width());
        var mandelPosY = this.start.y + (mandelHeight * pos.y / this.context.width());
        this.zoom(new Pixel(mandelPosX,  mandelPosY), new Pixel(mandelWidth / 4, mandelHeight / 4));
    }

    public void zoomBack() {
        if (this.stack.size() > 1) {
            var newZoomArea = this.stack.pop();
            this.start = newZoomArea.start;
            this.end = newZoomArea.end;
            this.run();
        } else if (this.stack.size() == 1) {
            this.start = this.initialStart;
            this.end = this.initialEnd;
            this.run();
        } else {
            this.start = this.initialStart;
            this.end = this.initialEnd;
            this.stack.push(new ZoomArea(this.start, this.end));
            this.run();
        }
    }
    public void zoomInitial() {
        while (this.stack.size() > 1) {
            this.stack.pop();
        }
        this.zoomBack();
    }

    public void run() {
        this.run3();
    }

    public void run1() {
        var startTimeInMs = System.currentTimeMillis();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var i = 0;
            for (int y = 0; y < this.context.width(); y++) {
                for (int x = 0; x < this.context.width(); x++) {
                    executor.submit(new Mandel(this.context, this.start, this.end, new Pixel(x, y, i++), this.context.width(), this.context.width(), this.context.maxIterations()));
                }
            }
        }
        var diffInMs = System.currentTimeMillis() - startTimeInMs;
        context.printContext(diffInMs);
    }


    public void run2() {
        var startTimeInMs = System.currentTimeMillis();
        try (ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
            var i = 0;
            for (int y = 0; y < this.context.width(); y++) {
                for (int x = 0; x < this.context.width(); x++) {
                    executor.submit(new Mandel(this.context, this.start, this.end, new Pixel(x, y, i++), this.context.width(), this.context.width(), this.context.maxIterations()));
                }
            }
        }
        var diffInMs = System.currentTimeMillis() - startTimeInMs;
        context.printContext(diffInMs);
    }

    public void run3() {
        var startTimeInMs = System.currentTimeMillis();
        try (ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
            int width = this.context.width();
            int maxIterations = this.context.maxIterations();
            for (int y = 0; y < width; y++) {
                final int row = y;
                executor.submit(() -> {
                    for (int x = 0; x < width; x++) {
                        int i = row * width + x;
                        new Mandel(this.context, this.start, this.end, new Pixel(x, row, i), width, width, maxIterations).run();
                    }
                });
            }
        }
        var diffInMs = System.currentTimeMillis() - startTimeInMs;
        context.printContext(diffInMs);
    }

    public void run4() {
        var startTimeInMs = System.currentTimeMillis();
        int width = this.context.width();
        int maxIterations = this.context.maxIterations();
        IntStream.range(0, width).parallel().forEach(row -> {
            for (int x = 0; x < width; x++) {
                int i = row * width + x;
                new Mandel(this.context, this.start, this.end, new Pixel(x, row, i), width, width, maxIterations).run();
            }
        });
        var diffInMs = System.currentTimeMillis() - startTimeInMs;
        context.printContext(diffInMs);
    }


    static class ZoomArea {
        private final Pixel start;
        private final Pixel end;

        public ZoomArea(Pixel start, Pixel end) {
            this.start = start;
            this.end = end;
        }
    }

}
