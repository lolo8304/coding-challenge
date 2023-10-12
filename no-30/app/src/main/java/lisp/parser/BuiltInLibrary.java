package lisp.parser;

import java.util.HashMap;
import java.util.List;

public class BuiltInLibrary {
    public BuiltInLibrary() {
    }

    private ILispBuiltInFunction addFunction() {
        return new ILispBuiltInFunction() {
            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                    List<? extends ILispFunction> pars) {
                var sum = 0.0;
                for (ILispFunction token : pars) {
                    sum += (Double) token.getDouble();
                }
                return new TokenValue(Token.NUMBER_DOUBLE, sum);
            }
        };
    }

    // (defun doublen (n) (* n 2))
    // defun <symbol> expr(par1, par2, ) expr
    private ILispBuiltInFunction defunFunction() {
        return new ILispBuiltInFunction() {
            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                    List<? extends ILispFunction> pars) {
                runtime.addCustom(symbol, new DefunBuiltIn(expr, symbol, pars));
                return expr;
            }
        };
    }

    public void init(HashMap<String, ILispBuiltInFunction> builtIns) {
        builtIns.put("+", this.addFunction());
        builtIns.put("defun", this.defunFunction());
    }

    public static class DefunBuiltIn implements ILispBuiltInFunction {

        private ILispFunction expr;
        private String symbol;
        private List<? extends ILispFunction> pars;

        public DefunBuiltIn(ILispFunction expr, String symbol,
                List<? extends ILispFunction> pars) {
            this.expr = expr;
            this.symbol = symbol;
            this.pars = pars;
        }

        @Override
        public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                List<? extends ILispFunction> pars) {
            // TODO: needs evaluation along the expression tree
            throw new IllegalAccessError("Not implemented yet");
        }
    }
}
