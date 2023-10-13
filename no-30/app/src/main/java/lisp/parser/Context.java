package lisp.parser;

import java.util.HashMap;
import java.util.Map;

public class Context {

    private final Map<String, ILispFunction> variables;
    private final Context prev;
    private final Context global;

    private String scope;

    public Context(Context prev) {
        this.prev = prev;
        if (prev == null) {
            this.global = this;
        } else {
            this.global = prev.global;
            this.scope = prev.scope;
        }
        this.variables = new HashMap<>();
    }
    public Context() {
        this(null);
    }

    public Context globals() {
        return this.global;
    }

    public ILispFunction get(String symbol) {
        if (this.isLocalScope()) {
            var variable = this.variables.get(symbol);
            if (variable == null) {
                if (this.prev != null) {
                    return this.prev.get(symbol);
                } else {
                    return new TokenValue(Token.NIL, 0.0);
                }
            }
            return variable;
        } else if (this.isGlobalScope()) {
            var variable = this.variables.get(symbol);
            if (variable == null) {
                return new TokenValue(Token.NIL, 0.0);
            }
            return variable;
        } else {
            return this.global.get(symbol);
        }
    }
    public void put(String symbol, ILispFunction value) {
        if (this.isLocalScope()) {
            this.variables.put(symbol, value);
        } else if (this.isGlobalScope()){
            this.variables.put(symbol, value);
        } else {
            this.global.put(symbol, value);
        }
    }

    public void scope(String defun) {
        this.scope = defun;
    }

    public boolean isLocalScope() {
        return this.scope != null;
    }
    public boolean isGlobalScope() {
        return this.prev == null;
    }
    public boolean isNormalScope() {
        return !this.isLocalScope();
    }
}
