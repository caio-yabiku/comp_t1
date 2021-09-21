package ast;

public class Variable {
    private String name;
    private int value;

    public Variable(String name) {
        this.name = name;
        this.value = 0;
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void genC(PW pw) {
    }
}
