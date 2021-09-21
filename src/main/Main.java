package main;

import ast.PW;
import ast.Program;

import java.io.PrintWriter;

public class Main {
    public static void main( String []args ) {
        //char []input = "var Int s; var Int i; s = 0; for j in 1..100 { s = s + j; } println s; if (s || 1) + ! 5 { s = s + 23; print i; } else { s = 0; } while (i < 5) { i = i + 1; for k in 1..50 { s = s + k; } }".toCharArray();
        char []input = "var Int i; var Int soma; var Int somaFor; var Int n; var Int verd; n = 100; soma = 0; i = 0; verd = 2; while i < n && !!verd { if i%2 == (0+0*0/1) { soma = (--soma) + i*i; } i = i + 1; } somaFor = 0; for k in 0..100 { if i%2 == 0 { somaFor = somaFor + k*k; } } println soma; println somaFor;".toCharArray();

        Compiler compiler = new Compiler();

        Program program = compiler.compile(input);

        PrintWriter printWriter = new PrintWriter(System.out, true);
        PW pw = new PW();
        pw.set(printWriter);

        program.genC(pw);

        program.run();
    }
}
