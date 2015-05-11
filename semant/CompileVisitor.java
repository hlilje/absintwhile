package semant;

import semant.amsyntax.*;
import semant.whilesyntax.*;

public class CompileVisitor implements WhileVisitor {

    // Unique id to be increased for each control point
    private int controlPoint = 0;

    public Code visit(Compound compound) {
        Code c = new Code();
        c.addAll(compound.s1.accept(this));
        c.addAll(compound.s2.accept(this));
        return c;
    }

    public Code visit(Not not) {
        Code c = new Code();
        Inst inst = new Neg();
        inst.stmControlPoint = controlPoint;
        c.addAll(not.b.accept(this));
        c.add(inst);
        return c;
    }

    public Code visit(Conjunction and) {
        Code c = new Code();
        Inst inst = new And();
        inst.stmControlPoint = controlPoint;
        c.addAll(and.b2.accept(this));
        c.addAll(and.b1.accept(this));
        c.add(inst);
        return c;
    }

    public Code visit(Assignment assignment) {
        Code c = new Code();
        Inst inst = new Store(assignment.x.id);
        ++controlPoint;
        assignment.controlPoint = controlPoint;
        inst.stmControlPoint = assignment.controlPoint;
        c.addAll(assignment.a.accept(this));
        c.add(inst);
        return c;
    }

    public Code visit(Conditional conditional) {
        Code c = new Code();
        ++controlPoint;
        conditional.controlPoint = controlPoint;
        c.addAll(conditional.b.accept(this));
        Inst inst = new Branch(conditional.s1.accept(this),
                conditional.s2.accept(this));
        inst.stmControlPoint = conditional.controlPoint;
        c.add(inst);
        return c;
    }

    public Code visit(Equals equals) {
        Code c = new Code();
        Inst inst = new Eq();
        inst.stmControlPoint = controlPoint;
        c.addAll(equals.a2.accept(this));
        c.addAll(equals.a1.accept(this));
        c.add(inst);
        return c;
    }

    public Code visit(FalseConst f) {
        Code c = new Code();
        Inst inst = new False();
        inst.stmControlPoint = controlPoint;
        c.add(inst);
        return c;
    }

    public Code visit(LessThanEq lessthaneq) {
        Code c = new Code();
        Inst inst = new Le();
        inst.stmControlPoint = controlPoint;
        // Not commutative
        c.addAll(lessthaneq.a2.accept(this));
        c.addAll(lessthaneq.a1.accept(this));
        c.add(inst);
        return c;
    }

    public Code visit(Minus minus) {
        Code c = new Code();
        Inst inst = new Sub();
        inst.stmControlPoint = controlPoint;
        // Not commutative
        c.addAll(minus.a2.accept(this));
        c.addAll(minus.a1.accept(this));
        c.add(inst);
        return c;
    }

    public Code visit(Num num) {
        Code c = new Code();
        Inst inst = new Push(num.n);
        inst.stmControlPoint = controlPoint;
        c.add(inst);
        return c;
    }

    public Code visit(Plus plus) {
        Code c = new Code();
        Inst inst = new Add();
        inst.stmControlPoint = controlPoint;
        c.addAll(plus.a2.accept(this));
        c.addAll(plus.a1.accept(this));
        c.add(inst);
        return c;
    }

    public Code visit(Skip skip) {
        Code c = new Code();
        Inst inst = new Noop();
        skip.controlPoint = controlPoint;
        inst.stmControlPoint = controlPoint;
        c.add(inst);
        return c;
    }

    public Code visit(Times times) {
        Code c = new Code();
        Inst inst = new Mult();
        inst.stmControlPoint = controlPoint;
        c.addAll(times.a2.accept(this));
        c.addAll(times.a1.accept(this));
        c.add(inst);
        return c;
    }

    public Code visit(TrueConst t) {
        Code c = new Code();
        Inst inst = new True();
        inst.stmControlPoint = controlPoint;
        c.add(inst);
        return c;
    }

    public Code visit(Var var) {
        Code c = new Code();
        Inst inst = new Fetch(var.id);
        inst.stmControlPoint = controlPoint;
        c.add(inst);
        return c;
    }

    public Code visit(While whyle) {
        Code c = new Code();
        ++controlPoint;
        whyle.controlPoint = controlPoint;
        Inst inst = new Loop(whyle.b.accept(this), whyle.s.accept(this));
        inst.stmControlPoint = whyle.controlPoint;
        c.add(inst);
        return c;
    }

    public Code visit(TryCatch trycatch) {
        Code c = new Code();
        ++controlPoint;
        trycatch.controlPoint = controlPoint;
        Inst inst = new Try(trycatch.s1.accept(this), trycatch.s2.accept(this));
        inst.stmControlPoint = trycatch.controlPoint;
        c.add(inst);
        return c;
    }

    public Code visit(Divide div) {
        Code c = new Code();
        Inst inst = new Div();
        inst.stmControlPoint = controlPoint;
        // Not commutative
        c.addAll(div.a2.accept(this));
        c.addAll(div.a1.accept(this));
        c.add(inst);
        return c;
    }
}
