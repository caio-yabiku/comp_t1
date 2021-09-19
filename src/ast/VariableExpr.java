package ast;

public class VariableExpr extends Expr {
    private Variable v;

    public VariableExpr(Variable v) {
        super();
        this.v = v;
    }

    public void genC() {
        System.out.print(v.getName());
    }
}
