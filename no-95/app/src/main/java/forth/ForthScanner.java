package forth;

public class ForthScanner {
    private String line;
    private int position;

    public ForthScanner(String line) {
        this.line = line;
        this.position = 0;
    }

    public String nextToken() {
        skipWhitespace();
        if (position >= line.length()) {
            return null; // No more tokens
        }
        int start = position;
        while (position < line.length() && !Character.isWhitespace(line.charAt(position))) {
            position++;
        }
        return line.substring(start, position);
    }

    private void skipWhitespace() {
        while (position < line.length() && Character.isWhitespace(line.charAt(position))) {
            position++;
        }
    }
}
