package semant;

import semant.amsyntax.Code;
import semant.whilesyntax.*;

public class PrettyPrinter implements WhileVisitor {

    String i = "";

    public Code visit(Conjunction and) {
        and.b1.accept(this);
        System.out.print(" & ");
        and.b2.accept(this);
        return null;
    }

    public Code visit(Assignment assignment) {
        System.out.println();
        System.out.print(i);
        assignment.x.accept(this);
        System.out.print(" := ");
        assignment.a.accept(this);
        return null;
    }

    public Code visit(Compound compound) {
        compound.s1.accept(this);
        System.out.print(";");
        compound.s2.accept(this);
        return null;
    }

    public Code visit(Conditional conditional) {
        System.out.println();
        System.out.print(i + "if ");
        conditional.b.accept(this);
        System.out.print(" then");
        indent();
        conditional.s1.accept(this);
        outdent();
        System.out.println();
        System.out.print(i + "else");
        indent();
        conditional.s2.accept(this);
        outdent();
        return null;
    }

    public Code visit(Equals equals) {
        equals.a1.accept(this);
        System.out.print(" = ");
        equals.a2.accept(this);
        return null;
    }

    public Code visit(FalseConst f) {
        System.out.print("false");
        return null;
    }

    public Code visit(LessThanEq leq) {
        leq.a1.accept(this);
        System.out.print(" <= ");
        leq.a2.accept(this);
        return null;
    }

    public Code visit(Minus minus) {
        System.out.print("(");
        minus.a1.accept(this);
        System.out.print(" - ");
        minus.a2.accept(this);
        System.out.print(")");
        return null;
    }

    public Code visit(Not not) {
        System.out.print("!(");
        not.b.accept(this);
        System.out.print(")");
        return null;
    }

    public Code visit(Num num) {
        System.out.print(num.n);
        return null;
    }

    public Code visit(Plus plus) {
        System.out.print("(");
        plus.a1.accept(this);
        System.out.print(" + ");
        plus.a2.accept(this);
        System.out.print(")");
        return null;
    }

    public Code visit(Skip skip) {
        System.out.println();
        System.out.print(i + "skip");
        return null;
    }

    public Code visit(Times times) {
        System.out.print("(");
        times.a1.accept(this);
        System.out.print(" * ");
        times.a2.accept(this);
        System.out.print(")");
        return null;
    }

    public Code visit(TrueConst t) {
        System.out.print("true");
        return null;
    }

    public Code visit(Var var) {
        System.out.print(var.id);
        return null;
    }

    public Code visit(While whyle) {
        System.out.println();
        System.out.print(i + "while ");
        whyle.b.accept(this);
        System.out.print(" do");
        indent();
        whyle.s.accept(this);
        outdent();
        return null;
    }

    public Code visit(Divide div) {
        System.out.print("(");
        div.a1.accept(this);
        System.out.print(" / ");
        div.a2.accept(this);
        System.out.print(")");
        return null;
    }

    public Code visit(TryCatch trycatch) {
        System.out.println();
        System.out.print(i + "try");
        indent();
        trycatch.s1.accept(this);
        outdent();
        System.out.println();
        System.out.print(i + "catch");
        indent();
        trycatch.s2.accept(this);
        outdent();
        return null;
    }

    private void indent() {
        i += "    ";
    }

    private void outdent() {
        i = i.substring(4);
    }
}
