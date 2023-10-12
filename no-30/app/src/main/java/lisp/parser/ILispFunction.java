package lisp.parser;

public interface ILispFunction {
    ILispFunction apply(LispRuntime runtime);

    Object getObject();

    Double getDouble();
}
