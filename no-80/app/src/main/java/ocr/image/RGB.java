package ocr.image;


/** Small RGB tuple. */
public record RGB(int r, int g, int b) {
    public RGB {
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255)
            throw new IllegalArgumentException("RGB values must be in 0..255");
    }

    public static RGB white() {
        return new RGB(255, 255, 255);
    }
    public static RGB black() {
        return new RGB(0, 0, 0);
    }
    public static RGB red() {
        return new RGB(255, 0, 0);
    }
    public static RGB green() {
        return new RGB(0, 255, 0);
    }
    public static RGB blue() {
        return new RGB(0, 0, 255);
    }
    public static RGB yellow() {
        return new RGB(255, 255, 0);
    }
    public static RGB cyan() {
        return new RGB(0, 255, 255);
    }
    public static RGB magenta() {
        return new RGB(255, 0, 255);
    }
    public static RGB gray(int value) {
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException("Gray value must be in 0..255");
        }
        return new RGB(value, value, value);
    }
    public static RGB fromHex(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        if (hex.length() != 6) {
            throw new IllegalArgumentException("Hex color must be 6 characters long");
        }
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return new RGB(r, g, b);
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