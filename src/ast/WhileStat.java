package ast;

public class WhileStat extends Stat {
    private Expr expr;
    private StatList statList;

    public WhileStat(Expr e, StatList statList) {
        this.expr = e;
        this.statList = statList;
    }

    @Override
    public void genC(PW pw) {
        pw.print("while ");
        expr.genC(pw);
        pw.out.println(" {");

        pw.add();
        statList.genC(pw);
        pw.sub();

        pw.println("}");
    }

    @Override
    public void eval() {
        int val = expr.eval();

        while(val != 0) {
            statList.eval();

            val = expr.eval();
        }
    }
}
