package lisp.parser;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TokenValue implements ILispFunction {
    private static final ArrayList<TokenValue> EMPTY_ARRAY_LIST = new ArrayList<>();
    private static final Random RANDOM = new SecureRandom();
    public static final TokenValue NIL = new TokenValue(Token.NIL,0.0);
    public static TokenValue ZERO = new TokenValue(Token.NUMBER_DOUBLE, 0.0);
    public static TokenValue ZERO_INT = new TokenValue(Token.NUMBER_INTEGER, 0);
    public static TokenValue ONE = new TokenValue(Token.NUMBER_DOUBLE, 1.0);
    public static TokenValue ONE_INT = new TokenValue(Token.NUMBER_INTEGER, 1);

    private final Token token;
    private final String str;
    private final Double d;
    private final Integer i;
    private List<TokenValue> expression;

    private Tensor tensor;
    private TokenValue unary;
    private int dimension = 0;

    public TokenValue(Token token) {
        this.token = token;
        this.str = null;
        this.expression = EMPTY_ARRAY_LIST;
        this.d = null;
        this.i = null;
    }

    public TokenValue(String str) {
        this(Token.STRING, str);
    }

    public TokenValue(Token token, String str) {
        this.token = token;
        this.str = str;
        this.expression = EMPTY_ARRAY_LIST;
        this.d = null;
        this.i = null;
    }

    public TokenValue(Token token, List<? extends ILispFunction> expression) {
        this.token = token;
        this.expression = expression.stream().map( x -> (TokenValue)x).toList();
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

    public TokenValue(Double d) {
        this(Token.NUMBER_DOUBLE, d);
    }

    public TokenValue(Token numberDouble, Double d) {
        this.token = numberDouble;
        this.str = String.format("%f", d);
        this.d = d;
        this.i = null;
    }
    public TokenValue(Integer i) {
        this(Token.NUMBER_INTEGER, i);
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

    public TokenValue(Tensor tensor) {
        this.token = Token.S_EXPRESSION;
        this.tensor = tensor;
        this.expression = tensor.toList();
        this.str = null;
        this.d = null;
        this.i = null;
    }

    public static TokenValue randomDouble() {
        return new TokenValue(Token.NUMBER_DOUBLE, RANDOM.nextDouble());
    }
    public static TokenValue randomDouble(double max) {
        return new TokenValue(Token.NUMBER_DOUBLE, RANDOM.nextDouble(max));
    }
    public static TokenValue randomInteger(int max) {
        return new TokenValue(Token.NUMBER_INTEGER, RANDOM.nextInt(max));
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

    @Override
    public ILispFunction get(int... indices) {
        if (this.tensor != null) {
            return this.tensor.get(indices);
        } else if (indices.length == 1) {
            return this.getExpression().get(indices[0]);
        } else {
            throw new IllegalArgumentException("cannot access multiple dimensions without tensor");
        }
    }

    @Override
    public ILispFunction get(int index) {
        return this.expression.get(index);
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
                var count = 0;
                for (TokenValue value : this.expression) {
                    if (count == 20) {
                        builder.append(" ... (size="+this.expression.size()+")");
                    } else if (count > 20) {
                        // skip
                    }else {
                        if (count > 0) {
                            builder.append(" ");
                        }
                        value.appendTo(builder);
                    }
                    count++;
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
                    return TokenValue.NIL;
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
