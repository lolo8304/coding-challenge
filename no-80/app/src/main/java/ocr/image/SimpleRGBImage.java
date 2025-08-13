package ocr.image;

import ocr.image.processing.MakeGrayscale;
import ocr.image.processing.Pipeline;
import ocr.image.processing.ReplaceBackgroundUntilPercentage;
import ocr.image.processing.Scale;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/** Simple RGB image functions using only standard Java (AWT/Swing). */
public final class SimpleRGBImage {


    /** ImgBuffer object = load_rgb_image(filename) */
    public static ImgBuffer loadRgbImage(String path) throws Exception {
        BufferedImage src = ImageIO.read(new File(path));
        if (src == null) throw new IllegalArgumentException("File is not a supported image: " + path);
        // Convert to RGB format without alpha, so (r,g,b) is clean
        BufferedImage rgb = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgb.createGraphics();
        try { g.drawImage(src, 0, 0, null); } finally { g.dispose(); }
        return new ImgBuffer(rgb);
    }

    /** Array of tuples (r,g,b) = load_rgb_pixels(imgBuffer)
     *  Order: row-major (y*w + x)
     */
    public static RGB[] loadRgbPixels(ImgBuffer imgBuffer) {
        int w = imgBuffer.width(), h = imgBuffer.height();
        RGB[] out = new RGB[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = imgBuffer.raw().getRGB(x, y);
                out[y * w + x] = RGB.fromArgb(argb);
            }
        }
        return out;
    }

    /** New_img = new_rgb_image(w, h, pixels) */
    public static ImgBuffer newRgbImage(int w, int h, RGB[] pixels) {
        if (pixels.length != w * h)
            throw new IllegalArgumentException("pixels.length must be w*h");
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int i = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                bi.setRGB(x, y, pixels[i++].toArgb());
            }
        }
        return new ImgBuffer(bi);
    }

    public static RGB[] clone(RGB[] pixels) {
        RGB[] cloned = new RGB[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            try {
                cloned[i] = (RGB) pixels[i].clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException("Clone not supported for RGB", e);
            }
        }
        return cloned;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java SimpleRGBImage <image-file>");
            return;
        }
        ImgBuffer imgBuffer = loadRgbImage(args[0]);
        System.out.println("Loaded: " + imgBuffer.width() + "x" + imgBuffer.height());
        var pipeline = new Pipeline()
                .addStep(new ReplaceBackgroundUntilPercentage(75, new RGB(255, 0, 0)))
                .addStep(new MakeGrayscale())
                .addStep(new Scale(50));
        var image = pipeline.process(new Image(imgBuffer));
        var reconstructed = newRgbImage(image.getWidth(), image.getHeight(), image.getPixels());

        reconstructed.show("Reconstructed from (r,g,b) tuples");
    }
}
