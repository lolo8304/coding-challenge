package lisp.parser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DefunBuiltIn implements ILispBuiltInFunction {

    
    private final Map<String, ILispFunction> cache;
    private final MessageDigest sha256;
    private ILispFunction expr;
    private String symbol;
    private ILispFunction vars;
    private final ILispFunction docu;
    private ILispFunction[] func;
    
    public DefunBuiltIn(String symbol, ILispFunction vars, ILispFunction docu, ILispFunction... func) {
        this.symbol = symbol;
        this.vars = vars;
        this.docu = docu;
        this.func = func;

        try {
            this.sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        this.cache = new Hashtable<String, ILispFunction>();
    }
    
    private String sha256(String value) {
        byte[] inputBytes = value.getBytes();
        byte[] hashBytes = sha256.digest(inputBytes);
        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private Optional<ILispFunction> cached(String sha256Key) {
        var found = this.cache.get(sha256Key);
        return found != null? Optional.of(found) : Optional.empty();
    }
    private ILispFunction cached(String sha256Key, ILispFunction value) {
        this.cache.put(sha256Key, value);
        return value;
    }

    @Override
    public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                               List<? extends ILispFunction> pars) {

        if (vars.getExpression().size() == pars.size()) {
            try {
                runtime.pushScope(symbol);
                var isPureFunction = runtime.isPureFunction(symbol);
                var hashBuilder = new StringBuilder();
                for (int i = 0; i < pars.size(); i++) {
                    var variable = vars.getExpression().get(i).getValue();
                    var value = pars.get(i).apply(runtime);
                    if (isPureFunction) hashBuilder.append("||").append(variable).append("=").append(value);
                    runtime.tos().put(variable, value);
                }
                Optional<ILispFunction> resultCached = Optional.empty();
                var sha256Key = "";
                if (isPureFunction) {
                    var keyAndArguments = hashBuilder.toString();
                    sha256Key = this.sha256(keyAndArguments);
                    resultCached = this.cached(sha256Key);
                    if (resultCached.isPresent()) {
                        System.out.println("Cached result: " + symbol + ": " + keyAndArguments);
                        return resultCached.get();
                    }
                }
                ILispFunction result = TokenValue.NIL;
                for (ILispFunction iLispFunction : this.func) {
                    result = iLispFunction.apply(runtime);
                }
                if (isPureFunction) {
                    this.cached(sha256Key, result);
                }
                return result;
            } finally {
                runtime.popScope();
            }
        } else {
            throw new IllegalArgumentException("function "+symbol+ " has not same vars ("+vars.getExpression().size()+") than pars ("+pars
                    .size()+")");
        }
    }
}
