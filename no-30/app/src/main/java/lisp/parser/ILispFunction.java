package lisp.parser;

import java.util.List;

public interface ILispFunction {
    ILispFunction apply(LispRuntime runtime);

    Object getObject();

    Double getDouble();
    Integer getInteger();
    String getValue();

    Token getToken();

    ILispFunction getUnary();

    List<? extends ILispFunction> getExpression();
}
