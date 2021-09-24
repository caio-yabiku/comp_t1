package main;

import ast.*;
import errorHandling.CompilerError;
import lexer.Lexer;
import lexer.Symbol;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

public class Compiler {
    private Hashtable<String, Variable> symbolTable;
    private CompilerError error;
    private Lexer lexer;

    public Program compile(char []p_input, PrintWriter pw) {
        symbolTable = new Hashtable<String, Variable>();
        error = new CompilerError(pw);
        lexer = new Lexer(p_input, error);
        error.setLexer(lexer);
        
        lexer.nextToken();
        return program();
    }

    /*
        Program ::= VarList { Stat }
     */
    private Program program() {
        ArrayList<Variable> arrayVariable = varDecList();
        StatList sl = statList();

        Program program = new Program(arrayVariable, sl);


        if(lexer.token != Symbol.EOF)
            error.signal("EOF expected");

        return program;
    }

    /*
        Stat ::= AssignStat | IfStat | ForStat | PrintStat |
                PrintlnStat | WhileStat
     */
    private StatList statList() {
        ArrayList<Stat> v = new ArrayList<Stat>();

        while(  lexer.token == Symbol.IDENT ||
                lexer.token == Symbol.IF ||
                lexer.token == Symbol.FOR||
                lexer.token == Symbol.PRINT ||
                lexer.token == Symbol.PRINTLN ||
                lexer.token == Symbol.WHILE) {
            v.add(stat());
        }

        return new StatList(v);
    }

    /*
        Stat ::= AssignStat | IfStat | ForStat | PrintStat |
                PrintlnStat | WhileStat
     */
    private Stat stat() {

        switch(lexer.token) {
            case IDENT:
                return assignmentStat();
            case IF:
                return ifStat();
            case FOR:
                return forStat();
            case PRINT:
                return printStat();
            case PRINTLN:
                return printlnStat();
            case WHILE:
                return whileStat();
            default:
                error.signal("Stat expected");
        }
        return null;
    }

    /*
        WhileStat ::= "while" Expr StatList
     */
    private Stat whileStat() {
        lexer.nextToken();

        Expr e = expr();

        if(lexer.token != Symbol.LEFTBRA)
            error.signal("{ expected");

        lexer.nextToken();
        StatList statList = statList();

        if(lexer.token != Symbol.RIGHTBRA)
            error.signal("} expected");

        lexer.nextToken();

        return new WhileStat(e, statList);
    }

    /*
        PrintlnStat ::= "println" Expr ";"
     */
    private Stat printlnStat() {
        lexer.nextToken();

        Expr e = expr();

        if(lexer.token != Symbol.SEMICOLON)
            error.signal("; expected");

        lexer.nextToken();

        return new PrintlnStat(e);
    }

    /*
        PrintStat ::= "print" Expr ";"
     */
    private Stat printStat() {
        lexer.nextToken();
        
        Expr e = expr();

        if(lexer.token != Symbol.SEMICOLON)
            error.signal("; expected");

        lexer.nextToken();
        
        return new PrintStat(e);
    }

    /*
        ForStat ::= "for" Id "in" Expr ".." Expr StatList
     */
    private Stat forStat() {
        lexer.nextToken();
        if(lexer.token != Symbol.IDENT)
            error.signal("Ident expected");
        String name = lexer.getStringValue();
        Variable v = symbolTable.get(name);
        if(v != null)
            error.signal("variable was already declared");
        v = new Variable(name);
        symbolTable.put(name, v);

        lexer.nextToken();
        if(lexer.token != Symbol.IN)
            error.signal("in expected");

        lexer.nextToken();
        Expr startExpr = expr();

        if(lexer.token != Symbol.DOTS)
            error.signal(".. expected");

        lexer.nextToken();
        Expr endExpr = expr();

        if(lexer.token != Symbol.LEFTBRA)
            error.signal("{ expected");

        lexer.nextToken();
        StatList statList = statList();

        if(lexer.token != Symbol.RIGHTBRA)
            error.signal("} expected");

        lexer.nextToken();

        symbolTable.remove(v);

        return new ForStat(v, startExpr, endExpr, statList);
    }

    /*
        IfStat ::= "if" Expr StatList [
                    "else" StatList ]
     */
    private Stat ifStat() {
        lexer.nextToken();

        Expr e = expr();

        if(lexer.token != Symbol.LEFTBRA)
            error.signal("{ expected");

        lexer.nextToken();
        StatList thenPart = statList();

        if(lexer.token != Symbol.RIGHTBRA)
            error.signal("} expected");
        lexer.nextToken();

        StatList elsePart = null;
        
        if(lexer.token == Symbol.ELSE) {
            lexer.nextToken();
            if(lexer.token != Symbol.LEFTBRA)
                error.signal("{ expected");

            lexer.nextToken();
            elsePart = statList();

            if(lexer.token != Symbol.RIGHTBRA)
                error.signal("} expected");
            lexer.nextToken();
        }
        
        return new IfStat(e, thenPart, elsePart);
    }

    /*
        AssignStat ::= Ident "=" Expr ";
     */
    private Stat assignmentStat() {
        String name = lexer.getStringValue();

        Variable v = symbolTable.get(name);
        if(v == null)
            error.signal("Variable " + name + " was not declared");
        lexer.nextToken();

        if(lexer.token != Symbol.ASSIGN)
            error.signal("= expected");
        lexer.nextToken();

        Expr e = expr();

        if(lexer.token != Symbol.SEMICOLON)
            error.signal("; expected");

        lexer.nextToken();

        return new AssignStat(v, e);
    }

    /*
        Expr ::= AndExpr [ "||" AndExpr ]
    */
    private Expr expr() {
        Expr left, right;
        left = andExpr();
        if(lexer.token == Symbol.OR) {
            lexer.nextToken();
            right = andExpr();
            left = new CompositeExpr(left, Symbol.OR, right);
        }
        return left;
    }

    /*
        AndExpr ::= RelExpr [ "&&" RelExpr ]
     */
    private Expr andExpr() {
        Expr left, right;
        left = relExpr();
        if(lexer.token == Symbol.AND) {
            lexer.nextToken();
            right = relExpr();
            left = new CompositeExpr(left, Symbol.AND, right);
        }
        return left;
    }

    /*
         RelExpr ::= AddExpr [ RelOp AddExpr ]
     */
    private Expr relExpr() {
        Expr left, right;
        left = addExpr();
        Symbol op = lexer.token;
        if(op == Symbol.LT || op == Symbol.LE || op == Symbol.GT || op == Symbol.GE || op == Symbol.EQ || op == Symbol.NEQ) {
            lexer.nextToken();
            right = addExpr();
            left = new CompositeExpr(left, op, right);
        }
        return left;
    }

    /*
        AddExpr ::= MultExpr { AddOp MultExpr }
     */
    private Expr addExpr() {
        Expr left, right;
        left = multExpr();
        Symbol op = lexer.token;
        while(op == Symbol.PLUS || op == Symbol.MINUS) {
            lexer.nextToken();
            right = multExpr();
            left = new CompositeExpr(left, op, right);

            op = lexer.token;
        }
        return left;
    }

    /*
        MultExpr ::= SimpleExpr { MultOp SimpleExpr }
     */
    private Expr multExpr() {
        Expr left, right;
        left = simpleExpr();
        Symbol op = lexer.token;
        while(op == Symbol.MULT || op == Symbol.DIV || op == Symbol.REMAINDER) {
            lexer.nextToken();
            right = simpleExpr();
            left = new CompositeExpr(left, op, right);

            op = lexer.token;
        }
        return left;
    }

    /*
        SimpleExpr ::= Number | ’(’ Expr ’)’ | "!" SimpleExpr
                        | AddOp SimpleExpr
     */
    private Expr simpleExpr() {
        Expr e;

        switch(lexer.token) {
            case NUMBER:
                return number();
            case LEFTPAR:
                lexer.nextToken();
                e = expr();
                if(lexer.token != Symbol.RIGHTPAR)
                    error.signal(") expected");
                lexer.nextToken();
                return e;
            case NOT:
                lexer.nextToken();
                e = expr();
                return new UnaryExpr(e, Symbol.NOT);
            case PLUS:
                lexer.nextToken();
                e = expr();
                return new UnaryExpr(e, Symbol.PLUS);
            case MINUS:
                lexer.nextToken();
                e = expr();
                return new UnaryExpr(e, Symbol.MINUS);
            default:
                if(lexer.token != Symbol.IDENT)
                    error.signal("Identifier expected");
                lexer.nextToken();
                String name = lexer.getStringValue();
                Variable v = symbolTable.get(name);
                if(v == null)
                    error.signal("Variable " + name + " was not declared");
                return new VariableExpr(v);
        }
    }

    private Expr number() {
        int value = lexer.getNumberValue();
        lexer.nextToken();

        return new NumberExpr(value);
    }

    /*
        VarList ::= { "var" Int Ident ";" }
     */
    private ArrayList<Variable> varDecList() {
        ArrayList<Variable> varList = new ArrayList<Variable>();

        while(lexer.token == Symbol.VAR) {
            lexer.nextToken();
            if(lexer.token != Symbol.INTEGER)
                error.signal("Incorrect type");

            lexer.nextToken();
            if(lexer.token != Symbol.IDENT)
                error.signal("Identifier expected");
            String name = lexer.getStringValue();
            if(symbolTable.get(name) != null)
                error.signal("Variable " + name + "has already been declared");
            Variable v = new Variable(name);

            // insere na tabela de simbolos e no array de variaveis
            symbolTable.put(name, v);
            varList.add(v);

            lexer.nextToken();
            if(lexer.token != Symbol.SEMICOLON)
                error.signal("; expected");

            lexer.nextToken();
        }

        return varList;
    }

    /*
    private void error.signal(String msg) {
        if ( lexer.tokenPos == 0 )
            lexer.tokenPos = 1;
        else
        if ( lexer.tokenPos >= input.length )
            lexer.tokenPos = input.length;

        String strInput = new String( input, lexer.tokenPos - 1, input.length - lexer.tokenPos + 1 );
        String strError = "Error at \"" + strInput + "\"";
        System.out.print( strError );
        // throws an exception: terminate the program
        throw new RuntimeException(strError);
    }*/
}
