package ast;

import lexer.Symbol;

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
}
