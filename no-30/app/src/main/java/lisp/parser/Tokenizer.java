package lisp.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Optional;

public class Tokenizer {
    private Reader reader;
    private Optional<Character> last;

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
            return this.nextToken((char)reader.read());
        }
    }

    private Optional<TokenValue> nextToken(char ch) throws IOException {
        if (ch == -1) {
            return Optional.empty();                
        }
        switch (ch) {
            case '\'':
            case '\"':
                return this.parseStringToken(ch);
            case ':':
                return this.parseSymbolToken(ch);
            case '(':
                //return Optional.of(new TokenValue(Token.OPEN_PARENTHESIS));
                return this.parseSExpression(ch);
            case ')':
                return Optional.of(new TokenValue(Token.CLOSE_PARENTHESIS));
        
            default:
                if (Character.isLetterOrDigit(ch)) {
                    return this.parseSymbolToken(ch);                    
                }
                if (Character.isWhitespace(ch)) {
                    return parseWhitespace(ch);
                }
                throw new IllegalStateException("Illegal character "+ch);
        }
    }

    private Optional<TokenValue> parseWhitespace(char ch) throws IOException {
        ch = (char)reader.read();
        while (Character.isWhitespace(ch)) {
            ch = (char)reader.read();
        }
        return this.nextToken(ch);
    }

    private Optional<TokenValue> parseSExpression(char ch) throws IOException {
        var elem = this.nextToken();
        var list = new ArrayList<TokenValue>();
        while (elem.isPresent() && (elem.get().getToken() != Token.CLOSE_PARENTHESIS)) {
            list.add(elem.get());
            elem = this.nextToken();
        }
        return Optional.of(new TokenValue(Token.S_EXPRESSION, list));
    }

    private Optional<TokenValue> parseSymbolToken(char ch) throws IOException {
        var buffer = new StringBuilder();
        buffer.append(ch);
        var next = this.reader.read();
        while (next != -1 && Character.isLetterOrDigit(next) && !Character.isWhitespace(ch)) {
            buffer.append((char)next);
            next = this.reader.read();
        }
        this.last = Optional.of((char)next);
        return Optional.of(new TokenValue(Token.SYMBOL_ATOM, buffer.toString()));
    }

    private Optional<TokenValue> parseStringToken(char ch) throws IOException {
        var buffer = new StringBuilder();
        var next = this.reader.read();
        while (next != -1 && next != ch) {
            buffer.append((char)next);
            if (next == '\\') { // escaping
                buffer.append((char)this.reader.read());
            }
            next = this.reader.read();
        }
        if (next == -1) {
            return Optional.empty();
        }
        return Optional.of(new TokenValue(Token.STRING_ATOM, buffer.toString()));
    }

    
}
