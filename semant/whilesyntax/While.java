package semant.whilesyntax;

import semant.WhileVisitor;
import semant.amsyntax.Code;

public class While extends Stm {

    public final Bexp b;
    public final Stm s;
    
    public While(Bexp b, Stm s) {
        this.b = b;
        this.s = s;
    }
    public Code accept(WhileVisitor v) {
        return v.visit(this);
    }

}
