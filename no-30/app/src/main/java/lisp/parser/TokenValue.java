package lisp.parser;

import java.util.ArrayList;
import java.util.List;

public class TokenValue implements ILispFunction {
    private static final ArrayList<TokenValue> EMPTY_ARRAY_LIST = new ArrayList<>();

    private final Token token;
    private final String str;
    private final Double d;
    private final Integer i;
    private List<TokenValue> expression;
    private TokenValue unary;
    private int dimension = 0;

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

    public TokenValue(Token numberDouble, Double d) {
        this.token = numberDouble;
        this.str = String.format("%f", d);
        this.d = d;
        this.i = null;
    }
    public TokenValue(Token numberInteger, Integer i) {
        this.token = numberInteger;
        this.str = String.format("%d", i);
        this.d = null;
        this.i = i;
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

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public String getValue() {
        return this.appendTo(new StringBuilder()).toString();
    }

    public Integer getInteger() {
        return this.i;
    }

    public Token getToken() {
        return this.token;
    }

    public List<? extends ILispFunction> getExpression() {
        return this.expression;
    }

    public StringBuilder appendTo(StringBuilder builder) {
        return this.appendTo(builder, false);
    }

    public StringBuilder appendTo(StringBuilder builder, boolean printToConsole) {
        switch (this.token) {
            case NUMBER_DOUBLE:
                builder.append(this.d.toString());
                break;
            case NUMBER_INTEGER:
                builder.append(this.i.toString());
                break;
            case S_EXPRESSION:
                if (this.dimension > 0) {
                    builder.append("#");
                    if (dimension > 1) {
                        builder.append(dimension).append('A');
                    }
                }
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
            case T, NIL:
                builder.append(this.token);
                break;
            case STRING:
                if (printToConsole) {
                    builder.append(str);
                } else {
                    builder.append('"').append(str).append('"');
                }
                break;
            case COMMA:
                builder.append(',');
                this.unary.appendTo(builder);
                break;
            case DOT:
                builder.append('.');
                break;
            case QUOTE:
                builder.append('\'');
                this.unary.appendTo(builder);
                break;
            case DYNAMIC_FUNCTION:
                builder.append("#'").append(str);
                break;
            case FUNCTION_ARGUMENT_NAME:
                builder.append("&").append(str);
                break;
            default:
                throw new IllegalArgumentException("Token " + this.token + " is invalid");
        }
        return builder;
    }

    @Override
    public String toString() {
        return this.appendTo(new StringBuilder(), true).toString();
    }

    @Override
    public ILispFunction apply(LispRuntime runtime) {
        switch (this.token) {
            case S_EXPRESSION:
                if (this.expression.isEmpty()) {
                    return new TokenValue(Token.NIL, 0.0);
                } else {
                    return runtime.execute(this);
                }
            case BUILTIN:
            case KEYWORD:
            case PACKAGE:
            case NUMBER_DOUBLE:
            case NUMBER_INTEGER:
            case STRING:
            case T:
            case NIL:
                return this;
            case SYMBOL:
                // if variable is not found - error
                return runtime.tos().get(this.getValue()).apply(runtime);
            case QUOTE:
                return this.unary;

            default:
                throw new IllegalArgumentException("Token " + this.token + " is invalid");
        }
    }

    @Override
    public Object getObject() {
        switch (this.token) {
            case NUMBER_DOUBLE:
                return this.d;
            case NUMBER_INTEGER:
                return this.i;
            case T:
                return 1.0;
            case NIL:
                return 0.0;
            case STRING:
                return this.getValue();
            default:
                throw new IllegalArgumentException(
                        "Token <"+this.token+"> value " + this.getValue() + " cannot be turned into a object");
        }
    }

    @Override
    public Double getDouble() {
        switch (this.token) {
            case NUMBER_DOUBLE:
                return this.d;
            case NUMBER_INTEGER:
                return Double.valueOf(this.i);
            case T:
                return 1.0;
            case NIL:
                return 0.0;
            default:
                throw new IllegalArgumentException(
                        "Token value " + this.getValue() + " cannot be turned into a double");
        }
    }

    @Override
    public TokenValue getUnary() {
        return unary;
    }

    public String getExprSymbol() {
        return this.getExpression().get(0).getValue();
    }

    public List<TokenValue> getExprParameters() {
        return expression.subList(1, this.getExpression().size());
    }


}
