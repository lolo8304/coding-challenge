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
        return this.str;
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

    @Override
    public String toString() {
        return String.format("Token[%s, %s]", this.token, this.token == Token.NUMBER_DOUBLE ? this.d.toString()
                : (this.token == Token.NUMBER_INTEGER ? this.i.toString() : this.str));
    }
}
