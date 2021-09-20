package ast;

public class ForStat extends Stat {
    private Variable v;
    private Expr startExpr;
    private Expr endExpr;
    private StatList statList;

    public ForStat(Variable v, Expr startExpr, Expr endExpr, StatList statList) {
        super();
        this.v = v;
        this.startExpr = startExpr;
        this.endExpr = endExpr;
        this.statList = statList;
    }

    @Override
    public void genC(PW pw) {
        pw.print("for(int " + v.getName() + " = ");
        startExpr.genC(pw);
        pw.out.print("; " + v.getName() + " <= ");
        endExpr.genC(pw);
        pw.out.println("; " + v.getName() + "++) {");

        pw.add();
        statList.genC(pw);
        pw.sub();

        pw.println("}");
    }
}
