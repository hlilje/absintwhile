package semant.whilesyntax;

import semant.WhileVisitor;
import semant.amsyntax.Code;

public class Not extends Bexp {

    public final Bexp b;
    
    public Not(Bexp b) {
        this.b = b;
    }
    
    public Code accept(WhileVisitor v) {
        return v.visit(this);
    }
}
