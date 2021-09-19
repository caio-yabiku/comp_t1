package lexer;

public enum Symbol {
    EOF("eof"),
    IDENT("Ident"),
    NUMBER("Number"),
    PLUS("+"),
    MINUS("-"),
    MULT("*"),
    DIV("/"),
    LT("<"),
    LE("<="),
    GT(">"),
    GE(">="),
    NEQ("!="),
    EQ("=="),
    ASSIGN("="),
    LEFTPAR("("),
    RIGHTPAR(")"),
    SEMICOLON(";"),
    VAR("var"),
    IF("if"),
    THEN("then"),
    ELSE("else"),
    ENDIF("endif"),
    FOR("for"),
    IN("in"),
    DOTS(".."),
    WHILE("while"),
    PRINT("print"),
    PRINTLN("println"),
    COMMA(","),
    COLON(":"),
    INTEGER("Int"),
    CHARACTER("character"),
    TRUE("true"),
    FALSE("false"),
    OR   ("||"),
    AND  ("&&"),
    REMAINDER("%"),
    NOT("!");

    Symbol(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    private String name;
}