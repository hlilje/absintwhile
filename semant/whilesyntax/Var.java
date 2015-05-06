package semant.whilesyntax;

import semant.WhileVisitor;
import semant.amsyntax.Code;

public class Var extends Aexp {

    public final String id;
    
    public Var(String id) {
        this.id = id;
    }
    
    public Code accept(WhileVisitor v) {
        return v.visit(this);
    }
}
