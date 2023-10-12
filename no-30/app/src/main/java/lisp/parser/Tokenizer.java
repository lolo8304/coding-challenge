package lisp.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Tokenizer {
    private static Set<Character> SPECIAL_SYMBOL_CHARS = new HashSet<>();
    private Reader reader;
    private Optional<Character> last;
    static {
        Character[] chars = { '+', ':', '-', '_', '/', '=', '<', '>', '!', '$', '%', '&', '|', '?', '~', '*', '.' };
        SPECIAL_SYMBOL_CHARS.addAll(Arrays.asList(chars));
    }

    public Tokenizer(Reader reader) {
        this.reader = reader;
        this.last = Optional.empty();
    }

    public Optional<TokenValue> nextToken() throws IOException {
        if (this.last.isPresent()) {
            var ch = this.last.get();
            this.last = Optional.empty();
            return this.nextToken(ch);
        } else {
            return this.nextToken((char) reader.read());
        }
    }

    private Optional<TokenValue> nextToken(char ch) throws IOException {
        if (ch == -1 || ch == 65535) {
            return Optional.empty();
        }
        switch (ch) {
            case '\"':
                return this.parseStringToken(ch);
            case ':':
                return this.parseSymbolToken(ch, Token.KEYWORD);
            case '(':
                // return Optional.of(new TokenValue(Token.LPARENT));
                return this.parseSExpression(ch);
            case ')':
                return Optional.of(new TokenValue(Token.RPAREN));

            case '+', '-', '_', '/', '=', '<', '>', '!', '$', '%', '&', '|', '?', '~', '*':
                return this.parseSymbolToken(ch, Token.SYMBOL);
            case '\'':
                return this.parseQuote(ch);
            case ',':
                return this.parseComma(ch);
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9':
                return this.parseNumberToken(ch);
            case ' ', '\t', '\r', '\n':
                return this.parseWhitespace(ch);
            default:
                if (Character.isDigit(ch)) {
                    return this.parseSymbolToken(ch, Token.NUMBER);
                }
                if (isLetterAndUnderline(ch)) {
                    return this.parseSymbolToken(ch, Token.SYMBOL);
                }
                if (Character.isWhitespace(ch)) {
                    return parseWhitespace(ch);
                }
                throw new IllegalStateException("Illegal character " + ch);
        }
    }

    private Optional<TokenValue> parseQuote(char ch) throws IOException {
        var elem = this.nextToken();
        if (elem.isPresent()) {
            return Optional.of(new TokenValue(Token.QUOTE, elem.get()));
        } else {
            return Optional.empty();
        }
    }

    private Optional<TokenValue> parseComma(char ch) throws IOException {
        var elem = this.nextToken();
        if (elem.isPresent()) {
            return Optional.of(new TokenValue(Token.COMMA, elem.get()));
        } else {
            return Optional.empty();
        }
    }

    private Optional<Double> parseDouble(String str) {
        try {
            var d = Double.parseDouble(str);
            return Optional.of(d);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<Integer> parseInteger(String str) {
        try {
            var d = Integer.parseInt(str);
            return Optional.of(d);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<TokenValue> parseNumberToken(char ch) throws IOException {
        var buffer = new StringBuilder();
        buffer.append(ch);
        var nextInt = this.reader.read();
        var next = (char) nextInt;
        while (nextInt != -1 && (Character.isDigit(next) || (next == '.')) && !Character.isWhitespace(next)) {
            buffer.append(next);
            nextInt = this.reader.read();
            next = (char) nextInt;
        }
        if (nextInt == -1) {
            this.last = Optional.empty();
        } else {
            this.last = Optional.of((char) next);
        }

        var numStr = buffer.toString();
        var i = this.parseInteger(numStr);
        if (i.isPresent()) {
            return Optional.of(new TokenValue(Token.NUMBER_INTEGER, numStr, i.get()));
        } else {
            var doubleValue = this.parseDouble(numStr);
            if (doubleValue.isPresent()) {
                return Optional.of(new TokenValue(Token.NUMBER_DOUBLE, numStr, doubleValue.get()));
            } else {
                return Optional.of(new TokenValue(Token.SYMBOL, numStr));
            }
        }
    }

    private boolean isLetterAndUnderline(char ch) {
        return Character.isLetter(ch) || ch == '_';
    }

    private boolean isLetterDigitalUnderline(char ch) {
        return isLetterAndUnderline(ch) || Character.isDigit(ch);
    }

    private Optional<TokenValue> parseWhitespace(char ch) throws IOException {
        ch = (char) reader.read();
        while ((Character.isWhitespace(ch) || ch == '\r' || ch == '\n') && ch != -1 && ch != 65535) {
            ch = (char) reader.read();
        }
        return ch == -1 || ch == 65535 ? Optional.empty() : this.nextToken(ch);
    }

    private Optional<TokenValue> parseSExpression(char ch) throws IOException {
        var elem = this.nextToken();
        var list = new ArrayList<TokenValue>();
        while (elem.isPresent() && (elem.get().getToken() != Token.RPAREN)) {
            list.add(elem.get());
            elem = this.nextToken();
        }
        return Optional.of(new TokenValue(Token.S_EXPRESSION, list));
    }

    private Optional<TokenValue> parseSymbolToken(char ch, Token returnedToken) throws IOException {
        var buffer = new StringBuilder();
        buffer.append(ch);
        var nextInt = this.reader.read();
        var next = (char) nextInt;
        while (nextInt != -1
                && (isLetterDigitalUnderline(next) || isSpecialSymbolChar(next) && !Character.isWhitespace(next))) {
            buffer.append(next);
            nextInt = this.reader.read();
            next = (char) nextInt;
        }
        if (nextInt == -1) {
            this.last = Optional.empty();
        } else {
            this.last = Optional.of((char) next);
        }
        var symbol = buffer.toString();
        if (symbol.equals("T")) {
            return Optional.of(new TokenValue(Token.T, symbol));
        } else if (symbol.equals("NIL")) {
            return Optional.of(new TokenValue(Token.NIL, symbol));
        } else {
            return Optional.of(new TokenValue(returnedToken, symbol));
        }
    }

    private boolean isSpecialSymbolChar(char next) {
        return SPECIAL_SYMBOL_CHARS.contains(next);
    }

    private Optional<TokenValue> parseStringToken(char ch) throws IOException {
        var buffer = new StringBuilder();
        var nextInt = this.reader.read();
        while (nextInt != -1 && nextInt != ch) {
            buffer.append((char) nextInt);
            if (nextInt == '\\') { // escaping
                buffer.append((char) this.reader.read());
            }
            nextInt = this.reader.read();
        }
        if (nextInt == -1) {
            return Optional.empty();
        }
        return Optional.of(new TokenValue(Token.STRING, buffer.toString()));
    }

}
