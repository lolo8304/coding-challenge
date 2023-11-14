package jq.parser;

import json.model.JValue;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class JsonQueryParser {

    private final String jqFilter;
    private final StringReader reader;

    private int charInBuffer = -1;

    public JsonQueryParser(String jqFilter) {
        this.jqFilter = jqFilter;
        this.reader = new StringReader(this.jqFilter);
    }

    public Optional<JsonQueryNode> parse() throws IOException {
        if (this.reader.ready()) {
            var token = this.nextToken();
            if (token.isPresent()) {
                JsonQueryNode root;
                if (token.get().token == Token.CURRENT) {
                    root = new JsonQueryNodeCurrent(token.get());
                } else if (token.get().token == Token.OPEN_ARRAY) {
                    root = new JsonQueryNodeArrayBuilder(token.get());
                } else {
                    throw new IllegalArgumentException("Must always start with . or [");
                }
                token = this.nextToken();
                while (token.isPresent()) {
                    Optional<JsonQueryNode> elem = Optional.empty();
                    if (token.get().token == Token.OPEN_ARRAY) {
                        elem = this.parseIndexed(root, token.get());
                    } else if (token.get().token == Token.OPEN_CURLY) {
                        elem = this.parseObjects(root, token.get());
                    } else if (token.get().token == Token.CURRENT) {
                        elem = Optional.of(new JsonQueryNodeCurrent(token.get(), root));
                    } else if (token.get().token == Token.PIPE) {
                        elem = Optional.of(new JsonQueryNodePipe(token.get(), root));
                    } else if (token.get().token == Token.NAME) {
                        elem = Optional.of(new JsonQueryNodeKeyd(token.get(), root));
                    }
                    if (elem.isPresent()) {
                        root = elem.get();
                    }
                    token = this.nextToken();
                }
                return Optional.of(root.root());
            } else {
                return Optional.of(new JsonQueryNodeCurrent(new TokenValue(Token.CURRENT, ".")));
            }
        } else {
            return Optional.empty();
        }
    }

    private Optional<JsonQueryNode> parseObjects(JsonQueryNode root, TokenValue tokenValue) throws IOException {
        List<TokenValue> list = new ArrayList<>();
        var next = this.nextToken();
        while (next.isPresent() && next.get().token != Token.CLOSE_CURLY) {
            if (next.get().token == Token.NAME) {
                list.add(next.get());
            }
            next = this.nextToken();
        }
        if (next.isPresent()) {
            var objectElement = new JsonQueryNodeObjects(null, root);
            list.forEach(x -> new JsonQueryNode(x, objectElement));
            return Optional.of(objectElement);
        } else {
            return Optional.empty();
        }
    }

    private Optional<JsonQueryNode> parseIndexed(JsonQueryNode root, TokenValue token) throws IOException {
        List<TokenValue> list = new ArrayList<>();
        var next = this.nextToken();
        while (next.isPresent() && next.get().token != Token.CLOSE_ARRAY) {
            list.add(next.get());
            next = this.nextToken();
        }
        if (next.isPresent()) {
            return Optional.of(new JsonQueryNodeIndexed(list.size() == 0 ? null : list.get(0), root));
        } else {
            return Optional.empty();
        }
    }

    private Optional<JsonQueryNode> parseArrayBuilder(JsonQueryNodeArrayBuilder root, TokenValue tokenValue) throws IOException {
        List<TokenValue> list = new ArrayList<>();
        list.add(tokenValue);
        var next = this.nextToken();
        while (next.isPresent() && next.get().token != Token.CLOSE_ARRAY) {
            list.add(next.get());
            next = this.nextToken();
        }
        if (next.isPresent()) {
            return Optional.of(new JsonQueryNodeIndexed(list.size() == 0 ? null : list.get(0), root));
        } else {
            return Optional.empty();
        }
    }


    private void pushBackToken(Optional<Character> ch) {
        ch.ifPresent(character -> this.charInBuffer = character);
    }

    private Optional<Character> read() throws IOException {
        if (this.charInBuffer != -1) {
            char lastChar = (char)this.charInBuffer;
            this.charInBuffer = -1;
            return Optional.of(lastChar);
        }
        int ch = this.reader.read();
        if (ch == -1) {
            return Optional.empty();
        } else {
            return Optional.of((char)ch);
        }
    }

    private Optional<TokenValue> nextToken() throws IOException {
        var ch = this.read();
        if (ch.isPresent()) {
            var token = ch.get();
            switch (token) {
                case '.' -> {
                    return Optional.of(new TokenValue(Token.CURRENT, "."));
                }
                case ',' -> {
                    return Optional.of(new TokenValue(Token.COMMA, ","));
                }
                case '[' -> {
                    return Optional.of(new TokenValue(Token.OPEN_ARRAY, "["));
                }
                case ']' -> {
                    return Optional.of(new TokenValue(Token.CLOSE_ARRAY, "]"));
                }
                case ' ' -> {
                    return this.parseWhiteSpace(token);
                }
                case '(' -> {
                    return Optional.of(new TokenValue(Token.OPEN_PARENTHISIS, "("));
                }
                case ')' -> {
                    return Optional.of(new TokenValue(Token.CLOSE_PARENTHISIS, ")"));
                }
                case '{' -> {
                    return Optional.of(new TokenValue(Token.OPEN_CURLY, "{"));
                }
                case '}' -> {
                    return Optional.of(new TokenValue(Token.CLOSE_CURLY, "}"));
                }
                case '|' -> {
                    return Optional.of(new TokenValue(Token.PIPE, "|"));
                }
                case '-' -> {
                    return this.parseDigit(token);
                }
                case '"' -> {
                    return this.parseString(token);
                }
                default -> {
                    if (Character.isAlphabetic(token)) {
                        return this.parseAlpahbetic(token);
                    }
                    if (Character.isDigit(token)) {
                        return this.parseDigit(token);
                    }
                    throw new IllegalArgumentException("Character '" + ch + "' is not valid for jq");
                }
            }
        } else {
            return Optional.empty();
        }
    }

    private Optional<TokenValue> parseDigit(Character token) throws IOException {
        var builder = new StringBuilder();
        builder.append(token);
        var ch = this.read();
        while (ch.isPresent() && (Character.isDigit(ch.get()) || ch.get().equals('.'))) {
            builder.append(ch.get());
            ch = this.read();
        }
        this.pushBackToken(ch);
        return Optional.of(new TokenValue(Token.NUMBER, builder.toString()));
    }

    private Optional<TokenValue> parseAlpahbetic(Character token) throws IOException {
        var builder = new StringBuilder();
        builder.append(token);
        var ch = this.read();
        while (ch.isPresent() && (Character.isLetterOrDigit(ch.get()) || ch.get().equals('_'))) {
            builder.append(ch.get());
            ch = this.read();
        }
        this.pushBackToken(ch);
        return Optional.of(new TokenValue(Token.NAME, builder.toString()));
    }

    private Optional<TokenValue> parseString(Character token) throws IOException {
        var builder = new StringBuilder();
        builder.append(token);
        var ch = this.read();
        while (ch.isPresent() && !ch.get().equals('\"')) {
            if (ch.get().equals('\\')) { // override any escape
                builder.append(ch.get());
                ch = this.read();
                if (ch.isEmpty()) {
                    throw new IllegalArgumentException("Escaped char \\ is not correctly finished ");
                }
            }
            builder.append(ch.get());
            ch = this.read();
        }
        if (ch.isPresent()) {
            builder.append(ch.get());
            return Optional.of(new TokenValue(Token.STRING, builder.toString()));
        } else {
            throw new IllegalArgumentException("String not finished with \"");
        }
    }

    private Optional<TokenValue> parseWhiteSpace(Character token) throws IOException {
        var ch = this.read();
        while (ch.isPresent() && Character.isWhitespace(ch.get())) {
            ch = this.read();
        }
        this.pushBackToken(ch);
        return Optional.of(new TokenValue(Token.WHITESPACE, String.valueOf(token)));
    }


    public enum Token {
        CURRENT,
        OPEN_ARRAY, CLOSE_ARRAY,
        OPEN_CURLY, CLOSE_CURLY,
        OPEN_PARENTHISIS, CLOSE_PARENTHISIS,
        COMMA, PIPE,
        WHITESPACE,
        STRING, NAME, NUMBER, ;
    }
    public static class TokenValue {
        public final Token token;
        public final String value;

        public TokenValue(Token token, String value) {
            this.token = token;
            this.value = value;
        }
        public TokenValue(Token token) {
            this.token = token;
            this.value = token.toString();
        }

        public String stringValue() {
            if (this.value.startsWith("\"") && this.value.endsWith("\"")) {
                return this.value.substring(1, this.value.length()-1);
            }
            return this.value;
        }
    }
}
