package ast;

public class VariableExpr extends Expr {
    private Variable v;

    public VariableExpr(Variable v) {
        this.v = v;
    }

    @Override
    public void genC(PW pw) {
        pw.out.print(v.getName());
    }

    @Override
    public int eval() {
        return v.getValue();
    }
}
