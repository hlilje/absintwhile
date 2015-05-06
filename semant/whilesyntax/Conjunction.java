package semant.whilesyntax;

import semant.WhileVisitor;
import semant.amsyntax.Code;

public class Conjunction extends Bexp {
    
    public final Bexp b1;
    public final Bexp b2;
    
    public Conjunction(Bexp b1, Bexp b2) {
        this.b1 = b1;
        this.b2 = b2;
    }
    
    public Code accept(WhileVisitor v) {
        return v.visit(this);
    }
}
