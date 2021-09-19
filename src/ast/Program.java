package ast;

import java.util.ArrayList;

public class Program {
    private ArrayList<Variable> arrayVariable;
    private StatList statList;

    public Program(ArrayList<Variable> arrayVariable, StatList statList) {
        this.arrayVariable = arrayVariable;
        this.statList = statList;
    }

    public void genC() {
        System.out.println("#include <stdio.h>");
        System.out.println();
        System.out.println("void main() {");

        for(Variable v: arrayVariable) {
            System.out.println("var Int " + v.getName() + ";");
        }

        statList.genC();

        System.out.println("}");

    }
}
