package main;

import ast.PW;
import ast.Program;

import java.io.PrintWriter;

public class Main {
    public static void main( String []args ) {
        char []input = "var Int s; var Int i; s = 0; for j in 1..100 { s = s + j; } println s; if ((s || 1) + ! 5) { s = s + 23; print i; } else { s = 0; } while (i < 5) { i = i + 1; for k in 1..50 { s = s + k; } }".toCharArray();

        Compiler compiler = new Compiler();

        Program program = compiler.compile(input);

        PrintWriter printWriter = new PrintWriter(System.out, true);
        PW pw = new PW();
        pw.set(printWriter);

        program.genC(pw);
    }
}
