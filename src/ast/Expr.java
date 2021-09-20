package ast;

import java.util.Map;

abstract public class Expr {
    abstract public void genC(PW pw);

    public void eval() {}
}
