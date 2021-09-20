package ast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class StatList {
    private ArrayList<Stat> v;

    public StatList(ArrayList<Stat> v) {
        this.v = v;
    }

    public void genC(PW pw) {
        for(Stat s : v)
            s.genC(pw);
    }
}
