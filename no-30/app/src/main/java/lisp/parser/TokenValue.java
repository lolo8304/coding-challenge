package lisp.parser;

import java.util.ArrayList;
import java.util.List;

public class TokenValue {
    private static final ArrayList<TokenValue> EMPTY_ARRAY_LIST = new ArrayList<>();

    private Token token;
    private String str;
    private Double d;
    private Integer i;
    private List<TokenValue> expression;

    public TokenValue(Token token) {
        this.token = token;
        this.str = null;
        this.expression = EMPTY_ARRAY_LIST;
    }
    public TokenValue(Token token, String str) {
        this.token = token;
        this.str = str;
        this.expression = EMPTY_ARRAY_LIST;
    }
    public TokenValue(Token token, List<TokenValue> expression) {
        this.token = token;
        this.expression = expression;
        this.str = null;
    }

    public TokenValue(Token numberDouble, String str, Double d) {
        this.token = token;
        this.str = str;
        this.d = d;
    }
    public TokenValue(Token numberInteger, String str, int i) {
        this.token = token;
        this.str = str;
        this.i = i;
    }
    public String getValue() {
        return this.str;
    }
    public int getInt() {
        return Integer.parseInt(this.str);
    }
    public Token getToken() {
        return this.token;
    }
    public List<TokenValue> getExpression() {
        return this.expression;
    }

    @Override
    public String toString() {
        return String.format("Token[%s, %s]", this.token, this.str);
    }
}
