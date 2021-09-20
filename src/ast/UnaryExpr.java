package ast;

import lexer.Symbol;

public class UnaryExpr extends Expr {
    private Expr expr;
    private Symbol op;

    public UnaryExpr(Expr e, Symbol op) {
        this.expr = e;
        this.op = op;
    }

    @Override
    public void genC(PW pw) {
        pw.out.print(op.toString());
        expr.genC(pw);
    }
}
