package lisp.parser;

import java.util.List;

public interface ILispBuiltInFunction {
    ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
            List<? extends ILispFunction> pars);
}