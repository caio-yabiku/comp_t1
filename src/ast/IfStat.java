package ast;

public class IfStat extends Stat {
    private Expr expr;
    private StatList thenPart;
    private StatList elsePart;

    public IfStat(Expr e, StatList thenPart, StatList elsePart) {
        this.expr = e;
        this.thenPart = thenPart;
        this.elsePart = elsePart;
    }

    @Override
    public void genC(PW pw) {
        pw.print("if ");
        expr.genC(pw);
        pw.out.println(" {");

        pw.add();
        thenPart.genC(pw);
        pw.sub();

        pw.println("}");

        if(elsePart != null) {
            pw.println("else {");

            pw.add();
            elsePart.genC(pw);
            pw.sub();

            pw.println("}");
        }
    }

    @Override
    public void eval() {
        int val = expr.eval();

        if(val != 0)
            thenPart.eval();
        else if(elsePart != null)
            elsePart.eval();
    }
}
