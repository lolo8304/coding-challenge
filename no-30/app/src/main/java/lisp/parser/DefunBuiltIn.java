package lisp.parser;

import java.util.List;

public class DefunBuiltIn implements ILispBuiltInFunction {

    private ILispFunction expr;
    private String symbol;
    private ILispFunction vars;
    private final ILispFunction docu;
    private ILispFunction func;

    public DefunBuiltIn(String symbol, ILispFunction vars, ILispFunction docu, ILispFunction func) {
        this.symbol = symbol;
        this.vars = vars;
        this.docu = docu;
        this.func = func;
    }

    @Override
    public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                               List<? extends ILispFunction> pars) {

        if (vars.getExpression().size() == pars.size()) {
            try {
                runtime.pushScope(symbol);
                for (int i = 0; i < pars.size(); i++) {
                    var variable = vars.getExpression().get(i).getValue();
                    var value = pars.get(i).apply(runtime);
                    runtime.tos().put(variable, value);
                }
                return this.func.apply(runtime);
            } finally {
                runtime.popScope();
            }
        } else {
            throw new IllegalArgumentException("function "+symbol+ " has not same vars ("+vars.getExpression().size()+") than pars ("+pars
                    .size()+")");
        }
    }
}
