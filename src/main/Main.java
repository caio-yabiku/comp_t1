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
            System.out.println("Usage:\n   Main (flag -gen or -run) input output");
            System.out.println("First flag tells the program if it must generate C code (-gen) or interpret it (-run)");
            System.out.println("input is the file to be compiled");
            System.out.println("output is the file where the generated code will be stored");
        } else {
            file = new File(args[1]);
            if (!file.exists() || !file.canRead()) {
                System.out.println("Either the file " + args[1] + " does not exist or it cannot be read");
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
                System.out.println("Error reading file " + args[1]);
                throw new RuntimeException();
            }

            if (numChRead != file.length()) {
                System.out.println("Read error");
                throw new RuntimeException();
            }
            try {
                stream.close();
            } catch (IOException e) {
                System.out.println("Error in handling the file " + args[1]);
                throw new RuntimeException();
            }


            Compiler compiler = new Compiler();
            PrintWriter printWriter = new PrintWriter(System.out, true);
            program = null;

            try {
                program = compiler.compile(input, printWriter);
            } catch (RuntimeException e) {
                System.out.println(e);
            }
            if (program != null) {
                PW pw = new PW();
                pw.set(printWriter);
                if (args[0].equals("-gen")) {
                	program.genC(pw);
                } else if (args[0].equals("-run")){
                	program.run();
                } else {
                	System.out.println("Invalid flag (must pass -gen or -run as first argument)");
                }
                if (printWriter.checkError()) {
                    System.out.println("There was an error in the output");
                }
            }
        }
    }
}
