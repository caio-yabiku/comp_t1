package ast;

import java.util.Map;

public class AssignStat extends Stat {
    private Variable v;
    private Expr expr;

    public AssignStat(Variable v, Expr e) {
        super();
        this.v = v;
        this.expr = e;
    }

    @Override
    public void genC(PW pw) {
        pw.print(v.getName() + " = ");
        expr.genC(pw);
        pw.out.println(";");
    }

    //@Override
    public void eval() {
    }
}
