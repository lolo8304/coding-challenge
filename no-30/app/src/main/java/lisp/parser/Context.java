package lisp.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

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

    public ILispFunction getIfAbsent(String symbol, Function<Void, ILispFunction> callback) {
        if (this.isLocalScope()) {
            var variable = this.variables.get(symbol);
            if (variable == null) {
                if (this.prev != null) {
                    return this.prev.getIfAbsent(symbol, callback);
                } else {
                    return callback.apply(null);
                }
            }
            return variable;
        } else if (this.isGlobalScope()) {
            var variable = this.variables.get(symbol);
            if (variable == null) {
                return callback.apply(null);
            }
            return variable;
        } else {
            return this.global.getIfAbsent(symbol, callback);
        }
    }

    public ILispFunction get(String symbol) {
        return this.getIfAbsent(symbol, (unused) -> {
            throw new IllegalArgumentException("Evaluation aborted on #<UNBOUND-VARIABLE "+symbol+">");
        });
    }

    public ILispFunction getOrNil(String symbol) {
        return this.getIfAbsent(symbol, (unused) -> TokenValue.NIL);
    }

    public void putGlobal(String symbol, ILispFunction value) {
        this.global.variables.put(symbol, value);
    }
    public void putLocal(String symbol, ILispFunction value) {
        this.variables.put(symbol, value);
    }
    public void put(String symbol, ILispFunction value) {
        if (this.isLocalScope()) {
            putLocal(symbol, value);
        } else if (this.isGlobalScope()){
            putLocal(symbol, value);
        } else {
            this.putGlobal(symbol, value);
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
