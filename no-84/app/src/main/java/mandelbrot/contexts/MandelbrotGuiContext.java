package mandelbrot.contexts;

import mandelbrot.MandelbrotExplorer;
import mandelbrot.Pixel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MandelbrotGuiContext extends MandelbrotAbstractContext {
    final int[] context;
    private JFrame frame;

    public MandelbrotGuiContext(MandelbrotExplorer explorer, int width, int max) {
        super(explorer, width, max);
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
    public void printContext(long timeInMs) {
        int kPixelsPerS = (int)(this.context.length * 1000.0 / timeInMs) / 1000;
        int mPixelsPerS = kPixelsPerS / 1000;
        var pixelsSpeed = mPixelsPerS >= 10 ? mPixelsPerS+"M" : kPixelsPerS+"k";
        if (this.frame != null) {
            this.frame.repaint();
            this.frame.revalidate();
            return;
        }
        final boolean[] opened = new boolean[1];
        opened[0] = false;
        SwingUtilities.invokeLater(() -> {
            var frame = new JFrame("Mandelbrot explorer: "+pixelsSpeed+" pixels/s");
            var panel = new MandelbrotPanel(this);

            // Frame settings
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(this.width(), this.width()); // Adjust to fit the grid nicely
            frame.setResizable(true);
            frame.add(panel);
            frame.repaint();
            frame.revalidate();
            frame.setVisible(true);
            opened[0] = false;
            this.frame = frame;
        });
        // wait to not close automatically
        while (!opened[0]) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    class MandelbrotPanel extends JPanel {

        private final MandelbrotGuiContext context;
        private Pixel mouseClicked;
        private Pixel mouseMoved;

        public MandelbrotPanel(MandelbrotGuiContext context) {
            this.context = context;
            // Add MouseListener to capture click events
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Get the x and y coordinates of the mouse click
                    int x = e.getX();
                    int y = e.getY();
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        explorer().zoomBack();
                    } else if (e.getButton() == MouseEvent.BUTTON2) {
                        explorer().zoomInitial();
                    } else if (e.getButton() == MouseEvent.BUTTON1) {
                        System.out.println("Mouse clicked at: (" + x + ", " + y + ")");
                        mouseClicked = new Pixel(x, y);
                        explorer().zoomInAt(mouseClicked);
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            var interpolator = new Interpolator("#9E0141", "#F1FAA9");

            // Draw a 40x40 pixel grid
            int pixelSize = 1; // Size of each pixel on the screen (adjust for visibility)
            var i = 0;
            for (int y = 0; y < this.context.width(); y++) {
                for (int x = 0; x < this.context.width(); x++) {
                    if (this.mouseClicked != null && x == this.mouseClicked.x && y == this.mouseClicked.y) {
                        g.setColor(Color.white);
                    } else {
                        // Draw each pixel as a small rectangle
                        var density = this.context.context[i++];
                        if (density == Interpolator.MAX_DENSITY) {
                            g.setColor(Color.BLACK);
                        } else {
                            g.setColor(interpolator.fromDensity(density));
                        }
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

        public static Color hexToColor(String hex) {
            // Remove the '#' if present
            if (hex.startsWith("#")) {
                hex = hex.substring(1);
            }

            // Parse the hex string to get RGB values
            int red = Integer.parseInt(hex.substring(0, 2), 16);
            int green = Integer.parseInt(hex.substring(2, 4), 16);
            int blue = Integer.parseInt(hex.substring(4, 6), 16);

            // Create and return the Color object
            return new Color(red, green, blue);
        }


        public Interpolator(String from, String to) {
            this(hexToColor(from), hexToColor(to));
        }
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