package lisp.parser;

import java.util.ArrayList;
import java.util.List;

public class TokenValue {
    private static final ArrayList<TokenValue> EMPTY_ARRAY_LIST = new ArrayList<>();

    private Token token;
    private String str;
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
}
