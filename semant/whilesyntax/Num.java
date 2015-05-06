package semant.whilesyntax;

import semant.WhileVisitor;
import semant.amsyntax.Code;

public class Num extends Aexp {
    
    public final String n;
    
    public Num(String n) {
        this.n = n;
    }

    public Code accept(WhileVisitor v) {
        return v.visit(this);
    }

}
