package ocr.image;


/** Small RGB tuple. */
public record RGB(int r, int g, int b) {
    public RGB {
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255)
            throw new IllegalArgumentException("RGB values must be in 0..255");
    }
    int toArgb() { return (0xFF << 24) | (r << 16) | (g << 8) | b; }
    static RGB fromArgb(int argb) {
        return new RGB((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new RGB(r, g, b);
    }
}