package mandelbrot;

public class MandelbrotContext {
    private final int width;
    private final char[] context;
    private int printedIndex;
    private String chars;
    private final float max;

    public MandelbrotContext(int width, int max) {
        this.width = width;
        this.max = (float)max;
        this.context = new char[width * width];
        var w2 = width * width;
        for (int i = 0; i < w2; i++) {
            this.context[i] = ' ';
        }
        this.printedIndex = 0;
        this.chars = ".'`^\",:;Il!i~+_-?][}{1)(|\\/tfjrxnuvczXYUJCLQ0OZmwqpdbkhao*#MW&8%B@$ ";
    }

    public void draw(char ch, int x, int y) {
        var i = y * this.width + x;
        this.draw(ch, i);
    }
    public void draw(char ch, int index) {
        this.context[index] = ch;
    }
    public void draw(int iterations, int x, int y) {
        var i = y * this.width + x;
        this.draw(iterations, i);
    }
    public void draw(int iterations, int index) {
        var charIndex = (int) ((iterations / this.max) * (chars.length() - 1));
        this.context[index] = this.chars.charAt(charIndex);
    }

    public void printContext() {
        var endIndex = this.context.length;
        var w = 0;
        var alternate = false;
        for (int i = 0; i < endIndex; i++) {
            System.out.print(this.context[i]);
            w++;
            if (w == this.width) {
                w = 0;
                System.out.println();
                alternate = !alternate;
                if (alternate) {
                    i += width;
                }
            }
        }
    }

}
