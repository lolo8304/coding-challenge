


import forth.ForthScanner;
import org.junit.jupiter.api.Test;

class ForthScannerTest {

    @Test void nextToken_simpleCode() {
        // Arrange
        var code = "10 10 + 10 + 10 +";
        var scanner = new ForthScanner(code);
        // Action
        var token = scanner.nextToken();
        // Assert
        assert token != null;
        assert token.equals("10");
        // Action
        token = scanner.nextToken();
        // Assert
        assert token != null;
        assert token.equals("10");
        // Action
        token = scanner.nextToken();
        // Assert
        assert token != null;
        assert token.equals("+");

    }

    @Test void nextToken_withWhitespace() {
        // Arrange
        var code = "  10   10 +    ";
        var scanner = new ForthScanner(code);
        // Action
        var token = scanner.nextToken();
        // Assert
        assert token != null;
        assert token.equals("10");

    }

    @Test void nextToken_emptyInput() {
        // Arrange
        var code = "";
        var scanner = new ForthScanner(code);
        // Action
        var token = scanner.nextToken();
        // Assert
        assert token == null; // No tokens should be returned for empty input
    }

    @Test void nextToken_whitespaceOnlyInput() {
        // Arrange
        var code = "   ";
        var scanner = new ForthScanner(code);
        // Action
        var token = scanner.nextToken();
        // Assert
        assert token == null; // No tokens should be returned for whitespace only input
    }

    @Test void nextToken_tabsAndNewlines() {
        // Arrange
        var code = "\t10\n10\t+\n";
        var scanner = new ForthScanner(code);
        // Action
        var token = scanner.nextToken();
        // Assert
        assert token != null;
        assert token.equals("10");
        // Action
        token = scanner.nextToken();
        // Assert
        assert token != null;
        assert token.equals("10");
        // Action
        token = scanner.nextToken();
        // Assert
        assert token != null;
        assert token.equals("+");
    }

    @Test void nextTokenUntil_delimiter() {
        // Arrange
        var code = "10 s\" hello\" 20";
        var scanner = new ForthScanner(code);
        var first = scanner.nextToken();
        var start = scanner.nextToken();
        // Action
        var token = scanner.nextTokenUntil('\"');
        var next = scanner.nextToken();
        // Assert
        assert first != null;
        assert first.equals("10");
        assert start != null;
        assert start.equals("s\"");

        assert token != null;
        assert token.equals("hello");

        assert next != null;
        assert next.equals("20");
    }
}
