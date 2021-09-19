package ast;

public class AssignStat extends Stat {
    private Variable v;
    private Expr expr;

    public AssignStat(Variable v, Expr e) {
        super();
        this.v = v;
        this.expr = e;
    }

    @Override
    public void genC() {
        System.out.print(v.getName() + " = ");
        expr.genC();
        System.out.println(";");
    }
}
