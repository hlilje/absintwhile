package semant.whilesyntax;

import semant.WhileVisitor;
import semant.amsyntax.Code;

public class TryCatch extends Stm {

    public final Stm s1, s2;
    
    public TryCatch(Stm s1, Stm s2) {
        this.s1 = s1;
        this.s2 = s2;
    }
    
    public Code accept(WhileVisitor v) {
        return v.visit(this);
    }
    
}
