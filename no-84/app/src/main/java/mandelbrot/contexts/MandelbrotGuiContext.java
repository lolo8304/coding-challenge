package mandelbrot.contexts;

import javax.swing.*;
import java.awt.*;

public class MandelbrotGuiContext extends MandelbrotAbstractContext {
    final int[] context;

    public MandelbrotGuiContext(int width, int max) {
        super(width, max);
        this.context = new int[width * width];
        var w2 = width * width;
        for (int i = 0; i < w2; i++) {
            this.context[i] = 0;
        }
    }

    @Override
    public void draw(int iterations, int index) {
        if (iterations == this.maxIterations()) {
            this.context[index] = Interpolator.MAX_DENSITY;
        } else {
            this.context[index] = (int) ((iterations / (float) this.maxIterations()) * (Interpolator.MAX_DENSITY - 1));
        }
    }

    @Override
    public void printContext() {
        System.out.println("Starting application...");
        SwingUtilities.invokeLater(() -> {
            System.out.println("invoked later...");
            var frame = new JFrame("Mandelbrot explorer");
            var panel = new MandelbrotPanel(this);

            // Frame settings
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(this.width(), this.width()); // Adjust to fit the grid nicely
            frame.setResizable(true);
            frame.add(panel);
            frame.repaint();
            frame.revalidate();
            frame.setVisible(true);
            System.out.println("JFrame should be visible now.");
        });
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class MandelbrotPanel extends JPanel {

        private final MandelbrotGuiContext context;

        public MandelbrotPanel(MandelbrotGuiContext context) {
            this.context = context;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            var interpolator = new Interpolator(Color.ORANGE, Color.MAGENTA);

            // Draw a 40x40 pixel grid
            int pixelSize = 1; // Size of each pixel on the screen (adjust for visibility)
            var i = 0;
            for (int y = 0; y < this.context.width(); y++) {
                for (int x = 0; x < this.context.width(); x++) {
                    // Draw each pixel as a small rectangle
                    var density = this.context.context[i++];
                    if (density == Interpolator.MAX_DENSITY) {
                        g.setColor(Color.BLACK);
                    } else {
                        g.setColor(interpolator.fromDensity(density));
                    }
                    g.fillRect(x * pixelSize, y * pixelSize, pixelSize, pixelSize);
                }
            }
        }
    }

    class Interpolator {

        public static final int MAX_DENSITY = 256;
        private final Color from;
        private final Color to;
        private Color[] cache;
        private float maxRatio;

        public Interpolator(Color from, Color to) {
            this.from = from;
            this.to = to;
            this.cache = new Color[MAX_DENSITY];
            this.maxRatio = (float)(MAX_DENSITY -  1) * (MAX_DENSITY - 1);
        }

        /**
         * Interpolates between two colors based on a value from 0 to 255.
         * @param value - The value from 0 to 255.
         * @return - The interpolated color.
         */
        public Color fromDensity(int value) {
            value = Math.max(0, Math.min(MAX_DENSITY - 1, value));
            if (cache[value] != null) return cache[value];

            double ratio = value * value / maxRatio;
            int red = (int) (from.getRed() + (to.getRed() - from.getRed()) * ratio);
            int green = (int) (from.getGreen() + (to.getGreen() - from.getGreen()) * ratio);
            int blue = (int) (from.getBlue() + (to.getBlue() - from.getBlue()) * ratio);

            var c = new Color(red, green, blue);
            this.cache[value] = c;
            return c;
        }
    }
}