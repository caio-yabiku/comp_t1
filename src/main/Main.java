package main;

import ast.PW;
import ast.Program;

import java.io.*;

public class Main {
    public static void main( String []args ) {

        File file;
        FileReader stream;
        int numChRead;
        Program program;

        if (args.length != 2) {
            System.out.println("Usage:\n   Main input output");
            System.out.println("input is the file to be compiled");
            System.out.println("output is the file where the generated code will be stored");
        } else {
            file = new File(args[0]);
            if (!file.exists() || !file.canRead()) {
                System.out.println("Either the file " + args[0] + " does not exist or it cannot be read");
                throw new RuntimeException();
            }
            try {
                stream = new FileReader(file);
            } catch (FileNotFoundException e) {
                System.out.println("Something wrong: file does not exist anymore");
                throw new RuntimeException();
            }
            // one more character for '\0' at the end that will be added by the
            // compiler
            char[] input = new char[(int) file.length() + 1];

            try {
                numChRead = stream.read(input, 0, (int) file.length());
            } catch (IOException e) {
                System.out.println("Error reading file " + args[0]);
                throw new RuntimeException();
            }

            if (numChRead != file.length()) {
                System.out.println("Read error");
                throw new RuntimeException();
            }
            try {
                stream.close();
            } catch (IOException e) {
                System.out.println("Error in handling the file " + args[0]);
                throw new RuntimeException();
            }


            Compiler compiler = new Compiler();
            FileOutputStream outputStream;
            try {
                outputStream = new FileOutputStream(args[1]);
            } catch (IOException e) {
                System.out.println("File " + args[1] + " could not be opened for writing");
                throw new RuntimeException();
            }
            PrintWriter printWriter = new PrintWriter(outputStream);
            program = null;
            // the generated code goes to a file and so are the errors
            try {
                program = compiler.compile(input, printWriter);
            } catch (RuntimeException e) {
                System.out.println(e);
            }
            if (program != null) {
                PW pw = new PW();
                pw.set(printWriter);
                program.genC(pw);
                program.run();
                if (printWriter.checkError()) {
                    System.out.println("There was an error in the output");
                }
            }
        }
    }

    /*
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
    }*/
}
