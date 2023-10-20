package lisp.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

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
                    var sum = 0.0;
                    if (pars.size() == 1) {
                        sum = -(Double) pars.get(0).apply(runtime).getDouble();
                    } else {
                        sum = pars.get(0).apply(runtime).getDouble();
                        for (int i = 1; i < pars.size(); i++) {
                            sum -= pars.get(i).apply(runtime).getDouble();
                        }
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
                            return TokenValue.NIL;
                        }
                    }
                    return new TokenValue(Token.T, 1.0);
                } else {
                    return TokenValue.NIL;
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
                            return TokenValue.NIL;
                        }
                    }
                    return new TokenValue(Token.T, 1.0);
                } else {
                    return TokenValue.NIL;
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
                            return TokenValue.NIL;
                        }
                    }
                    return new TokenValue(Token.T, 1.0);
                } else {
                    return TokenValue.NIL;
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
                            return TokenValue.NIL;
                        }
                    }
                    return new TokenValue(Token.T, 1.0);
                } else {
                    return TokenValue.NIL;
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
                            return TokenValue.NIL;
                        }
                    }
                    return new TokenValue(Token.T, 1.0);
                } else {
                    return TokenValue.NIL;
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
                        return TokenValue.NIL;
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
                            return TokenValue.NIL;
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
                    return TokenValue.NIL;

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

                return TokenValue.NIL;
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
                return TokenValue.NIL;
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
                    ILispFunction elseCondition = TokenValue.NIL;
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
                    return TokenValue.NIL;
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
                            return TokenValue.NIL;
                        }
                    }
                    // defparameter or not existing value
                    runtime.tos().putGlobal(parName, initValue);
                    return TokenValue.NIL;
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
                return initialContent != null ?
                        this.makeArray(runtime, dimensions, null, initialContent.apply(runtime))
                        :
                        this.makeArray(runtime, dimensions, initialElement, null);
            }

            private ILispFunction makeArray(LispRuntime runtime, ILispFunction dimensions, ILispFunction initialElement, ILispFunction initialContent) {
                if (dimensions.getToken() == Token.NUMBER_INTEGER || (dimensions.getToken() == Token.QUOTE && dimensions.getUnary().getToken() == Token.NUMBER_INTEGER)) {
                    // 1 dimensional vector
                    var size = dimensions.apply(runtime).getInteger();
                    return this.makeArray(runtime, new int[]{size}, initialElement, initialContent);
                }
                ILispFunction dimensionToken;
                if (dimensions.getToken() == Token.S_EXPRESSION) {
                    if (dimensions.getExpression().get(0).getValue().equals("list")) {
                        dimensionToken = dimensions.apply(runtime);
                    } else {
                        dimensionToken = dimensions;
                    }
                } else if (dimensions.getToken() == Token.QUOTE && dimensions.getUnary().getToken() == Token.S_EXPRESSION) {
                    dimensionToken = dimensions.apply(runtime);
                } else {
                    throw new IllegalArgumentException("Invalid multi dimensions '(1 2) ");
                }
                var xD = dimensionToken.getExpression();
                var dims = new int[xD.size()];
                for (int i = 0; i < xD.size(); i++) {
                    dims[i] = xD.get(i).getInteger();
                }
                return this.makeArray(runtime, dims, initialElement, initialContent);
            }


            private ILispFunction makeArray(LispRuntime runtime, int[] size, ILispFunction initialElement, ILispFunction initialContent) {
                var initialValue = initialElement != null ? initialElement.apply(runtime) : null;
                var initialContentValue = initialContent != null ? initialContent.apply(runtime) : null;
                if (initialValue == null && initialContentValue == null) {
                    initialValue = TokenValue.NIL;
                }
                final var initialValue2 = initialValue;
                if (initialValue2 != null) {
                    var initializer = new TensorInitializer() {
                        @Override
                        public TokenValue get() {
                            return (TokenValue)initialValue2;
                        }
                    };
                    var tensor = new Tensor(size, initializer);
                    return new TokenValue(tensor);
                }
                var tensor = new Tensor(size, (TokenValue) initialContent);
                return new TokenValue(tensor);
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
                            return new TokenValue(NUMBER_DOUBLE, RANDOM.nextDouble() * Math.abs(max.getDouble()));
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
                    runtime.pushScope("let");
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
                        ILispFunction lastResult = TokenValue.NIL;
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
                if (pars.size() >= 2) {
                    runtime.pushScope("dotimes");
                    try {
                        // (dotimes (var count &optional result-form) body)
                        var counter = pars.get(0);
                        var variable = counter.getExpression().get(0).getValue();
                        var count = counter.getExpression().get(1).apply(runtime);

                        var countInt = count.getInteger();
                        for (int i = 0; i < countInt; i++) {
                            runtime.tos().put(variable, new TokenValue(i));
                            for (int j = 1; j < pars.size(); j++) {
                                pars.get(j).apply(runtime);
                            }
                        }
                        ILispFunction result = TokenValue.NIL;
                        if (counter.getExpression().size() == 3) {
                            // include result form
                            result = counter.getExpression().get(2).apply(runtime);
                        }
                        return result;
                    } finally {
                        runtime.popScope();
                    }
                } else {
                    throw new IllegalArgumentException("dotimes must have >=2 parameters: (dotimes (var count &optional result-form) body body body ...)");
                }
            }
        };
    }


    // (loop repeat count collect|append body)
    //
    private ILispBuiltInFunction loopFunction() {
        return new ILispBuiltInFunction() {

            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                                       List<? extends ILispFunction> pars) {
                if (pars.size() >= 4) {
                    runtime.pushScope("loop");
                    try {
                        var type = pars.get(0).getValue();
                        switch (type) {
                            case "repeat":
                                var count = pars.get(1).apply(runtime).getInteger();
                                var accumulator = pars.get(2).getValue();
                                var body = pars.get(3);
                                if (accumulator.equals("collect")) {
                                    var values = new ArrayList<ILispFunction>();
                                    for (int i = 0; i < count; i++) {
                                        var newValue = body.apply(runtime);
                                        values.add(newValue);
                                    }
                                    return new TokenValue(S_EXPRESSION, values);
                                } else {
                                    throw new IllegalArgumentException("loop - different accumulator than defined: "+accumulator);
                                }
                            // loop for i from 0 below 5 do
                            case "for":
                                var variable = pars.get(1);
                                var variableName = variable.getValue();
                                // from
                                var lower = pars.get(3).apply(runtime);
                                runtime.tos().putLocal(variableName, lower);
                                // below
                                var upperExclude = pars.get(5).apply(runtime);
                                var doIndex = 6;
                                for (int i = lower.getInteger(); i < upperExclude.getInteger(); i++) {
                                    runtime.tos().putLocal(variableName, new TokenValue(NUMBER_INTEGER, i));
                                    // do
                                    doIndex = 6;
                                    while (doIndex < pars.size() && pars.get(doIndex).getValue().equals("do")) {
                                        doIndex++;
                                        pars.get(doIndex).apply(runtime);
                                        doIndex++;
                                    }
                                }
                                // finally
                                var finallyIndex = doIndex;
                                while (finallyIndex < pars.size() && pars.get(finallyIndex).getValue().equals("finally")) {
                                    finallyIndex++;
                                    pars.get(finallyIndex).apply(runtime);
                                    finallyIndex++;
                                }
                                return TokenValue.NIL;
                            default:
                                throw new IllegalArgumentException("loop - different type than defined: "+expr);
                        }
                    } finally {
                        runtime.popScope();
                    }
                } else {
                    throw new IllegalArgumentException("loop must have 3 parameters: (loop repeat count body)");
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
                if (pars.size() >= 2 && pars.size() % 2 == 0) {
                    for (int i = 0; i < pars.size(); i++) {
                        var variable = pars.get(i++);
                        if (variable.getToken() == SYMBOL) {
                            var exprOrVariable = pars.get(i).apply(runtime);
                            runtime.tos().put(variable.getValue(), exprOrVariable);
                        } else if (variable.getToken() == S_EXPRESSION && variable.getExpression().get(0).getValue().equals("aref")) {
                            // (setf (aref my-array 1) 100)
                            // do not run (aref ...) just use definitions to access array and adapt it
                            var arrayVariableName = variable.getExpression().get(1).getValue();
                            var array = runtime.tos().get(arrayVariableName);
                            // after 2 there could be multiple indices
                            var indices = new int[variable.getExpression().size()-2];
                            for (int j = 2; j < variable.getExpression().size(); j++) {
                                indices[j-2] = variable.getExpression().get(j).apply(runtime).getInteger();
                            }
                            // check if (apply #'aref var) is now there
                            var newValueSlot = pars.get(i);
                            ILispFunction newValue = TokenValue.NIL;
                            if (newValueSlot.getToken() == S_EXPRESSION && newValueSlot.get(0).getValue().equals("apply") && newValueSlot.get(1).getValue().equals("#'aref")) {
                                var newValueArray = newValueSlot.get(2).apply(runtime); // resolve variable
                                var fromValueArrayDims = newValueArray.dimensions();
                                var toValueArrayDims = array.dimensions();
                                if (fromValueArrayDims.length < toValueArrayDims.length) {
                                    var newIndices = new int[fromValueArrayDims.length];
                                    System.arraycopy(indices, 0, newIndices, 0, fromValueArrayDims.length);
                                    newValue = newValueArray.get(newIndices);
                                } else {
                                    newValue = newValueArray.get(indices);
                                }
                            } else {
                                newValue = pars.get(i).apply(runtime);
                            }
                            array.set(indices, newValue);
                        } else {
                            throw new IllegalArgumentException("setf type is not supported yet - " + expr.toString());
                        }
                    }
                    return TokenValue.NIL;
                } else {
                    throw new IllegalArgumentException("setf must have >2 and even arguments : setf place value place value ...");
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

                    var indices = new int[pars.size()-1];
                    for (int i = 1; i < pars.size(); i++) {
                        var index = pars.get(i).apply(runtime).getInteger();
                        indices[i-1] = index;
                    }
                    if (array.getToken() != S_EXPRESSION) {
                        throw new IllegalArgumentException("Array referenced is not an array ("+array.getToken()+") - "+array.toString());
                    }
                    return array.get(indices);
                } else {
                    throw new IllegalArgumentException("aref must have > 2 parameters: aref var index1 index2 ... - "+expr);
                }
            }
        };
    }

    // (aref my-array 10)
    private ILispBuiltInFunction expFunction() {
        return new ILispBuiltInFunction() {

            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                                       List<? extends ILispFunction> pars) {
                if (pars.size() == 1) {
                    var power = pars.get(0).apply(runtime).getDouble();
                    return new TokenValue(Math.exp(power));
                } else {
                    throw new IllegalArgumentException("exp must have 1 parameter: (exp power) - " +expr);
                }
            }
        };
    }

    private ILispBuiltInFunction listFunction() {
        return new ILispBuiltInFunction() {

            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                                       List<? extends ILispFunction> pars) {
                var list = new ArrayList<TokenValue>();
                for (int i = 0; i < pars.size(); i++) {
                    list.add((TokenValue)pars.get(i).apply(runtime));
                }
                return new TokenValue(S_EXPRESSION, list);
            }
        };
    }


    // (destructuring-bind pattern source-form
    //  body)
    // e.g. (destructuring-bind (input-layer hidden-layer output-layer output) network
    //       body ...)
    private ILispBuiltInFunction destructuringBindFunction() {
        return new ILispBuiltInFunction() {

            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                                       List<? extends ILispFunction> pars) {
                if (pars.size() >= 3) {
                    runtime.pushScope("destructuringBind");
                    try {
                        var patterns = pars.get(0); // contains list of variable names - found in
                        var sourceForm = pars.get(1).apply(runtime); // variable resolved as list of paramters
                        for (int i = 0; i < patterns.getExpression().size(); i++) {
                            var patternString = patterns.getExpression().get(i).getValue();
                            var source = sourceForm.get(i);
                            runtime.tos().putLocal(patternString, source);
                        }
                        ILispFunction result = TokenValue.NIL;
                        for (int i = 2; i < pars.size(); i++) {
                            result = pars.get(i).apply(runtime);
                        }
                        return result;
                    } finally {
                        runtime.popScope();
                    }
                } else {
                    throw new IllegalArgumentException("destructuring-bind must have >= 3 parameter: (destructuring-bind pattern source-form body...) - " +expr);
                }
            }
        };
    }

    private ILispBuiltInFunction arrayDimensionBindFunction() {
        return new ILispBuiltInFunction() {

            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                                       List<? extends ILispFunction> pars) {
                if (pars.size() == 2) {
                    var array = pars.get(0).apply(runtime);
                    var index = pars.get(1).apply(runtime).getInteger();
                    if (index < array.dimensions().length) {
                        return new TokenValue(Token.NUMBER_INTEGER, array.dimensions()[index]);
                    } else {
                        // hack to support 20 x 1 = 20
                        return new TokenValue(Token.NUMBER_INTEGER, 1);
                    }
                } else {
                    throw new IllegalArgumentException("array-dimension must have 2 parameter: (array-dimension array dimension) - " +expr);
                }
            }
        };
    }



    private ILispBuiltInFunction loadFunction() {
        return new ILispBuiltInFunction() {

            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                                       List<? extends ILispFunction> pars) {
                if (pars.size() >= 1) {
                    //System.out.println("Pars "+pars.toString());
                    for (int i = 0; i < pars.size(); i++) {
                        var fileName = pars.get(0).getValue();
                        System.out.println("Load from file "+fileName);
                        try (var reader = new FileReader(fileName)) {
                            runtime.execute(reader);
                        } catch (FileNotFoundException e) {
                            System.out.println("File "+fileName+" does not exists");
                        } catch (IOException e) {
                            System.out.println("Error while reading file: "+e.getMessage());
                        }
                    }
                    return TokenValue.NIL;
                } else {
                    throw new IllegalArgumentException("load at least 1 parameter: (load file file file...) - " +expr);
                }
            }
        };
    }


    private ILispBuiltInFunction pureFunction() {
        return new ILispBuiltInFunction() {

            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                                       List<? extends ILispFunction> pars) {
                if (pars.size() >= 1) {
                    for (int i = 0; i < pars.size(); i++) {
                        runtime.setPureFunction(pars.get(i).getValue());
                    }
                    return TokenValue.NIL;
                } else {
                    throw new IllegalArgumentException("pure at least 1 parameter: (pure funcname funcname funcname ...) - " +expr);
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
                if (pars.size() >= 3) {
                    var funcName = pars.get(0).getValue();
                    var vars = pars.get(1);
                    ILispFunction docu = new TokenValue(Token.STRING, "");
                    var funcStartIndex = 2;
                    var func = pars.get(2);
                    if (func.getToken() == Token.STRING) {
                        docu = func;
                        func = pars.get(3);
                        funcStartIndex = 3;
                    }
                    var funcs = new ILispFunction[pars.size() - funcStartIndex];
                    for (int i = funcStartIndex; i < pars.size(); i++) {
                        funcs[i-funcStartIndex] = pars.get(i);
                    }
                    runtime.addCustom(funcName, new DefunBuiltIn(funcName, vars, docu, funcs));
                    return new TokenValue(Token.SYMBOL, symbol.toUpperCase());
                } else {
                    throw new IllegalArgumentException("defun must have >=3 parameters: func name, vars-expr, [docustring], func-expr func-expre func-exp - "+expr);
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
        builtIns.put("loop", this.loopFunction());
        builtIns.put("exp", this.expFunction());
        builtIns.put("list", this.listFunction());
        builtIns.put("destructuring-bind", this.destructuringBindFunction());
        builtIns.put("array-dimension", this.arrayDimensionBindFunction());
        builtIns.put("load", this.loadFunction());
        builtIns.put("pure", this.pureFunction());


        builtIns.put("defun", this.defunFunction());
        return this;
    }

    public void initGlobals(Context global) {
        global.put("pi", new TokenValue(Token.NUMBER_DOUBLE, Math.PI));
        global.put("e", new TokenValue(Token.NUMBER_DOUBLE, Math.E));
    }

}
