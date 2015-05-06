package semant.whilesyntax;

import semant.WhileVisitor;
import semant.amsyntax.Code;

public class Skip extends Stm {
    
    public Code accept(WhileVisitor v) {
        return v.visit(this);
    }
}
