package json;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    private static final String validNumberRegexp = "^-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?$";

    private Reader reader;
    private LinkedList<Character> nextCharQueue;
    private Pattern validNumberPattern;

    public Lexer(Reader reader) {
        this.reader = reader;
        this.nextCharQueue = new LinkedList<>();
        this.validNumberPattern = Pattern.compile(validNumberRegexp);
    }
    
    public enum Token {
        OPEN_OBJECT,
        CLOSE_OBJECT,
        TRUE,
        FALSE,
        STRING,
        WHITESPACE,
        COMMA,
        COLON,
        OPEN_ARRAY,
        CLOSE_ARRAY,
        CHAR,
        HEX,
        DIGIT,
        ZERO,
        ONENINE,
        MINUS,
        NUMBER,
        INTEGER,
        FRACTION,
        EXPONENT,
        SIGN,
        NULL,
        EOF
    }

    public class TokenValue {
        public final Lexer.Token token;
        public final Object value;
        public final String string;

        public TokenValue(Lexer.Token token) {
            this(token, null);
        }
        public TokenValue(Lexer.Token token, Object value) {
            this.token = token;
            this.value = value;
            this.string = value == null ? "" : value.toString();
        }
    }

    public TokenValue next() throws IOException {
        return this.parseNextToken();
    }

    private Character readNext() throws IOException {
        if (this.nextCharQueue.isEmpty()) {
            var ch = this.reader.read();
            if (ch < 0) return null;
            return (char)ch;
        } else {
            return this.nextCharQueue.poll();
        }
    }
    private Character peekNext() throws IOException {
        if (this.nextCharQueue.isEmpty()) {
            var ch = this.reader.read();
            if (ch < 0) return null;
            this.nextCharQueue.add((char)ch);
        }
        return this.nextCharQueue.element();
    }
    private Character peekNext(int pos) throws IOException {
        if (pos == 0) {
            return this.peekNext();
        }
        while (pos > 0) {
            if (this.nextCharQueue.size() <= pos) {
                this.nextCharQueue.get(pos);
            }
        }
        return null;
    }

    private TokenValue parseNextToken() throws IOException {
        var ch = this.peekNext();
        if (ch == null) {
            return new TokenValue(Lexer.Token.EOF);
        }
        switch (ch) {
            case '{':
                this.readNext();
                return new TokenValue(Lexer.Token.OPEN_OBJECT);
            case '}':
                this.readNext();
                return new TokenValue(Lexer.Token.CLOSE_OBJECT);
            case '[':
                this.readNext();
                return new TokenValue(Lexer.Token.OPEN_ARRAY);
            case ']':
                this.readNext();
                return new TokenValue(Lexer.Token.CLOSE_ARRAY);
            case ':':
                this.readNext();
                return new TokenValue(Lexer.Token.COLON);
            case ',':
                this.readNext();
                return new TokenValue(Lexer.Token.COMMA);
            case '"':
                this.readNext();
                var str = this.readString(ch);
                return new TokenValue(Lexer.Token.STRING, str);
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '-':
                this.readNext();
                var number = this.parseNumber(ch);
                return new TokenValue(Lexer.Token.NUMBER, number);

            case 't':
                this.readNext();
                this.parseConstant(ch, "true");
                return new TokenValue(Lexer.Token.TRUE);

            case 'f':
                this.readNext();
                this.parseConstant(ch, "false");
                return new TokenValue(Lexer.Token.FALSE);

            case 'n':
                this.readNext();
                this.parseConstant(ch, "null");
                return new TokenValue(Lexer.Token.NULL);

            case ' ':
            case '\n':
            case '\r':
            case '\t':
                this.readNext();
                this.readWhitespace(ch);
                return this.parseNextToken();

            default:
                throw new IOException("invalid character parsing '"+ch+"'");
        }
    }

    private boolean isControlCharacter(Character ch){
        return ch != null && "\"\\/bfnrt".contains(ch.toString());
    }

    private void parseConstant(Character ch, String constant) throws IOException {
        String foundConstant = this.readAnyChars(ch, constant);
        if (!constant.equals(foundConstant)) {
            throw new IOException("found invalid token '"+foundConstant+"' instead of '"+constant+"'");
        }
    }

    private Number parseNumber(Character ch) throws IOException {
        String nextNumberString = this.readAnyChars(ch, "0123456789.+-eE");
        try {
            Matcher matcher = validNumberPattern.matcher(nextNumberString);
            if (matcher.matches()) {
                var number = NumberFormat.getInstance().parse(nextNumberString);
                if (number.getClass().equals(Double.class) && Double.isInfinite(number.doubleValue())) {
                    return new BigDecimal(nextNumberString);
                }
                return number;
            } else {
                throw new IOException("invalid number format '"+nextNumberString+"'");
            }
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
    }


    private String readString(Character quotes) throws IOException {
        var str = new StringBuilder();
        // dont add quotes
        var ch = this.peekNext();
        while (ch != null && !ch.equals(quotes)) {
            if (ch == '\\') { // is escaped
                str.append(ch);
                readNext();
                ch = peekNext();
                if (this.isControlCharacter(ch)) {
                    str.append(ch);
                    readNext();
                    ch = this.peekNext();
                }
            } else {
                str.append(ch);
                readNext();
                ch = this.peekNext();
            }
        }
        if (ch == null) {
            throw new IOException("Illegal string parsing - so far='"+str.toString()+"'");
        }
        // dont add quotes
        readNext();
        return str.toString();
    }

    private String readWhitespace(Character whitespace) throws IOException {
        return this.readAnyChars(whitespace, " \n\r\t");
    }

    private String readAnyChars(Character any, String nextChars) throws IOException {
        var str = new StringBuilder();
        str.append(any);
        var ch = this.peekNext();
        while (ch != null && nextChars.contains(ch.toString())) {
            str.append(ch);
            readNext();
            ch = this.peekNext();
        }
        if (ch == null) {
            return str.toString();
        }
        return str.toString();
    }

}
