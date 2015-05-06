package semant.whilesyntax;

import semant.WhileVisitor;
import semant.amsyntax.Code;

public abstract class Aexp {
    public abstract Code accept(WhileVisitor v);
}
