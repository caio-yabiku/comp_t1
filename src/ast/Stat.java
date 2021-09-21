package ast;

abstract public class Stat {
    abstract public void genC(PW pw);

    abstract public void eval();
}
