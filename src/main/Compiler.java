package main;

import ast.*;
import lexer.Symbol;

import java.util.ArrayList;
import java.util.Hashtable;

public class Compiler {
    private Symbol token;
    private int  tokenPos;
    private char []input;
    private Hashtable<String, Variable> symbolTable;

    static private Hashtable<String, Symbol> keywordsTable;

    private String stringValue;
    private int numberValue;

    static {
        keywordsTable = new Hashtable<String, Symbol>();
        keywordsTable.put( "var", Symbol.VAR );
        keywordsTable.put( "if", Symbol.IF );
        keywordsTable.put( "else", Symbol.ELSE );
        keywordsTable.put( "for", Symbol.FOR );
        keywordsTable.put( "while", Symbol.WHILE );
        keywordsTable.put( "print", Symbol.PRINT );
        keywordsTable.put( "println", Symbol.PRINTLN );
        keywordsTable.put( "Int", Symbol.INTEGER );
        keywordsTable.put( "true", Symbol.TRUE );
        keywordsTable.put( "false", Symbol.FALSE );
        keywordsTable.put( "&&", Symbol.AND );
        keywordsTable.put( "||", Symbol.OR );
        keywordsTable.put( "!", Symbol.NOT );
    }

    public Program compile(char []p_input) {
        input = p_input;
        tokenPos = 0;
        symbolTable = new Hashtable<String, Variable>();
        nextToken();
        return program();
    }

    private Program program() {
        ArrayList<Variable> arrayVariable = null;

        if(token == Symbol.VAR) {
            nextToken();
            arrayVariable = varDecList();
        }
        StatList sl = statList();

        Program program = new Program(arrayVariable, sl);

        /*
        if(token != Symbol.EOF)
            error("EOF expected");
         */

        return program;

        /*
        if(token == Symbol.IDENT || token == Symbol.IF || token == Symbol.FOR|| token == Symbol.PRINT || token == Symbol.PRINTLN || token == Symbol.WHILE) {
            nextToken();
            arrayStat = statList();
        }*/

    }

    private StatList statList() {
        ArrayList<Stat> v = new ArrayList<Stat>();

        while(  token == Symbol.IDENT ||
                token == Symbol.IF ||
                token == Symbol.FOR||
                token == Symbol.PRINT ||
                token == Symbol.PRINTLN ||
                token == Symbol.WHILE) {
            v.add(stat());
            if(token != Symbol.SEMICOLON)
                error("; expected");
            nextToken();
        }

        return new StatList(v);
    }

    private Stat stat() {
        /*
            Stat ::= AssignStat | IfStat | ForStat | PrintStat |
                    PrintlnStat | WhileStat
         */
        switch(token) {
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
                error("Stat expected");
        }
        return null;
    }

    private Stat whileStat() {
        /*
            WhileStat ::= "while" Expr StatList
         */
        nextToken();

        Expr e = expr();
        StatList statList = statList();

        return new WhileStat(e, statList);
    }

    private Stat printlnStat() {
        /*
            PrintlnStat ::= "println" Expr ";"
         */
        nextToken();

        Expr e = expr();

        return new PrintlnStat(e);
    }

    private Stat printStat() {
        /*
            PrintStat ::= "print" Expr ";"
         */
        nextToken();
        
        Expr e = expr();
        
        return new PrintStat(e);
    }

    private Stat forStat() {
        /*
            ForStat ::= "for" Id "in" Expr ".." Expr StatList
         */
        nextToken();
        if(token != Symbol.IDENT)
            error("Ident expected");
        String name = stringValue;
        Variable v = symbolTable.get(name);
        if(v != null)
            error("variable was already declared");
        v = new Variable(name);

        nextToken();
        if(token != Symbol.IN)
            error("in expected");

        nextToken();
        Expr startExpr = expr();

        if(token != Symbol.DOTS)
            error(".. expected");

        nextToken();
        Expr endExpr = expr();

        StatList statList = statList();

        return new ForStat(v, startExpr, endExpr, statList);
    }

    private Stat ifStat() {
        /*
            IfStat ::= "if" Expr StatList [
                        "else" StatList ]
         */
        nextToken();

        Expr e = expr();
        
        StatList thenPart = statList();
        StatList elsePart = null;
        
        if(token == Symbol.ELSE) {
            nextToken();
            elsePart = statList();
        }
        
        return new IfStat(e, thenPart, elsePart);
    }

    private Stat assignmentStat() {
        /*
            AssignStat ::= Ident "=" Expr ";
         */
        String name = stringValue;

        Variable v = symbolTable.get(name);
        if(v == null)
            error("Variable " + name + " was not declared");
        nextToken();

        if(token != Symbol.ASSIGN)
            error("= expected");
        nextToken();

        Expr e = expr();

        /*
        if(token != Symbol.SEMICOLON)
            error("; expected");            
         */

        return new AssignStat(v, e);
    }

    private Expr expr() {
        /*
            Expr ::= AndExpr [ "||" AndExpr ]
        */
        Expr left, right;
        left = andExpr();
        if(token == Symbol.OR) {
            nextToken();
            right = andExpr();
            left = new CompositeExpr(left, Symbol.OR, right);
        }
        return left;
    }

    private Expr andExpr() {
        /*
            AndExpr ::= RelExpr [ "&&" RelExpr ]
         */
        Expr left, right;
        left = relExpr();
        if(token == Symbol.AND) {
            nextToken();
            right = relExpr();
            left = new CompositeExpr(left, Symbol.AND, right);
        }
        return left;
    }

    private Expr relExpr() {
        /*
             RelExpr ::= AddExpr [ RelOp AddExpr ]
         */
        Expr left, right;
        left = addExpr();
        Symbol op = token;
        if(op == Symbol.LT || op == Symbol.LE || op == Symbol.GT || op == Symbol.GE || op == Symbol.EQ || op == Symbol.NEQ) {
            nextToken();
            right = addExpr();
            left = new CompositeExpr(left, op, right);
        }
        return left;
    }

    private Expr addExpr() {
        /*
            AddExpr ::= MultExpr { AddOp MultExpr }
         */
        Expr left, right;
        left = multExpr();
        Symbol op = token;
        if(op == Symbol.PLUS || op == Symbol.MINUS) {
            nextToken();
            right = multExpr();
            left = new CompositeExpr(left, op, right);
        }
        return left;
    }

    private Expr multExpr() {
        /*
            MultExpr ::= SimpleExpr { MultOp SimpleExpr }
         */
        Expr left, right;
        left = simpleExpr();
        Symbol op = token;
        if(op == Symbol.MULT || op == Symbol.DIV || op == Symbol.REMAINDER) {
            nextToken();
            right = simpleExpr();
            left = new CompositeExpr(left, op, right);
        }
        return left;
    }

    private Expr simpleExpr() {
        /*
            SimpleExpr ::= Number | ’(’ Expr ’)’ | "!" SimpleExpr
                            | AddOp SimpleExpr
         */
        Expr e;

        switch(token) {
            case NUMBER:
                return number();
            case LEFTPAR:
                nextToken();
                e = expr();
                if(token != Symbol.RIGHTPAR)
                    error(") expected");
                nextToken();
                return e;
            case NOT:
                nextToken();
                e = simpleExpr();
                return new UnaryExpr(e, Symbol.NOT);
            case PLUS:
                nextToken();
                e = simpleExpr();
                return new UnaryExpr(e, Symbol.PLUS);
            case MINUS:
                nextToken();
                e = simpleExpr();
                return new UnaryExpr(e, Symbol.MINUS);
            default:
                if(token != Symbol.IDENT)
                    error("Identifier expected");
                nextToken();
                String name = stringValue;
                Variable v = symbolTable.get(name);
                if(v == null)
                    error("Variable " + name + " was not declared");
                return new VariableExpr(v);
        }
    }

    private Expr number() {
        int value = numberValue;
        nextToken();
        return new NumberExpr(value);
    }

    private ArrayList<Variable> varDecList() {
        ArrayList<Variable> varList = new ArrayList<Variable>();

        do {
            //nextToken();
            if(token != Symbol.INTEGER)
                error("Incorrect type");

            nextToken();
            if(token != Symbol.IDENT)
                error("Identifier expected");
            String name = stringValue;
            if(symbolTable.get(name) != null)
                error("Variable " + name + "has already been declared");
            Variable v = new Variable(name);

            // insere na tabela de simbolos e no array de variaveis
            symbolTable.put(name, v);
            varList.add(v);

            nextToken();
            if(token != Symbol.SEMICOLON)
                error("; expected");

            nextToken();
        } while(token == Symbol.VAR);

        return varList;
    }

    private void nextToken() {
        char ch;
        if(tokenPos == input.length) {
            token = Symbol.EOF;
            return;
        }

        while((ch = input[tokenPos]) == ' ') {
            tokenPos++;
        }
        if(ch == '\0')
            token = Symbol.EOF;
        else {
            // Keywords e Identificadores
            if(Character.isLetter(ch)) {
                StringBuffer ident = new StringBuffer();
                while(Character.isLetter(input[tokenPos]) || Character.isDigit(input[tokenPos])) {
                    ident.append(input[tokenPos]);
                    tokenPos++;
                }
                stringValue = ident.toString();
                Symbol value = keywordsTable.get(stringValue);
                if(value == null)
                    token = Symbol.IDENT;
                else
                    token = value;
            }
            // Numeros
            else if(Character.isDigit(ch)) {
                StringBuffer number = new StringBuffer();
                while(Character.isDigit(input[tokenPos])) {
                    number.append(ch);
                    tokenPos++;
                }
                token = Symbol.NUMBER;
                try {
                    numberValue = Integer.parseInt(number.toString());
                } catch (NumberFormatException e) {
                    error("number expected");
                }
            }
            else {
                tokenPos++;
                switch(ch) {
                    case '+' :
                        token = Symbol.PLUS;
                        break;
                    case '-' :
                        token = Symbol.MINUS;
                        break;
                    case '*' :
                        token = Symbol.MULT;
                        break;
                    case '/' :
                        token = Symbol.DIV;
                        break;
                    case '%' :
                        token = Symbol.REMAINDER;
                        break;
                    case '<' :
                        if ( input[tokenPos] == '=' ) {
                            tokenPos++;
                            token = Symbol.LE;
                        }
                        else
                            token = Symbol.LT;
                        break;
                    case '>' :
                        if ( input[tokenPos] == '=' ) {
                            tokenPos++;
                            token = Symbol.GE;
                        }
                        else
                            token = Symbol.GT;
                        break;
                    case '=' :
                        if ( input[tokenPos] == '=' ) {
                            tokenPos++;
                            token = Symbol.EQ;
                        }
                        else
                            token = Symbol.ASSIGN;
                        break;
                    case '!':
                        if ( input[tokenPos] == '=' ) {
                            tokenPos++;
                            token = Symbol.NEQ;
                        }
                        else
                            token = Symbol.NOT;
                        break;
                    case '(' :
                        token = Symbol.LEFTPAR;
                        break;
                    case ')' :
                        token = Symbol.RIGHTPAR;
                        break;
                    case ';' :
                        token = Symbol.SEMICOLON;
                        break;
                    case '.':
                        if ( input[tokenPos] == '.' ) {
                            tokenPos++;
                            token = Symbol.DOTS;
                            break;
                        }
                    default:
                        error("Invalid Character: '" + ch + "'");
                        break;
                }
            }
        }
    }

    private void error(String msg) {
        if ( tokenPos == 0 )
            tokenPos = 1;
        else
        if ( tokenPos >= input.length )
            tokenPos = input.length;

        String strInput = new String( input, tokenPos - 1, input.length - tokenPos + 1 );
        String strError = "Error at \"" + strInput + "\"";
        System.out.print( strError );
        // throws an exception: terminate the program
        throw new RuntimeException(strError);
    }
}
