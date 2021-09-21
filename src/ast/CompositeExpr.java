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

    @Override
    public void genC(PW pw) {
        pw.out.print("(");
        left.genC(pw);
        pw.out.print(" " + op.toString() + " ");
        right.genC(pw);
        pw.out.print(")");
    }

    @Override
    public int eval() {
        int val;

        switch(op) {
            case PLUS:
                val = left.eval() + right.eval();
                break;
            case MINUS:
                val = left.eval() - right.eval();
                break;
            case MULT:
                val = left.eval() * right.eval();
                break;
            case DIV:
                val = left.eval() / right.eval();
                break;
            case REMAINDER:
                val = left.eval() % right.eval();
                break;
            case LT:
                val = (left.eval() < right.eval()) ? 1 : 0;
                break;
            case LE:
                val = (left.eval() <= right.eval()) ? 1 : 0;
                break;
            case GT:
                val = (left.eval() > right.eval()) ? 1 : 0;
                break;
            case GE:
                val = (left.eval() >= right.eval()) ? 1 : 0;
                break;
            case EQ:
                val = (left.eval() == right.eval()) ? 1 : 0;
                break;
            case NEQ:
                val = (left.eval() != right.eval()) ? 1 : 0;
                break;
            case OR:
                if(left.eval() != 0 || right.eval() != 0)
                    val = 1;
                else
                    val = 0;
                break;
            case AND:
                if(left.eval() != 0 && right.eval() != 0)
                    val = 1;
                else
                    val = 0;
                break;
            default:
                val = 0;
                break;
        }

        return val;
    }
}
