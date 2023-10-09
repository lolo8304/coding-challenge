package lisp.parser;

import java.util.ArrayList;
import java.util.List;

public class TokenValue {
    private static final ArrayList<TokenValue> EMPTY_ARRAY_LIST = new ArrayList<>();

    private final Token token;
    private final String str;
    private final Double d;
    private final Integer i;
    private List<TokenValue> expression;
    private TokenValue unary;

    public TokenValue(Token token) {
        this.token = token;
        this.str = null;
        this.expression = EMPTY_ARRAY_LIST;
        this.d = null;
        this.i = null;
    }

    public TokenValue(Token token, String str) {
        this.token = token;
        this.str = str;
        this.expression = EMPTY_ARRAY_LIST;
        this.d = null;
        this.i = null;
    }

    public TokenValue(Token token, List<TokenValue> expression) {
        this.token = token;
        this.expression = expression;
        this.str = null;
        this.d = null;
        this.i = null;
    }

    public TokenValue(Token token, TokenValue unary) {
        this.token = token;
        this.expression = EMPTY_ARRAY_LIST;
        this.str = null;
        this.d = null;
        this.i = null;
        this.unary = unary;
    }

    public TokenValue(Token numberDouble, String str, Double d) {
        this.token = numberDouble;
        this.str = str;
        this.d = d;
        this.i = null;
    }

    public TokenValue(Token numberInteger, String str, int i) {
        this.token = numberInteger;
        this.str = str;
        this.i = i;
        this.d = null;
    }

    public String getValue() {
        return this.appendTo(new StringBuilder()).toString();
    }

    public int getInt() {
        return this.i;
    }

    public double getDuble() {
        return this.d;
    }

    public Token getToken() {
        return this.token;
    }

    public List<TokenValue> getExpression() {
        return this.expression;
    }

    public StringBuilder appendTo(StringBuilder builder) {
        switch (this.token) {
            case NUMBER_DOUBLE:
                builder.append(this.d.toString());
                break;
            case NUMBER_INTEGER:
                builder.append(this.i.toString());
                break;
            case S_EXPRESSION:
                builder.append("(");
                var first = true;
                for (TokenValue value : this.expression) {
                    if (first) {
                        first = false;
                    } else {
                        builder.append(" ");
                    }
                    value.appendTo(builder);
                }
                builder.append(")");
                break;
            case BUILTIN, KEYWORD, PACKAGE, SYMBOL:
                builder.append(str);
                break;
            case STRING:
                builder.append('"').append(str).append('"');
                break;
            case COMMA:
                builder.append(',');
                break;
            case DOT:
                builder.append('.');
                break;
            case QUOTE:
                builder.append('\'').append(this.unary.appendTo(builder));
                break;
            default:
                throw new IllegalArgumentException("Token " + this.token + " is invalid");
        }
        return builder;
    }

    @Override
    public String toString() {
        return String.format("Token[%s, %s]", this.token, this.getValue());
    }
}
