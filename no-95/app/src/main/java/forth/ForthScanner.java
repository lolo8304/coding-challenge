package forth;

public class ForthScanner {
    private final String line;
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

    public String nextTokenUntil(char delimiter) {
        skipWhitespace();
        if (position >= line.length()) {
            return null; // No more tokens
        }
        int start = position;
        while (position < line.length() && line.charAt(position) != delimiter) {
            position++;
        }
        String token = line.substring(start, position);
        if (position < line.length() && line.charAt(position) == delimiter) {
            position++; // Skip the delimiter
        }
        return token;
    }

    public String nextTokenAfterNewLine() {
        skipWhitespace();
        if (position >= line.length()) {
            return null; // No more tokens
        }
        while (position < line.length() && line.charAt(position) != '\n' && line.charAt(position) != '\r') {
            position++;
        }
        return this.nextToken();
    }

    private void skipWhitespace() {
        while (position < line.length() && Character.isWhitespace(line.charAt(position))) {
            position++;
        }
    }
}
