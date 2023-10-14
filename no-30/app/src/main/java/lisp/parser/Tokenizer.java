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
            return this.nextToken(this.readChar());
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
                this.readChar();
                return Optional.of(new TokenValue(Token.RPAREN));
            case ';':
                return this.parseComment(ch);
            case '#':
                return this.parseSharpExpression(ch);
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
                if (isLetterAndUnderline(ch)) {
                    return this.parseSymbolToken(ch, Token.SYMBOL);
                }
                if (Character.isWhitespace(ch)) {
                    return parseWhitespace(ch);
                }
                throw new IllegalStateException("Illegal character " + ch);
        }
    }

    private Optional<TokenValue> parseSharpExpression(char ch) throws IOException {
        ch = this.readChar(); 
        switch (ch) {
            case '(', '1', '2','3', '4', '5', '6', '7', '8', '9':
                if (ch== '(') {
                    var expr = this.parseSExpression(ch);
                    if (expr.isPresent()) {
                        expr.get().setDimension(1);
                    }
                    return expr;
                }
                var chA = this.readChar();
                if (chA == 'A') {
                    var dimension = Character.getNumericValue(ch);
                    throw new IllegalArgumentException("not yet implemented - multi-dim");
                } else {
                    throw new IllegalArgumentException("Multidimenional arrays needs to have for #<dim>A(....)");
                }

            case '+', '-':
                // Sharpsign+: The #+ and #- reader conditionals are used to conditionally read code
                // #+sbcl (sb-ext:save-lisp-and-die "my-program")
            case '=':
                    // Sharpsign Equal #=: In Common Lisp, #= is used for "read-time evaluation."
                    // (let ((x 5))
                    //     (read (make-string-input-stream "#.(+ 3 x)")))
            case ':':
                // Sharpsign Colon #: uninterned symbols
                // (#:my-symbol)
                throw new IllegalArgumentException("#: not implemented yet");
            case '\'':
                // Sharpsign Single-quote #' is used as a shorthand for the function
                // (defun my-function (x) (* x x))
                // (funcall #'my-function 5)

                ch = this.readChar();
                var symbol = this.parseSymbolToken(ch, Token.SYMBOL);
                if (symbol.isPresent()) {
                    return Optional.of(new TokenValue(Token.DYNAMIC_FUNCTION, symbol.get().getValue()));
                } else {
                    throw new IllegalArgumentException("#': must be followed by a symbol: e.g. #'+ ");
                }
            default:
                throw new IllegalArgumentException("#: not implemented yet");
        }

    }

    private char readChar() throws IOException {
        var ch = (char) reader.read();
        if (ch == -1 || ch == 65535) {
            this.last = Optional.empty();
        }
        this.last = Optional.of(ch);
        return ch;
    }

    private Optional<TokenValue> parseComment(char ch) throws IOException {
        ch = this.readChar();
        while (ch != '\n' && ch != -1 && ch != 65535) {
            ch = this.readChar();
        }
        return ch == -1 || ch == 65535 ? Optional.empty() : this.nextToken();
    }

    private Optional<TokenValue> parseQuote(char ch) throws IOException {
        ch = this.readChar();
        var elem = this.nextToken();
        if (elem.isPresent()) {
            return Optional.of(new TokenValue(Token.QUOTE, elem.get()));
        } else {
            return Optional.empty();
        }
    }

    private Optional<TokenValue> parseComma(char ch) throws IOException {
        ch = this.readChar();
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
        var next = this.readChar();
        while (this.last.isPresent() && (Character.isDigit(next) || (next == '.')) && !Character.isWhitespace(next)) {
            buffer.append(next);
            next = this.readChar();
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
        ch = this.readChar();
        while ((Character.isWhitespace(ch) || ch == '\r' || ch == '\n') && ch != -1 && ch != 65535) {
            ch = this.readChar();
        }
        return ch == -1 || ch == 65535 ? Optional.empty() : this.nextToken();
    }

    private Optional<TokenValue> parseSExpression(char ch) throws IOException {
        ch = this.readChar();
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
        var next = this.readChar();
        while (this.last.isPresent()
                && (isLetterDigitalUnderline(next) || isSpecialSymbolChar(next) && !Character.isWhitespace(next))) {
            buffer.append(next);
            next = this.readChar();
        }
        var symbol = buffer.toString();
        if (ch == '&') {
            if (symbol.length() == 1) {
                throw new IllegalArgumentException("& cannot be used alone. must be used with a symbol");
            }
            return Optional.of(new TokenValue(Token.FUNCTION_ARGUMENT_NAME, symbol.substring(1)));
        } else {
            if (symbol.equals("T") || symbol.equals("true")) {
                return Optional.of(new TokenValue(Token.T, symbol));
            } else if (symbol.equals("NIL") || symbol.equals("false")) {
                return Optional.of(new TokenValue(Token.NIL, symbol));
            } else {
                return Optional.of(new TokenValue(returnedToken, symbol));
            }
        }
    }

    private boolean isSpecialSymbolChar(char next) {
        return SPECIAL_SYMBOL_CHARS.contains(next);
    }

    private Optional<TokenValue> parseStringToken(char ch) throws IOException {
        var buffer = new StringBuilder();
        var next = this.readChar();
        while (this.last.isPresent() && next != ch) {
            buffer.append(next);
            if (next == '\\') { // escaping
                buffer.append(this.readChar());
            }
            next = this.readChar();
        }
        if (this.last.isPresent()) {
            this.readChar();
            return Optional.of(new TokenValue(Token.STRING, buffer.toString()));
        } else {
            return Optional.empty();
        }
    }

}
