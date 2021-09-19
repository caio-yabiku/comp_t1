package ast;

public class NumberExpr extends Expr {
    private int value;

    public NumberExpr(int value) {
        super();
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void genC() {
        System.out.print(value);
    }
}
