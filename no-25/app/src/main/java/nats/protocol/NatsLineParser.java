package nats.protocol;

import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

import nats.protocol.NatsHandler.Request;

public class NatsLineParser {
    private String line;
    private int pos = 0;
    private Request request;

    public NatsLineParser(String line) {
        this.line = line;
        this.request = null;
    }

    public NatsLineParser(String line, NatsHandler.Request request) {
        this.line = line;
        this.request = request;
    }

    public NatsHandler handler() {
        return this.request.handler();
    }

    public Optional<Token> nextToken() {
        var index = pos;
        while (this.pos < line.length() && isWhitespace(this.line.charAt(this.pos))) {
            pos++;
            index++;
        }
        while (this.pos < line.length() && !isSeperator(this.line.charAt(this.pos))) {
            pos++;
        }
        if (index < line.length()) {
            if (this.isJson(this.line.charAt(index))) {
                while (this.pos < line.length() && !isCRLF(this.line.charAt(this.pos))) {
                    pos++;
                }
                if (this.pos < line.length()) {
                    var token = Optional.of(new Token(this, index, pos));
                    return token;
                } else {
                    throw new IllegalArgumentException(
                            String.format("Json structure starting at %d has not valid CRLF", index));
                }
            } else {
                if (this.isCRLF(line.charAt(index))) {
                    while (this.pos < line.length() && isCRLF(this.line.charAt(this.pos))) {
                        pos++;
                    }
                }
                var token = Optional.of(new Token(this, index, pos));
                if (this.pos < line.length()) {
                    if (this.isWhitespace(this.line.charAt(this.pos))) {
                        this.pos++; // because of separator
                    }
                }
                return token;
            }
        } else {
            return Optional.empty();
        }
    }

    public NatsLineParser readNextLine() throws IOException {
        if (this.request == null) {
            throw new IllegalArgumentException("Read next line not possible because not handler connected");
        }
        this.line = this.request.readNextLine();
        this.pos = 0;
        return this;
    }

    public int clientId() {
        return this.request != null ? this.request.clientId() : 0;
    }

    private boolean isJson(char ch) {
        return ch == '{';
    }

    private boolean isCRLF(char ch) {
        return ch == '\r' || ch == '\n';
    }

    private boolean isSeperator(char ch) {
        return isWhitespace(ch) || isCRLF(ch);
    }

    private boolean isWhitespace(char ch) {
        return ch == ' ' || ch == '\t';
    }

    public String line() {
        return line;
    }

    public static class Token {
        private int pos;
        private int endExcl;
        private int len;
        private String line;
        private Type type;

        public Token(NatsLineParser lineParser, int pos, int end) {
            this.line = lineParser.line;
            this.pos = pos;
            this.endExcl = end;
            this.len = end - pos;
            this.type = this.typeAtPos();
        }

        private Optional<Character> charAt() {
            return this.charAt(this.pos);
        }

        private Optional<Character> charAt(int index) {
            if (index < this.endExcl) {
                return Optional.of(this.line.charAt(index));
            } else {
                return Optional.empty();
            }
        }

        private Type typeAtPos() {
            var ch = charAt();
            if (ch.isEmpty()) {
                return Type.EOL;
            }
            switch (ch.get()) {
                case '{':
                    return Type.JSON;
                case ' ':
                case '\t':
                    return Type.WHITESPACE;
                case '\n':
                    return Type.LF;
                case '\r': {
                    if (len > 1) { // not last character
                        var next = this.charAt(this.pos + 1);
                        return (next.isPresent() && next.get() == '\n') ? Type.CRLF : Type.CR;
                    } else {
                        return Type.CR;
                    }
                }
                default: {
                    if (this.isInteger()) {
                        return Type.INTEGER;
                    } else {
                        return Type.STRING;
                    }
                }
            }
        }

        public Type type() {
            return this.type;
        }

        public String toString() {
            return this.line.substring(this.pos, this.endExcl);
        }

        public int toInt() {
            return Integer.parseInt(this.toString());
        }

        private boolean isInteger() {
            try (var sc = new Scanner(this.toString())) {
                if (!sc.hasNextInt(10))
                    return false;
                // we know it starts with a valid int, now make sure
                // there's nothing left!
                sc.nextInt(10);
                return !sc.hasNext();
            }
        }
    }

    public enum Type {
        STRING,
        INTEGER,
        WHITESPACE,
        JSON,
        LF,
        EOL,
        CR,
        CRLF
    }
}