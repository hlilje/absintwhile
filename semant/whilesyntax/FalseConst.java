package semant.whilesyntax;

import semant.WhileVisitor;
import semant.amsyntax.Code;

public class FalseConst extends Bexp {
    
    public Code accept(WhileVisitor v) {
        return v.visit(this);
    }
}
