package lisp.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

public class LispRuntime {

    private HashMap<String, ILispBuiltInFunction> builtIns;
    private HashMap<String, ILispBuiltInFunction> customIns;

    private Stack<Context> stack = new Stack<>();
    private Set<String> pureFunctions;

    public LispRuntime() {
        this.builtIns = new HashMap<String, ILispBuiltInFunction>();
        this.customIns = new HashMap<String, ILispBuiltInFunction>();
        this.stack.push(new Context());
        this.pureFunctions = new TreeSet<>();
        this.initBuiltIns();
    }

    private void initBuiltIns() {
        new BuiltInLibrary()
                .init(this.builtIns)
                .initGlobals(this.globals());
    }

    public String executeAndPrint(String command) throws IOException {
        var result = this.execute(command);
        return result != TokenValue.NIL? result.toString() : null;
    }
    public ILispFunction execute(String command) throws IOException {
        return this.execute(new BufferedReader(new StringReader(command)));
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

    public void pushScope() {
        this.stack.push(new Context(this.tos()));
    }
    public void pushScope(String defunName) {
        this.stack.push(new Context(this.tos()));
        this.newScope(defunName);
    }

    public void popScope() {
        this.stack.pop();
    }

    public ILispFunction execute(TokenValue tokenValue) {
        this.pushScope();
        try {
            if (tokenValue.getToken().equals(Token.S_EXPRESSION)) {
                var symbolElement = tokenValue.getExpression().get(0);
                if (symbolElement.getToken() == Token.SYMBOL) {
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
                    throw new IllegalArgumentException("EVAL: undefined function "+symbol);
                } else {
                    return tokenValue;
                }
            } else {
                return tokenValue.apply(this);
            }
        } finally {
            this.popScope();
        }
    }

    public ILispBuiltInFunction addCustom(String symbol, ILispBuiltInFunction function) {
        this.customIns.put(symbol.toLowerCase(), function);
        return function;
    }

    public boolean isPureFunction(String value) {
        return this.pureFunctions.contains(value.toUpperCase());
    }
    public void setPureFunction(String value) {
        this.pureFunctions.add(value.toUpperCase());
    }
}
