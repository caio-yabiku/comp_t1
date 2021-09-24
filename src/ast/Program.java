package ast;

import java.util.ArrayList;

public class Program {
    private ArrayList<Variable> arrayVariable;
    private StatList statList;

    public Program(ArrayList<Variable> arrayVariable, StatList statList) {
        this.arrayVariable = arrayVariable;
        this.statList = statList;
    }

    public void genC(PW pw) {
        pw.out.println("#include <stdio.h>");
        pw.out.println();
        pw.println("void main() {");

        pw.add();
        for(Variable v: arrayVariable) {
            v.genC(pw);
        }

        statList.genC(pw);

        pw.sub();

        pw.out.println("}");
    }

    public int run() {
        statList.eval();

        return 0;
    }
}
