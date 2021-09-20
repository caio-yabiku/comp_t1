package ast;

public class PrintStat extends Stat {
    private Expr expr;

    public PrintStat(Expr e) {
        this.expr = e;
    }

    @Override
    public void genC(PW pw) {
        pw.print("printf(\"%d\", ");
        expr.genC(pw);
        pw.out.println(");");
    }
}
