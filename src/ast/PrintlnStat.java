package ast;

public class PrintlnStat extends Stat {
    private Expr expr;

    public PrintlnStat(Expr e) {
        this.expr = e;
    }

    @Override
    public void genC(PW pw) {
        pw.print("printf(\"%d\\n\", ");
        expr.genC(pw);
        pw.out.println(");");
    }

    @Override
    public void eval() {
        System.out.println(expr.eval());
    }
}
