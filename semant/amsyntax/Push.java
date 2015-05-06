package semant.amsyntax;

public class Push extends Inst {
    
    public final String n;
    
    public Push(String n) {
        super(Opcode.PUSH);
        this.n = n;
    }
    
    public int getValue() {
        return Integer.parseInt(n);
    }
    
    
    public String toString() {
        return super.toString() + "-" + n;
    }
}
