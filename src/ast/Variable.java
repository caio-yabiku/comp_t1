package ast;

public class Variable {
    private String name;
    private int value;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void genC(PW pw) {

    }
}
