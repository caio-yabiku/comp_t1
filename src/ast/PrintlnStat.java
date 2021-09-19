package ast;

public class PrintlnStat extends Stat {
    private Expr expr;

    public PrintlnStat(Expr e) {
        super();
        this.expr = e;
    }

    @Override
    public void genC() {
        expr.genC();
        System.out.println(";");
    }
}
