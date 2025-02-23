package mandelbrot.contexts;

public class MandelbrotTerminalContext extends MandelbrotAbstractContext {
    private final char[] context;
    private int printedIndex;
    private String chars;

    public MandelbrotTerminalContext(int width, int max) {
        super(width, max);
        this.context = new char[width * width];
        var w2 = width * width;
        for (int i = 0; i < w2; i++) {
            this.context[i] = ' ';
        }
        this.printedIndex = 0;
        this.chars = ".'`^\",:;Il!i~+_-?][}{1)(|\\/tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$ ";
    }

    @Override
    public void draw(int iterations, int index) {
        var charIndex = (int) ((iterations / (float)this.maxIterations()) * (chars.length() - 1));
        this.context[index] = this.chars.charAt(charIndex);
    }

    @Override
    public void printContext(long timeInMs) {
        var endIndex = this.context.length;
        var w = 0;
        var alternate = false;
        for (int i = 0; i < endIndex; i++) {
            System.out.print(this.context[i]);
            w++;
            if (w == this.width()) {
                w = 0;
                System.out.println();
                alternate = !alternate;
                if (alternate) {
                    i += width();
                }
            }
        }
        int kPixelsPerS = (int)(endIndex * 1000.0 / timeInMs) / 1000;
        int mPixelsPerS = kPixelsPerS / 1000;
        var pixelsSpeed = mPixelsPerS >= 10 ? mPixelsPerS+"M" : kPixelsPerS+"k";
        System.out.println(pixelsSpeed+" pixels/s calculated");
    }

}
