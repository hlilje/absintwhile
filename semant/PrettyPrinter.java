package semant;

import semant.amsyntax.Code;
import semant.signexc.SignExc;
import semant.signexc.TTExc;
import semant.whilesyntax.*;
import semant.signexc.*;
import java.util.HashMap;
import java.util.Map;

public class PrettyPrinter implements WhileVisitor {

    private VM vm;

    String i = "";

    public PrettyPrinter(VM vm) {
        this.vm = vm;
    }

    public Code visit(Conjunction and) {
        and.b1.accept(this);
        System.out.print(" & ");
        and.b2.accept(this);
        return null;
    }

    public Code visit(Assignment assignment) {
        printVars(assignment.controlPoint-1);
        SignExc a = vm.getZLubs()[assignment.controlPoint - 1];
        String s = a != null ? a.toString() : "never evaluated";
        System.out.print(" Right-hand side: " + s);
        if (a == SignExc.ERR_A)
            System.out.print(" (Exception raiser!)");
        if (a == SignExc.ANY_A)
            System.out.print(" (Possible exception raiser!)");
        if (a == SignExc.NONE_A)
            System.out.print(" (Use of uninitialised variable!)");
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
        printVars(conditional.controlPoint-1);
        TTExc b = vm.getTTLubs()[conditional.controlPoint - 1];
        System.out.print(" Boolean guard: " + b);
        if (b == TTExc.FF || b == TTExc.TT)
            System.out.print(" (Unreachable code!)");
        if (b == TTExc.ERR_B)
            System.out.print(" (Exception raiser!)");
        if (b == TTExc.ANY_B)
            System.out.print(" (Possible exception raiser!)");
        if (b == TTExc.NONE_B)
            System.out.print(" (Use of uninitialised variable!)");
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
        printVars(whyle.controlPoint-1);
        TTExc b = vm.getTTLubs()[whyle.controlPoint - 1];
        System.out.print(" Boolean guard: " + b);
        if (b == TTExc.FF)
            System.out.print(" (Unreachable code!)");
        if (b == TTExc.ERR_B)
            System.out.print(" (Exception raiser!)");
        if (b == TTExc.ANY_B)
            System.out.print(" (Possible exception raiser!)");
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
        printVars(trycatch.controlPoint-1);
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

    private void printVars(int cp) {
        HashMap<String, SignExc> vars = vm.getVarLubs()[cp];
        StringBuilder sb = new StringBuilder();
        sb.append(i + "{");

        for (Map.Entry<String, SignExc> e : vars.entrySet())
            sb.append(e.getKey() + "=" + e.getValue() + ", ");
        if (!vars.isEmpty()) sb.setLength(sb.length()-2);
        sb.append("}");
        System.out.println();
        System.out.print(sb);
    }

    public void printTermination() {
        printVars(vm.lastControlPoint()-1);
        if (vm.possiblyNormalTermination()) {
            if(vm.possiblyExceptionalTermination())
                System.out.print(" (possibly exceptional termination");
            else
                System.out.print(" (normal termination)");
        } else {
            if(vm.possiblyExceptionalTermination())
                System.out.print(" (exceptional termination)");
            else
                System.out.print(" (no termination)");
        }
    }
}
