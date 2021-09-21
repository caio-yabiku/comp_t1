package ast;

public class ForStat extends Stat {
    private Variable v;
    private Expr startExpr;
    private Expr endExpr;
    private StatList statList;

    public ForStat(Variable v, Expr startExpr, Expr endExpr, StatList statList) {
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

    @Override
    public void eval() {
        int startVal = startExpr.eval();
        int endVal = endExpr.eval();

        if(startVal > endVal)
            throw new RuntimeException("end expression should be >= than start expression");

        v.setValue(startVal);
        while(v.getValue() <= endVal) {
            statList.eval();
            v.setValue(v.getValue() + 1);
        }
    }
}
