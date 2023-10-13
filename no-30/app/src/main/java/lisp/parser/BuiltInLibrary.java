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
                            return new TokenValue(Token.NIL, 0.0);
                        }
                    }
                    return new TokenValue(Token.T, 1.0);
                } else {
                    return new TokenValue(Token.NIL, 0.0);
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
                            return new TokenValue(Token.NIL, 0.0);
                        }
                    }
                    return new TokenValue(Token.T, 1.0);
                } else {
                    return new TokenValue(Token.NIL, 0.0);
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
                            return new TokenValue(Token.NIL, 0.0);
                        }
                    }
                    return new TokenValue(Token.T, 1.0);
                } else {
                    return new TokenValue(Token.NIL, 0.0);
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
                            return new TokenValue(Token.NIL, 0.0);
                        }
                    }
                    return new TokenValue(Token.T, 1.0);
                } else {
                    return new TokenValue(Token.NIL, 0.0);
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
                            return new TokenValue(Token.NIL, 0.0);
                        }
                    }
                    return new TokenValue(Token.T, 1.0);
                } else {
                    return new TokenValue(Token.NIL, 0.0);
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
                    if (token == Token.NIL) {
                        return new TokenValue(Token.T, 1.0);
                    } else {
                        return new TokenValue(Token.NIL, 0.0);
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
                        if (token.equals(Token.NIL)) {
                            return new TokenValue(Token.NIL, 0.0);
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
                        if (!token.equals(Token.NIL)) {
                            return new TokenValue(Token.T, 1.0);
                        }
                    }
                    return new TokenValue(Token.NIL, 0.0);

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

                return new TokenValue(Token.NIL, 0.0);
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
                    var args = pars.subList(2, pars.size()).stream().map( (x) -> x.apply(runtime).getObject()).toArray();
                    if (output.equals("t")) {
                        if (args.length > 0) {
                            System.out.print(String.format(this.migrateTemplateToJavaFormat(template), args));
                        } else {
                            System.out.print(String.format(this.migrateTemplateToJavaFormat(template)));
                        }
                    } else {
                        throw new IllegalArgumentException("format: only output format <t> is implemented");
                    }
                } else {
                    throw new IllegalArgumentException("format expression must contain <stream> (.e.g t), a string and optional parameters");
                }
                return new TokenValue(Token.NIL, 0.0);
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
                    ILispFunction elseCondition = new TokenValue(Token.NIL, 0.0);
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
                if (pars.size() == 2) {
                    var variable = pars.get(0).getValue();
                    var exprOrVariable = pars.get(1).apply(runtime);
                    runtime.tos().put(variable, exprOrVariable);
                    return new TokenValue(Token.NIL, 0.0);
                } else {
                    throw new IllegalArgumentException("set1 must have 2 parameters: setq <symbol> <expression of value>");
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
                if (pars.size() == 3) {
                    var funcName = pars.get(0).getValue();
                    var vars = pars.get(1);
                    var func = pars.get(2);
                    runtime.addCustom(symbol, new DefunBuiltIn(pars, funcName, vars, func));
                    return expr;
                } else {
                    throw nw IllegalArumwntException("defun must have 3 parameters: func name, vars-expr, func-expr");
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

        builtIns.put("defun", this.defunFunction());
        return this;
    }

    public void initGlobals(Context global) {
        global.put("pi", new TokenValue(Token.NUMBER_DOUBLE, Math.PI));
        global.put("e", new TokenValue(Token.NUMBER_DOUBLE, Math.E));
    }

    public static class DefunBuiltIn implements ILispBuiltInFunction {

        private ILispFunction expr;
        private String symbol;
        private ILispFunction vars;
        private ILispFunction func;

        public DefunBuiltIn(ILispFunction expr, String symbol, ILispFunction vars, ILispFunction func) {
            this.expr = expr;
            this.symbol = symbol;
            this.vars = vars;
            this.func = func;
        }

        @Override
        public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                    List<? extends ILispFunction> pars) {

            // check count (vars) with count (pars)
            // set tos().newScope(symbol)
            // iterste over vars. set values from para with same index
            // 


            // TODO: needs evaluation along the expression tree
            throw new IllegalAccessError("Not implemented yet");
        }
    }
}
