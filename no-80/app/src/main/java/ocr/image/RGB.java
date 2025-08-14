package ocr.image;


/** Small RGB tuple. */
public record RGB(int r, int g, int b) {
    public static final RGB BLACK = new RGB(0, 0, 0);
    public static final RGB WHITE = new RGB(255, 255, 255);
    public static final RGB RED = new RGB(255, 0, 0);
    public static final RGB GREEN = new RGB(0, 255, 0);
    public static final RGB BLUE = new RGB(0, 0, 255);
    public static final RGB YELLOW = new RGB(255, 255, 0);
    public static final RGB CYAN = new RGB(0, 255, 255);
    public static final RGB MAGENTA = new RGB(255, 0, 255);
    public static final RGB PINK = new RGB(255, 192, 203);

    public RGB {
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255)
            throw new IllegalArgumentException("RGB values must be in 0..255");
    }

    public static RGB white() {
        return WHITE;
    }
    public static RGB black() {
        return BLACK;
    }
    public static RGB red() {
        return RED;
    }
    public static RGB green() {
        return GREEN;
    }
    public static RGB blue() {
        return BLUE;
    }
    public static RGB yellow() {
        return YELLOW;
    }
    public static RGB cyan() {
        return CYAN;
    }
    public static RGB magenta() {
        return MAGENTA;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RGB)) return false;
        RGB other = (RGB) obj;
        return this.r == other.r && this.g == other.g && this.b == other.b;
    }

    @Override
    public int hashCode() {
        return (r << 16) | (g << 8) | b;
    }
}