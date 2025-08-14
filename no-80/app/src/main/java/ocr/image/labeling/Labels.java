package ocr.image.labeling;

import lombok.Getter;
import ocr.image.Image;
import ocr.image.RGB;

import java.util.ArrayDeque;
import java.util.Deque;

@Getter
public class Labels {
    public static final int CONTOUR = -1;
    public static final int UNLABELED = 0;
    public static final int BACKGROUND = -1;
    public static final int FOREGROUND = 1;

    public static final int BORDER = 2;
    public static final int NOISE = 3;
    private final int width;
    private final int height;
    private final int[][] labels;
    private final int[][] pixels;
    private int currentLabel;

    public static RGB[] labelColors = new RGB[] {
            RGB.RED, RGB.GREEN, RGB.BLUE, RGB.YELLOW, RGB.CYAN, RGB.MAGENTA
    };
    public static RGB toColor(int label) {
        if (label == UNLABELED) return RGB.WHITE; // Unlabeled pixels are white
        if (label == BACKGROUND) return RGB.BLACK; // Background pixels are black
        label = label - 2;
        return labelColors[label % labelColors.length];
    }

    public static int fromRGB(RGB rgb) {
        if (rgb.equals(RGB.WHITE)) return UNLABELED; // Unlabeled pixels are white
        if (rgb.equals(RGB.BLACK)) return BACKGROUND; // Background pixels are black
        for (int i = 0; i < labelColors.length; i++) {
            if (labelColors[i].equals(rgb)) {
                return i;
            }
        }
        return UNLABELED; // Default to UNLABELED if no match found
    }


    public Labels(Image image) {
        this.currentLabel = 2;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.pixels = new int[this.height][this.width];
        this.labels = new int[this.height][this.width];
        this.initialize(image.getPixels());
    }

    private void initialize(RGB[] pixels) {
        var i = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                RGB pixel = pixels[i++];
                // Assuming a simple threshold to distinguish foreground and background
                if (pixel.r() < 128 && pixel.g() < 128 && pixel.b() < 128) { // Dark pixel
                    this.pixels[y][x] = FOREGROUND;
                } else {
                    this.pixels[y][x] = BACKGROUND;
                }
                this.labels[y][x] = UNLABELED;
            }
        }
    }

    public Image getImage() {
        RGB[] labeledPixels = new RGB[width * height];
        var i = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int label = labels[y][x];
                if (label == UNLABELED) {
                    labeledPixels[i] = RGB.WHITE;
                } else if (label == CONTOUR) {
                    labeledPixels[i] = RGB.BLACK;
                } else {
                    labeledPixels[i] = toColor(label);
                }
                i++;
            }
        }
        return new Image(width, height, labeledPixels);
    }

    public boolean isNewExternalContour(int x, int y) {
        if (y == 0) return false;
        var isInside = labels[y][x] == UNLABELED && pixels[y-1][x] == BACKGROUND && pixels[y][x] == FOREGROUND;
        labels[y-1][x] = CONTOUR; // Mark as contour
        return isInside;
    }

    public boolean isNewInternalContour(int x, int y) {
        if (y == this.height - 1) return false;
        var  isOutside = labels[y][x] == UNLABELED && pixels[y+1][x] == FOREGROUND && pixels[y][x] == BACKGROUND;
        labels[y][x] = CONTOUR; // Mark as contour
        return isOutside;
    }

    public Labels detectLabels(boolean externalContours) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (labels[y][x] != UNLABELED) {
                    continue;
                }
                if (pixels[y][x] == BACKGROUND) {
                    labels[y][x] = BACKGROUND;
                } else if (externalContours && isNewExternalContour(x, y)) {
                    traceContourIterative(x, y);
                    currentLabel++;
                } else if (!externalContours && isNewInternalContour(x, y)) {
                    traceContourIterative(x, y);
                    currentLabel++;
                }
            }
        }
        return this;
    }

    private void traceContour(int x, int y) {
        labels[y][x] = currentLabel;
        // CTL algorithm - Simple contour tracing logic, can be improved with actual contour tracing algorithms
        int[] dx = {1, 1, 0, -1, -1, -1,  0,  1}; // Right, Down, Left, Up
        int[] dy = {0, 1, 1,  1,  0, -1, -1, -1}; // Right, Down, Left, Up
        for (int direction = 0; direction < 8; direction++) {
            int nx = x + dx[direction];
            int ny = y + dy[direction];
            if (nx >= 0 && nx < width && ny >= 0 && ny < height && labels[ny][nx] == UNLABELED && pixels[ny][nx] == FOREGROUND) {
                traceContour(nx, ny);
            }
        }
    }

    public void traceContourIterative(int x, int y) {
        int[] dx = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] dy = {0, 1, 1, 1, 0, -1, -1, -1};
        Deque<int[]> stack = new ArrayDeque<>();
        stack.push(new int[]{x, y});
        while (!stack.isEmpty()) {
            int[] pos = stack.pop();
            int cx = pos[0];
            int cy = pos[1];
            if (labels[cy][cx] != UNLABELED || pixels[cy][cx] != FOREGROUND) {
                continue;
            }
            labels[cy][cx] = currentLabel;
            for (int direction = 0; direction < 8; direction++) {
                int nx = cx + dx[direction];
                int ny = cy + dy[direction];
                if (nx >= 0 && nx < width && ny >= 0 && ny < height &&
                        labels[ny][nx] == UNLABELED && pixels[ny][nx] == FOREGROUND) {
                    stack.push(new int[]{nx, ny});
                }
            }
        }
    }

}
