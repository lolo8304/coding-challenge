package lisp.parser;

public class Expression implements ILispFunction {
    protected TokenValue token;

    public Expression(TokenValue token) {
        this.token = token;
    }

    @Override
    public ILispFunction apply(LispRuntime runtime) {
        return this.token.apply(runtime);
    }

    @Override
    public Object getObject() {
        return this.token.getObject();
    }

    @Override
    public Double getDouble() {
        return this.token.getDouble();
    }

    @Override
    public Token getToken() {
        return this.token.getToken();
    }
}
