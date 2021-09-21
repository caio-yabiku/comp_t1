package ast;

public class NumberExpr extends Expr {
    private int value;

    public NumberExpr(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void genC(PW pw) {
        pw.out.print(value);
    }

    @Override
    public int eval() {
        return this.value;
    }
}
