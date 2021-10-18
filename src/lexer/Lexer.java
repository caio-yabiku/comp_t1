package lexer;

import ast.CompilerError;

import java.util.Hashtable;

public class Lexer {
    public Symbol token;

    private int tokenPos;
    private int lastTokenPos;
    private int lineNumber;

    private char []input;

    private String stringValue;
    private int numberValue;

    private CompilerError error;

    static private Hashtable<String, Symbol> keywordsTable;

    static {
        keywordsTable = new Hashtable<String, Symbol>();
        keywordsTable.put( "var", Symbol.VAR );
        keywordsTable.put( "if", Symbol.IF );
        keywordsTable.put( "else", Symbol.ELSE );
        keywordsTable.put( "for", Symbol.FOR );
        keywordsTable.put( "in", Symbol.IN );
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

    public Lexer( char []input, CompilerError error ) {
        this.input = input;
        // add an end-of-file label to make it easy to do the lexer
        input[input.length - 1] = '\0';
        // number of the current line
        lineNumber = 1;
        tokenPos = 0;
        this.error = error;
    }

    public void nextToken() {
        char ch;

        while((ch = input[tokenPos]) == ' ' || ch == '\r' ||
                ch == '\t' || ch == '\n')  {
            if(ch == '\n')
                lineNumber++;
            tokenPos++;
        }
        if(ch == '\0')
            token = Symbol.EOF;
        else {
            // Keywords e Identificadores
            if(Character.isLetter(input[tokenPos])) {
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
            else if(Character.isDigit(input[tokenPos])) {
                StringBuffer number = new StringBuffer();
                while(Character.isDigit(input[tokenPos])) {
                    number.append(input[tokenPos]);
                    tokenPos++;
                }
                token = Symbol.NUMBER;
                try {
                    numberValue = Integer.parseInt(number.toString());
                } catch (NumberFormatException e) {
                    error.signal("number expected");
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
                    case '|':
                        if ( input[tokenPos] == '|' ) {
                            tokenPos++;
                            token = Symbol.OR;
                        }
                        else
                            error.signal("Invalid Character: '" + ch + "'");
                        break;
                    case '&':
                        if ( input[tokenPos] == '&' ) {
                            tokenPos++;
                            token = Symbol.AND;
                        }
                        else
                            error.signal("Invalid Character: '" + ch + "'");
                        break;
                    case '(' :
                        token = Symbol.LEFTPAR;
                        break;
                    case ')' :
                        token = Symbol.RIGHTPAR;
                        break;
                    case '{' :
                        token = Symbol.LEFTBRA;
                        break;
                    case '}' :
                        token = Symbol.RIGHTBRA;
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
                        error.signal("Invalid Character: '" + ch + "'");
                        break;
                }
            }
        }
        lastTokenPos = tokenPos - 1;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getStringValue() {
        return stringValue;
    }

    public int getNumberValue() {
        return numberValue;
    }

    public String getCurrentLine() {
        int i = lastTokenPos;
        if ( i == 0 )
            i = 1;
        else
        if ( i >= input.length )
            i = input.length;

        StringBuffer line = new StringBuffer();
        // go to the beginning of the line
        while ( i >= 1 && input[i] != '\n' )
            i--;
        if ( input[i] == '\n' )
            i++;
        // go to the end of the line putting it in variable line
        while ( input[i] != '\0' && input[i] != '\n' && input[i] != '\r' ) {
            line.append( input[i] );
            i++;
        }
        return line.toString();
    }
}
