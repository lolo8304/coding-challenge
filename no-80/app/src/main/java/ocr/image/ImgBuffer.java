package ocr.image;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Image wrapper with convenience methods.
 */
public record ImgBuffer(BufferedImage bi) {

    public int width() {
        return bi.getWidth();
    }

    public int height() {
        return bi.getHeight();
    }

    /**
     * Show the image in a simple window.
     */
    public void show() {
        show("Image (" + width() + "×" + height() + ")");
    }

    public void show(String title) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame(title);
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f.setLayout(new BorderLayout());
            f.add(new JScrollPane(new JLabel(new ImageIcon(bi))), BorderLayout.CENTER);
            f.pack();
            f.setLocationByPlatform(true);
            f.setVisible(true);
        });
    }

    BufferedImage raw() {
        return bi;
    }
}