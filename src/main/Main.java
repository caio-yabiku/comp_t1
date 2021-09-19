package main;

import ast.Program;

public class Main {
    public static void main( String []args ) {
        char []input = "var Int s; s = 0; println s; s = s + 1; print s;".toCharArray();

        Compiler compiler = new Compiler();

        Program program = compiler.compile(input);
        program.genC();
    }
}
