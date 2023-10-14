package lisp.parser;

import java.security.SecureRandom;
import java.util.*;
import java.util.function.Function;

import static lisp.parser.Token.*;

public class BuiltInLibrary {
    private static SecureRandom RANDOM = new SecureRandom();

    public BuiltInLibrary() {
    }

    private ILispBuiltInFunction addFunction() {
        return new ILispBuiltInFunction() {
            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                    List<? extends ILispFunction> pars) {
                var sum = 0.0;
                for (ILispFunction token : pars) {
                    sum += (Double) token.apply(runtime).getDouble();
                }
                return new TokenValue(Token.NUMBER_DOUBLE, sum);
            }
        };
    }

    private ILispBuiltInFunction multFunction() {
        return new ILispBuiltInFunction() {
            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                    List<? extends ILispFunction> pars) {
                var sum = 1.0;
                for (ILispFunction token : pars) {
                    sum *= (Double) token.apply(runtime).getDouble();
                }
                return new TokenValue(Token.NUMBER_DOUBLE, sum);
            }
        };
    }

    private ILispBuiltInFunction substractFunction() {
        return new ILispBuiltInFunction() {
            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                    List<? extends ILispFunction> pars) {
                if (!pars.isEmpty()) {
                    var sum = pars.get(0).apply(runtime).getDouble();
                    for (int i = 1; i < pars.size(); i++) {
                        sum -= (Double) pars.get(i).apply(runtime).getDouble();
                    }
                    return new TokenValue(Token.NUMBER_DOUBLE, sum);
                } else {
                    return new TokenValue(Token.NUMBER_DOUBLE, 0.0);
                }
            }
        };
    }

    private ILispBuiltInFunction divisionFunction() {
        return new ILispBuiltInFunction() {
            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                    List<? extends ILispFunction> pars) {
                if (!pars.isEmpty()) {
                    var sum = pars.get(0).apply(runtime).getDouble();
                    for (int i = 1; i < pars.size(); i++) {
                        sum /= (Double) pars.get(i).apply(runtime).getDouble();
                    }
                    return new TokenValue(Token.NUMBER_DOUBLE, sum);
                } else {
                    return new TokenValue(Token.NUMBER_DOUBLE, 0.0);
                }
            }
        };
    }

    private ILispBuiltInFunction moduloFunction() {
        return new ILispBuiltInFunction() {
            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                    List<? extends ILispFunction> pars) {
                if (!pars.isEmpty()) {
                    var sum = pars.get(0).apply(runtime).getDouble();
                    for (int i = 1; i < pars.size(); i++) {
                        sum %= (Double) pars.get(i).apply(runtime).getDouble();
                    }
                    return new TokenValue(Token.NUMBER_DOUBLE, sum);
                } else {
                    return new TokenValue(Token.NUMBER_DOUBLE, 0.0);
                }
            }
        };
    }

    private ILispBuiltInFunction eqFunction() {
        return new ILispBuiltInFunction() {
            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                    List<? extends ILispFunction> pars) {
                if (pars.size() > 1) {
                    var val = pars.get(0).apply(runtime).getDouble();
                    for (int i = 1; i < pars.size(); i++) {
                        var compare = (Double) pars.get(i).apply(runtime).getDouble();
                        if (!val.equals(compare)) {
                            return new TokenValue(NIL, 0.0);
                        }
                    }
                    return new TokenValue(Token.T, 1.0);
                } else {
                    return new TokenValue(NIL, 0.0);
                }
            }
        };
    }

    private ILispBuiltInFunction lessThanFunction() {
        return new ILispBuiltInFunction() {
            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                    List<? extends ILispFunction> pars) {
                if (pars.size() > 1) {
                    var val = pars.get(0).apply(runtime).getDouble();
                    for (int i = 1; i < pars.size(); i++) {
                        var compare = (Double) pars.get(i).apply(runtime).getDouble();
                        if (val.compareTo(compare) >= 0) {
                            return new TokenValue(NIL, 0.0);
                        }
                    }
                    return new TokenValue(Token.T, 1.0);
                } else {
                    return new TokenValue(NIL, 0.0);
                }
            }
        };
    }


    private ILispBuiltInFunction lessEqualThanFunction() {
        return new ILispBuiltInFunction() {
            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                                       List<? extends ILispFunction> pars) {
                if (pars.size() > 1) {
                    var val = pars.get(0).apply(runtime).getDouble();
                    for (int i = 1; i < pars.size(); i++) {
                        var compare = (Double) pars.get(i).apply(runtime).getDouble();
                        if (val.compareTo(compare) > 0) {
                            return new TokenValue(NIL, 0.0);
                        }
                    }
                    return new TokenValue(Token.T, 1.0);
                } else {
                    return new TokenValue(NIL, 0.0);
                }
            }
        };
    }

    private ILispBuiltInFunction greaterThanFunction() {
        return new ILispBuiltInFunction() {
            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                    List<? extends ILispFunction> pars) {
                if (pars.size() > 1) {
                    var val = pars.get(0).apply(runtime).getDouble();
                    for (int i = 1; i < pars.size(); i++) {
                        var compare = (Double) pars.get(i).apply(runtime).getDouble();
                        if (val.compareTo(compare) <= 0) {
                            return new TokenValue(NIL, 0.0);
                        }
                    }
                    return new TokenValue(Token.T, 1.0);
                } else {
                    return new TokenValue(NIL, 0.0);
                }
            }
        };
    }


    private ILispBuiltInFunction greaterEqualThanFunction() {
        return new ILispBuiltInFunction() {
            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                                       List<? extends ILispFunction> pars) {
                if (pars.size() > 1) {
                    var val = pars.get(0).apply(runtime).getDouble();
                    for (int i = 1; i < pars.size(); i++) {
                        var compare = (Double) pars.get(i).apply(runtime).getDouble();
                        if (val.compareTo(compare) < 0) {
                            return new TokenValue(NIL, 0.0);
                        }
                    }
                    return new TokenValue(Token.T, 1.0);
                } else {
                    return new TokenValue(NIL, 0.0);
                }
            }
        };
    }

    private ILispBuiltInFunction notFunction() {
        return new ILispBuiltInFunction() {
            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                    List<? extends ILispFunction> pars) {
                if (pars.size() == 1) {
                    var token = pars.get(0).apply(runtime).getToken();
                    // everything that is NOT NIL --> NIL
                    // if NIL --> T
                    if (token == NIL) {
                        return new TokenValue(Token.T, 1.0);
                    } else {
                        return new TokenValue(NIL, 0.0);
                    }
                } else {
                    throw new IllegalArgumentException("NOT function must have at least 1 element");
                }
            }
        };
    }

    private ILispBuiltInFunction andFunction() {
        return new ILispBuiltInFunction() {
            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                    List<? extends ILispFunction> pars) {
                if (!pars.isEmpty()) {
                    for (int i = 0; i < pars.size(); i++) {
                        var token = pars.get(i).apply(runtime).getToken();
                        if (token.equals(NIL)) {
                            return new TokenValue(NIL, 0.0);
                        }
                    }
                    return new TokenValue(Token.T, 1.0);

                } else {
                    throw new IllegalArgumentException("AND function must have at least 1 element");
                }
            }
        };
    }

    private ILispBuiltInFunction orFunction() {
        return new ILispBuiltInFunction() {
            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                    List<? extends ILispFunction> pars) {
                if (!pars.isEmpty()) {
                    for (int i = 0; i < pars.size(); i++) {
                        var token = pars.get(i).apply(runtime).getToken();
                        if (!token.equals(NIL)) {
                            return new TokenValue(Token.T, 1.0);
                        }
                    }
                    return new TokenValue(NIL, 0.0);

                } else {
                    throw new IllegalArgumentException("AND function must have at least 1 element");
                }
            }
        };
    }

    private ILispBuiltInFunction printFunction() {
        return new ILispBuiltInFunction() {
            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                    List<? extends ILispFunction> pars) {
                var first = true;
                for (ILispFunction token : pars) {
                    if (first) {
                        first = false;
                    } else {
                        System.out.print(" ");
                    }
                    System.out.print(token.apply(runtime));
                }
                System.out.println("");

                return new TokenValue(NIL, 0.0);
            }
        };
    }

    private ILispBuiltInFunction formatFunction() {
        return new ILispBuiltInFunction() {

            private String migrateTemplateToJavaFormat(String template) {
                var builder = new StringBuilder();
                var nextIsFormat = false;
                for (int i = 0; i < template.length(); i++) {
                    var ch = template.charAt(i);
                    if (nextIsFormat) {
                        nextIsFormat = false;
                        switch (ch) {
                            case 'A', 'S': builder.append("%s"); break;
                            case '%': builder.append("%n"); break;
                            case '&': builder.append("%n"); break;
                            case 'T': builder.append("\t"); break;
                            case 'D': builder.append("%s"); break;
                            case 'F': builder.append("%.2f"); break;
                            case 'X': builder.append("%x"); break;
                            case 'O': builder.append("%o"); break;
                            default: builder.append('~').append(ch);
                        }
                    } else {
                        if (ch == '~') {
                            nextIsFormat = true;
                        } else {
                            builder.append(ch);
                        }
                    }
                }
                // System.out.println("Translate <"+template+">=<"+builder.toString()+">");
                return builder.toString();
            }
            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                                       List<? extends ILispFunction> pars) {
                if (pars.size() > 1) {
                    var output = pars.get(0).toString();
                    var template = pars.get(1).toString();
                    var args = pars.subList(2, pars.size());
                    var argsResult = args.stream().map( (x) -> x.apply(runtime)).toArray();
                    if (output.equals("t")) {
                        if (argsResult.length > 0) {
                            System.out.print(String.format(this.migrateTemplateToJavaFormat(template), argsResult));
                        } else {
                            System.out.print(String.format(this.migrateTemplateToJavaFormat(template)));
                        }
                    } else {
                        throw new IllegalArgumentException("format: only output format <t> is implemented");
                    }
                } else {
                    throw new IllegalArgumentException("format expression must contain <stream> (.e.g t), a string and optional parameters");
                }
                return new TokenValue(NIL, 0.0);
            }
        };
    }

    private ILispBuiltInFunction ifFunction() {
        return new ILispBuiltInFunction() {

            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                                       List<? extends ILispFunction> pars) {
                if (pars.size() > 1) {
                    var condition = pars.get(0).apply(runtime).getToken();
                    var trueCondition = pars.get(1);
                    ILispFunction elseCondition = new TokenValue(NIL, 0.0);
                    if (pars.size() > 2) {
                        elseCondition = pars.get(2);
                    }
                    if (condition.equals(Token.T)) {
                        return trueCondition.apply(runtime);
                    } else {
                        return elseCondition.apply(runtime);
                    }
                } else {
                    throw new IllegalArgumentException("if format must contain at least 2 elements: if <cond> <true-expression> [<else expre>]");
                }
            }
        };
    }


    private ILispBuiltInFunction setqFunction() {
        return new ILispBuiltInFunction() {

            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                                       List<? extends ILispFunction> pars) {
                if (pars.size() >= 2 && pars.size() % 2 == 0) {
                    for (int i = 0; i < pars.size(); i++) {
                        var variable = pars.get(i++).getValue();
                        var exprOrVariable = pars.get(i).apply(runtime);
                        runtime.tos().put(variable, exprOrVariable);
                    }
                    return new TokenValue(NIL, 0.0);
                } else {
                    throw new IllegalArgumentException("setq must have 2 or even number of parameters: setq <symbol> <expression of value> | setq <s1> <exp1> <s2> <exp2> ...");
                }
            }
        };
    }

    // (defparameter symbol initial-value &optional doc-string)
    private ILispBuiltInFunction defparameterAndDefVarFunction() {
        return new ILispBuiltInFunction() {

            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                                       List<? extends ILispFunction> pars) {
                if (pars.size() == 2 || pars.size() == 3) {
                    var parName = pars.get(0).getValue();
                    var initValue = pars.get(1).apply(runtime);
                    ILispFunction docString = new TokenValue(Token.STRING, "");
                    if (pars.size() == 3) {
                        docString = pars.get(2);
                    }
                    if (symbol.equals("defvar")) { // defvar: only defined once cannot be overwritten
                        var existingValue = runtime.tos().globals().getOrNil(parName);
                        if (existingValue != null && !existingValue.getToken().equals(NIL)) {
                            // do not overwrite the value
                            return new TokenValue(NIL, 0.0);
                        }
                    }
                    // defparameter or not existing value
                    runtime.tos().putGlobal(parName, initValue);
                    return new TokenValue(NIL, 0.0);
                } else {
                    throw new IllegalArgumentException("set1 must have 2 parameters: setq <symbol> <expression of value>");
                }
            }
        };
    }

    // (make-array dimensions &key :element-type :initial-contents :initial-element)
    //
    private ILispBuiltInFunction makeArrayFunction() {
        return new ILispBuiltInFunction() {

            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                                       List<? extends ILispFunction> pars) {
                var dimensions = pars.get(0);
                var map = parseDynamicMap(pars, 1);
                var elementType = map.get(":element-type");
                var initialContent = map.get(":initial-contents");
                var initialElement = map.get(":initial-element");
                if (elementType == null) {
                    elementType = new TokenValue(Token.QUOTE, new TokenValue(Token.SYMBOL, "t"));
                }
                if (elementType.getToken() != Token.QUOTE) {
                    throw new IllegalArgumentException(":element-type must be quoted values of 'integer 'float 'string 't (any)");
                }
                var elementTypeSymbol = elementType.apply(runtime).getValue();
                return initialContent != null ? initialContent.apply(runtime) : this.makeArray(runtime, dimensions, elementTypeSymbol, initialElement);
            }

            private ILispFunction makeArray(int size, Function<Void, ILispFunction> initialValueCallback) {
                List<TokenValue> list = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    //TODO: check how to solve casting issue with list and interface later
                    list.add((TokenValue) initialValueCallback.apply(null));
                }
                return new TokenValue(Token.S_EXPRESSION, list);
            }

            private ILispFunction makeArray(LispRuntime runtime, ILispFunction dimensions, String elementTypeSymbol, ILispFunction initialElement) {
                if (initialElement == null) {
                    initialElement = new TokenValue(NIL, 0.0);
                }
                var initialValue = initialElement.apply(runtime);
                if (dimensions.getToken() == Token.NUMBER_INTEGER || (dimensions.getToken() == Token.QUOTE && dimensions.getUnary().getToken() == Token.NUMBER_INTEGER)) {
                    // 1 dimensional vector
                    var size = dimensions.apply(runtime).getInteger();
                    return this.makeArray(size, (unused -> initialValue));
                }
                ILispFunction dimensionToken;
                if (dimensions.getToken() == Token.S_EXPRESSION) {
                    dimensionToken = dimensions;
                } else if (dimensions.getToken() == Token.QUOTE && dimensions.getUnary().getToken() == Token.S_EXPRESSION) {
                    dimensionToken = dimensions.apply(runtime);
                } else {
                    throw new IllegalArgumentException("Invalid multi dimensions '(1 2) ");
                }
                var xD = dimensionToken.getExpression().size();
                if (xD == 2) {
                    // 1 dimensional vector
                    var size2 = dimensionToken.getExpression().get(0).apply(runtime).getInteger();
                    var size1 = dimensionToken.getExpression().get(1).apply(runtime).getInteger();
                    return this.makeArray(size2, unused -> this.makeArray(size1, unused1 -> initialValue));
                } else {
                    var innerInitialValue = initialValue;
                    while (xD > 0) {
                        xD--;
                        var size_i = dimensionToken.getExpression().get(xD).apply(runtime).getInteger();
                        ILispFunction finalInnerInitialValue = innerInitialValue; // effectivly final !!!
                        innerInitialValue = this.makeArray(size_i, unused -> finalInnerInitialValue);
                    }
                    return innerInitialValue;
                }
            }
        };
    }

    // (random &optional limit)
    // default 0 - 1.0, if integer then only integer
    private ILispBuiltInFunction randomFunction() {
        return new ILispBuiltInFunction() {

            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                                       List<? extends ILispFunction> pars) {
                if (pars.size() < 2) {
                    if (pars.size() == 1) {
                        var max = pars.get(0).apply(runtime);
                        if (max.getToken() == Token.NUMBER_INTEGER) {
                            return new TokenValue(Token.NUMBER_INTEGER, RANDOM.nextInt(max.getInteger()));
                        } else {
                            return new TokenValue(Token.NUMBER_INTEGER, RANDOM.nextDouble() * Math.abs(max.getDouble()));
                        }
                    }
                    return new TokenValue(Token.NUMBER_DOUBLE, RANDOM.nextDouble());
                } else {
                    throw new IllegalArgumentException("random can optionally have only 1 parameter");
                }
            }
        };
    }

    private Map<String, ILispFunction> parseDynamicMap(List<? extends ILispFunction> pars, int startingAt) {
        var map = new HashMap<String, ILispFunction>();
        for (int i = startingAt; i < pars.size(); i++) {
            var keyword = pars.get(i++);
            if (keyword.getToken() != Token.KEYWORD) {
                throw new IllegalArgumentException("keywords are only allowed to make array: :element-type :initial-contents :initial-element");
            }
            var symbol = keyword.getValue();
            if (!Objects.equals(symbol, ":element-type") && !Objects.equals(symbol, ":initial-contents") && !Objects.equals(symbol, ":initial-element")) {
                throw new IllegalArgumentException("Illegal keyword '"+symbol+"' for make array. Allowed are: :element-type :initial-contents :initial-element");
            }
            var value = pars.get(i);
            map.put(symbol, value);
        }
        return map;
    }


    // (let ((var1 val1) (var2 val2) ...) body...)
    //      define in local scope "only" not global and return body
    //      set local vars in paralle - no-re read from previous vars
    // (let* ((var1 val1) (var2 val2) ...) body...): The (let*) form is similar to (let), but it binds variables sequentially
    //      same as let, but sequential - possible to re-read from previous let vars
    private ILispBuiltInFunction letAndLetAsterixFunction() {
        return new ILispBuiltInFunction() {

            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                                       List<? extends ILispFunction> pars) {
                if (pars.size() >= 2) {
                    runtime.pushScope("let"); // ensure scope only for the "let" command
                    try {
                        // ( (var1 value1) (var2 value2) )
                        var varAndValueDeclaration = pars.get(0);
                        if (symbol.equals("let")) {
                            // execute in parallel or evaluate first and then set
                            var parallelVariableMap = new HashMap<String, ILispFunction>();
                            for (var varAndValue : varAndValueDeclaration.getExpression()) {
                                var variable = varAndValue.getExpression().get(0).getValue();
                                var value = varAndValue.getExpression().get(1).apply(runtime);
                                parallelVariableMap.put(variable, value);
                            }
                            for (var entry :
                                    parallelVariableMap.entrySet()) {
                                runtime.tos().putLocal(entry.getKey(), entry.getValue());
                            }
                        } else {
                            // execute in sequence
                            for (var varAndValue : varAndValueDeclaration.getExpression()) {
                                var variable = varAndValue.getExpression().get(0).getValue();
                                var value = varAndValue.getExpression().get(1).apply(runtime);
                                runtime.tos().putLocal(variable, value);
                            }
                        }
                        ILispFunction lastResult = new TokenValue(NIL);
                        for (int i = 1; i < pars.size(); i++) {
                            var body = pars.get(i);
                            lastResult = body.apply(runtime);
                        }
                        return lastResult;
                    } finally {
                        runtime.popScope();
                    }
                } else {
                    throw new IllegalArgumentException("let must have >= 2 parameters: let ((var1 val1) (var2 val2)) <body> <body> ...");
                }
            }
        };
    }


    // (dotimes (var count &optional result-form) body)
    // var: This is a variable that is locally bound within the loop and iterates from 0 to count - 1. It takes on values from 0 up to count - 1 during each iteration.
    // count: This is the number of times the body should be executed.
    // result-form (optional): This is an expression that, if provided, is evaluated once after the loop has completed. The result of the result-form is returned as the result of the (dotimes) expression.
    private ILispBuiltInFunction dotimesFunction() {
        return new ILispBuiltInFunction() {

            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                                       List<? extends ILispFunction> pars) {
                if (pars.size() == 2) {
                    runtime.pushScope("dotimes");
                    try {
                        // (dotimes (var count &optional result-form) body)
                        var counter = pars.get(0);
                        var variable = counter.getExpression().get(0).getValue();
                        var count = counter.getExpression().get(1).apply(runtime);

                        runtime.tos().put(variable, count);
                        var countInt = count.getInteger();
                        var body = pars.get(1);
                        for (int i = 0; i < countInt; i++) {
                            body.apply(runtime);
                        }
                        ILispFunction result = new TokenValue(NIL);
                        if (counter.getExpression().size() == 3) {
                            // include result form
                            result = counter.getExpression().get(2).apply(runtime);
                        }
                        return result;
                    } finally {
                        runtime.popScope();
                    }
                } else {
                    throw new IllegalArgumentException("dottimes must have 2 parameters: (dotimes (var count &optional result-form) body)");
                }
            }
        };
    }


    // (setf place value)
    // set a variable:
    //      (setq x 42) ; Set the value of the variable x to 42
    //      (setf x 42) ; Equivalent to the above
    // Modifying the contents of a data structure
    //      (setf (aref my-array 1) 100) ; Set the second element of my-array to 100
    //      (setf (slot-value my-object 'slot-name) new-value) ; Set the value of a slot in an object
    // Setting values in a nested data structure
    //      (setf (cdr (car my-list)) 42) ; Set the second element of the first sublist in a list to 42
    // Setting the value of a symbol using (symbol-value):
    //      (setf (symbol-value 'my-variable) 42) ; Set the value of my-variable to 42
    // Modifying the value returned by a function using (funcall):
    //      (setf (funcall (symbol-function 'my-function) arg) new-value) ; Set the return value of my-function to new-value
    private ILispBuiltInFunction setfFunction() {
        return new ILispBuiltInFunction() {

            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                                       List<? extends ILispFunction> pars) {
                if (pars.size() == 2) {
                    var variable = pars.get(0);
                    if (variable.getToken() == SYMBOL) {
                        var exprOrVariable = pars.get(1).apply(runtime);
                        runtime.tos().put(variable.getValue(), exprOrVariable);
                        return new TokenValue(NIL, 0.0);
                    }
                    if (variable.getToken() == S_EXPRESSION && variable.getExpression().get(0).getValue().equals("aref")) {
                        // (setf (aref my-array 1) 100)
                        // do not run (aref ...) just use definitions to access array and adapt it
                        var arrayVariableName = variable.getExpression().get(1).getValue();
                        var array = runtime.tos().get(arrayVariableName);
                        var index = variable.getExpression().get(2).apply(runtime).getInteger();
                        var newValue = pars.get(1).apply(runtime);

                        var casting = (List<TokenValue>)array.getExpression(); // issue with <? extends ILispFunction
                        casting.set(index, (TokenValue)newValue);
                        return new TokenValue(NIL, 0.0);
                    }
                    throw new IllegalArgumentException("setf type is not supported yet - "+expr.toString());
                } else {
                    throw new IllegalArgumentException("setf must have 2 : setf place value");
                }
            }
        };
    }

    // (aref my-array 10)
    private ILispBuiltInFunction arefFunction() {
        return new ILispBuiltInFunction() {

            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                                       List<? extends ILispFunction> pars) {
                if (pars.size() >= 2) {
                    var arrayVariableName = pars.get(0).getValue();
                    var array = runtime.tos().get(arrayVariableName);
                    var result = array;
                    for (int i = 1; i < pars.size(); i++) {
                        var index = pars.get(i).apply(runtime).getInteger();
                        if (array.getToken() != S_EXPRESSION) {
                            throw new IllegalArgumentException("Array referenced is not an array ("+array.getToken()+") - "+array.toString());
                        }
                        result = result.getExpression().get(index);
                    }
                    return result;
                } else {
                    throw new IllegalArgumentException("aref must have > 2 parameters: aref var index1 index2 ... - "+expr);
                }
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
                if (pars.size() == 3 || pars.size() == 4) {
                    var funcName = pars.get(0).getValue();
                    var vars = pars.get(1);
                    ILispFunction docu = new TokenValue(Token.STRING, "");
                    var func = pars.get(2);
                    if (func.getToken() == Token.STRING) {
                        docu = func;
                        func = pars.get(3);
                    }
                    runtime.addCustom(funcName, new DefunBuiltIn(funcName, vars, docu, func));
                    return new TokenValue(Token.SYMBOL, symbol.toUpperCase());
                } else {
                    throw new IllegalArgumentException("defun must have 3 or 4 parameters: func name, vars-expr, [docustring], func-expr");
                }
            }
        };
    }

    public BuiltInLibrary init(HashMap<String, ILispBuiltInFunction> builtIns) {
        builtIns.put("+", this.addFunction());
        builtIns.put("*", this.multFunction());
        builtIns.put("-", this.substractFunction());
        builtIns.put("/", this.divisionFunction());
        builtIns.put("%", this.moduloFunction());
        builtIns.put("=", this.eqFunction());
        builtIns.put("<", this.lessThanFunction());
        builtIns.put("<=", this.lessEqualThanFunction());
        builtIns.put(">", this.greaterThanFunction());
        builtIns.put(">=", this.greaterEqualThanFunction());
        builtIns.put("not", this.notFunction());
        builtIns.put("and", this.andFunction());
        builtIns.put("or", this.orFunction());
        builtIns.put("print", this.printFunction());
        builtIns.put("format", this.formatFunction());
        builtIns.put("if", this.ifFunction());
        builtIns.put("setq", this.setqFunction());
        builtIns.put("defparameter", this.defparameterAndDefVarFunction());
        builtIns.put("defvar", this.defparameterAndDefVarFunction());
        builtIns.put("make-array", this.makeArrayFunction());
        builtIns.put("let", this.letAndLetAsterixFunction());
        builtIns.put("let*", this.letAndLetAsterixFunction());
        builtIns.put("random", this.randomFunction());
        builtIns.put("dotimes", this.dotimesFunction());
        builtIns.put("setf", this.setfFunction());
        builtIns.put("aref", this.arefFunction());

        builtIns.put("defun", this.defunFunction());
        return this;
    }

    public void initGlobals(Context global) {
        global.put("pi", new TokenValue(Token.NUMBER_DOUBLE, Math.PI));
        global.put("e", new TokenValue(Token.NUMBER_DOUBLE, Math.E));
    }

}
