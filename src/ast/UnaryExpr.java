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
        pw.out.print(op.toString() + " ");
        expr.genC(pw);
    }

    @Override
    public int eval() {
        if(op == Symbol.PLUS)
            return + expr.eval();
        else if(op == Symbol.MINUS)
            return - expr.eval();
        else
            if(expr.eval() == 0)
                return 1;
            else
                return 0;
    }
}
