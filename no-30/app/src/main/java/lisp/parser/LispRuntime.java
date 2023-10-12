package lisp.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

public class LispRuntime {

    private HashMap<String, ILispBuiltInFunction> builtIns;
    private HashMap<String, ILispBuiltInFunction> customIns;

    public LispRuntime() {
        this.builtIns = new HashMap<String, ILispBuiltInFunction>();
        this.customIns = new HashMap<String, ILispBuiltInFunction>();
        this.initBuiltIns();
    }

    private void initBuiltIns() {
        new BuiltInLibrary().init(this.builtIns);
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

    public ILispFunction execute(TokenValue tokenValue) {
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
            throw new IllegalArgumentException(
                    String.format("%s is not a function name; try using a symbol instead", symbol));
        } else {
            return tokenValue.apply(this);
        }
    }

    public ILispBuiltInFunction addCustom(String symbol, ILispBuiltInFunction function) {
        this.customIns.put(symbol.toLowerCase(), function);
        return function;
    }
}
