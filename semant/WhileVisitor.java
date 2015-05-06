package semant;

import semant.amsyntax.*;
import semant.whilesyntax.*;

public interface WhileVisitor {
    public Code visit(Compound compound);
    public Code visit(Not not);
    public Code visit(Conjunction and);
    public Code visit(Assignment assignment);
    public Code visit(Conditional conditional);
    public Code visit(Equals equals);
    public Code visit(FalseConst f);
    public Code visit(LessThanEq lessthaneq);
    public Code visit(Minus minus);
    public Code visit(Num num);
    public Code visit(Plus plus);
    public Code visit(Skip skip);
    public Code visit(Times times);
    public Code visit(TrueConst t);
    public Code visit(Var var);
    public Code visit(While whyle);
    public Code visit(TryCatch trycatch);
    public Code visit(Divide div);
}
