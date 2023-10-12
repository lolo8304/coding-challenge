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

    private ILispBuiltInFunction multFunction() {
        return new ILispBuiltInFunction() {
            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                    List<? extends ILispFunction> pars) {
                var sum = 1.0;
                for (ILispFunction token : pars) {
                    sum *= (Double) token.getDouble();
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
                    var sum = pars.get(0).getDouble();
                    for (int i = 1; i < pars.size(); i++) {
                        sum -= (Double) pars.get(i).getDouble();
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
                    var sum = pars.get(0).getDouble();
                    for (int i = 1; i < pars.size(); i++) {
                        sum /= (Double) pars.get(i).getDouble();
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
                    var sum = pars.get(0).getDouble();
                    for (int i = 1; i < pars.size(); i++) {
                        sum %= (Double) pars.get(i).getDouble();
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
                    var val = pars.get(0).getDouble();
                    for (int i = 1; i < pars.size(); i++) {
                        var compare = (Double) pars.get(i).getDouble();
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
                    var val = pars.get(0).getDouble();
                    for (int i = 1; i < pars.size(); i++) {
                        var compare = (Double) pars.get(i).getDouble();
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

    private ILispBuiltInFunction greaterThanFunction() {
        return new ILispBuiltInFunction() {
            @Override
            public ILispFunction apply(LispRuntime runtime, ILispFunction expr, String symbol,
                    List<? extends ILispFunction> pars) {
                if (pars.size() > 1) {
                    var val = pars.get(0).getDouble();
                    for (int i = 1; i < pars.size(); i++) {
                        var compare = (Double) pars.get(i).getDouble();
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
        builtIns.put("*", this.multFunction());
        builtIns.put("-", this.substractFunction());
        builtIns.put("/", this.divisionFunction());
        builtIns.put("%", this.moduloFunction());
        builtIns.put("=", this.eqFunction());
        builtIns.put("<", this.lessThanFunction());
        builtIns.put(">", this.greaterThanFunction());
        builtIns.put("not", this.notFunction());
        builtIns.put("and", this.andFunction());
        builtIns.put("or", this.orFunction());
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
