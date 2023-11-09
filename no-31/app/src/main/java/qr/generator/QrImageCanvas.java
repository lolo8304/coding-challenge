package qr.generator;

import qr.Point2d;
import qr.Rect;
import qr.Region;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class QrImageCanvas extends QrCanvas {

    private BufferedImage board;
    private Graphics2D g2d;
    private int squareSize;

    public QrImageCanvas(Rect rect) {
        this(rect, 5);
    }


    public QrImageCanvas(Rect rect, int squareSize) {
        super(rect, true);
        this.squareSize = squareSize;
        this.initImage();
    }

    private void initImage() {
        var imageWidth = (this.rect.width() + 2 * this.quietZone) * this.squareSize;
        var imageHeight = (this.rect.height() + 2 * quietZone) * this.squareSize;
        this.board = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        this.g2d = this.board.createGraphics();
    }

    @Override
    public void drawReq(Rect rect, boolean fill, Color... colors) {

    }

    @Override
    public void drawPoint(Point2d point, Color color) {
        this.g2d.setColor(color);
        this.g2d.fillRect(point.x * this.squareSize, point.y * this.squareSize, this.squareSize, this.squareSize);
    }

    @Override
    public void drawRegion(Region region, Color... colors) {

    }

    @Override
    public void drawStraightLine(Point2d point, int length, int thickness, Direction direction, Color... colors) {

    }

    @Override
    public boolean isEmpty(Point2d at) {
        return false;
    }

    @Override
    public void draw() {
    }

    @Override
    public boolean flipBitAt(Point2d point, Color bit0Color, Color bit1Color, Color replBit0Color, Color replBit1Color) {
        return false;
    }

    @Override
    public void finalize() {
        this.g2d.dispose();
    }

    @Override
    public boolean isWhite(Point2d point2d) {
        return false;
    }

    @Override
    public Color getColor(Point2d point2d) {
        return null;
    }

    @Override
    public void saveFile(String outputFileName) {
        try {
            var outputImage = new File(outputFileName);
            ImageIO.write(this.board, "png", outputImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
