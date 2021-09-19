package ast;

import lexer.Symbol;

public class CompositeExpr extends Expr {
    private Expr left;
    private Expr right;
    private Symbol op;

    public CompositeExpr(Expr left, Symbol op, Expr right) {
        this.left = left;
        this.right = right;
        this.op = op;
    }

    public void genC() {
        System.out.print("(");
        left.genC();
        System.out.print(" " + op.toString() + " ");
        right.genC();
        System.out.print(")");
    }
}
