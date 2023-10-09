package lisp.parser;

public enum Token {

    LPAREN(false),
    RPAREN(false),

    S_EXPRESSION(false),

    QUOTE(false), // x=42; print 'x == 'x' and not 42

    SYMBOL(true), // name
    KEYWORD(true), // :key
    NUMBER(true), // 234234 23423.03
    STRING(true), // "___" '___'
    PACKAGE(true), // abc::def

    BUILTIN(true),
    NUMBER_DOUBLE(true),
    NUMBER_INTEGER(true),

    COMMA(false), // For the comma character (,)
    DOT(false), // For the dot character (.)
    EOF(false);

    private final boolean isAtom;

    Token(boolean isAtom) {
        this.isAtom = isAtom;
    }

    public boolean isAtom() {
        return isAtom;
    }

}
