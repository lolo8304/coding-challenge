package lisp.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Stack;

public class LispRuntime {

    private HashMap<String, ILispBuiltInFunction> builtIns;
    private HashMap<String, ILispBuiltInFunction> customIns;

    private Stack<Context> stack = new Stack<>();

    public LispRuntime() {
        this.builtIns = new HashMap<String, ILispBuiltInFunction>();
        this.customIns = new HashMap<String, ILispBuiltInFunction>();
        this.stack.push(new Context());
        this.initBuiltIns();
    }

    private void initBuiltIns() {
        new BuiltInLibrary()
                .init(this.builtIns)
                .initGlobals(this.globals());
    }

    public String execute(String command) throws IOException {
        var result = this.execute(new BufferedReader(new StringReader(command)));
        return result.toString();
    }

    public ILispFunction execute(Reader reader) throws IOException {
        var parser = new Parser(reader);
        var expressions = parser.parse();
        ILispFunction lastResponse = null;
        for (ILispFunction tokenValue : expressions) {
            lastResponse = tokenValue.apply(this);
        }
        return lastResponse;
    }

    public Context tos() {
        return this.stack.peek();
    }
    public Context globals() {
        return this.stack.peek().globals();
    }

    public void newScope(String defun) {
        if (this.tos().isGlobalScope()) {
            throw new IllegalArgumentException("Global scope can never be a local scope");
        }
        this.tos().scope(defun);
    }

    public ILispFunction execute(TokenValue tokenValue) {
        this.stack.push(new Context(this.tos()));
        try {
            if (tokenValue.getToken().equals(Token.S_EXPRESSION)) {
                var symbol = tokenValue.getExprSymbol();
                var pars = tokenValue.getExprParameters();
                var builtIn = this.builtIns.get(symbol.toLowerCase());
                if (builtIn != null) {
                    return builtIn.apply(this, tokenValue, symbol, pars);
                }
                var custom = this.customIns.get(symbol.toLowerCase());
                if (custom != null) {
                    return custom.apply(this, tokenValue, symbol, pars);
                }
                return tokenValue;
            } else {
                return tokenValue.apply(this);
            }
        } finally {
            this.stack.pop();
        }
    }

    public ILispBuiltInFunction addCustom(String symbol, ILispBuiltInFunction function) {
        this.customIns.put(symbol.toLowerCase(), function);
        return function;
    }

}
