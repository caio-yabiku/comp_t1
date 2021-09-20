package main;

import ast.PW;
import ast.Program;

import java.io.PrintWriter;

public class Main {
    public static void main( String []args ) {
        char []input = "var Int s; s = 0; println s; s = s + 1; print s;".toCharArray();

        Compiler compiler = new Compiler();

        Program program = compiler.compile(input);

        PrintWriter printWriter = new PrintWriter(System.out, true);
        PW pw = new PW();
        pw.set(printWriter);

        program.genC(pw);
    }
}
