package semant.amsyntax;


public class Branch extends Inst {

    public final Code c1, c2;
    
    public Branch(Code c1, Code c2) {
        super(Opcode.BRANCH);
        this.c1 = c1;
        this.c2 = c2;
    }
    
    public String toString() {
        return super.toString() + "(" + c1 + ", " + c2 + ")";
    }
}
